package com.example.duogoal_android.data.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.duogoal_android.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val Context.authDataStore by preferencesDataStore("secure_auth")

data class AuthSession(val accessToken: String, val refreshToken: String)
sealed interface AuthResult { data class Success(val session: AuthSession) : AuthResult; data class Failure(val message: String) : AuthResult }

class AuthRepository(private val context: Context) {
    private val accessKey = stringPreferencesKey("access_token")
    private val refreshKey = stringPreferencesKey("refresh_token")
    private val cryptor = TokenCryptor()

    val session: Flow<AuthSession?> = context.authDataStore.data.map { preferences ->
        val access = preferences[accessKey]?.let(cryptor::decrypt)
        val refresh = preferences[refreshKey]?.let(cryptor::decrypt)
        if (access != null && refresh != null) AuthSession(access, refresh) else null
    }

    suspend fun login(email: String, password: String): AuthResult = requestTokens("login", JSONObject().put("email", email).put("password", password))

    suspend fun register(username: String, email: String, password: String): AuthResult {
        val result = post("register", JSONObject().put("username", username).put("email", email).put("password", password))
        return if (result is ApiResult.Error) AuthResult.Failure(result.message) else login(email, password)
    }

    suspend fun refresh(refreshToken: String): AuthResult = requestTokens("refresh", JSONObject().put("refreshToken", refreshToken))

    suspend fun clear() { context.authDataStore.edit { it.clear() } }

    private suspend fun requestTokens(path: String, payload: JSONObject): AuthResult = when (val result = post(path, payload)) {
        is ApiResult.Error -> AuthResult.Failure(result.message)
        is ApiResult.Success -> try {
            val session = AuthSession(result.body.getString("accessToken"), result.body.getString("refreshToken"))
            context.authDataStore.edit { prefs -> prefs[accessKey] = cryptor.encrypt(session.accessToken); prefs[refreshKey] = cryptor.encrypt(session.refreshToken) }
            AuthResult.Success(session)
        } catch (_: Exception) { AuthResult.Failure("Сервер вернул неполный ответ. Попробуйте позже.") }
    }

    private suspend fun post(path: String, payload: JSONObject): ApiResult = withContext(Dispatchers.IO) {
        try {
            val connection = (URL("${BuildConfig.API_BASE_URL}api/auth/$path").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"; connectTimeout = 15_000; readTimeout = 15_000; doOutput = true
                setRequestProperty("Content-Type", "application/json"); setRequestProperty("Accept", "application/json")
            }
            connection.outputStream.bufferedWriter().use { it.write(payload.toString()) }
            val body = (if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream)?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (connection.responseCode in 200..299) ApiResult.Success(JSONObject(body))
            else ApiResult.Error(runCatching { JSONObject(body).optString("errorText") }.getOrDefault("").ifBlank { "Ошибка сервера (${connection.responseCode})" })
        } catch (_: java.net.UnknownHostException) { ApiResult.Error("Нет подключения к серверу.") }
        catch (_: java.net.SocketTimeoutException) { ApiResult.Error("Сервер долго не отвечает. Попробуйте ещё раз.") }
        catch (_: Exception) { ApiResult.Error("Не удалось выполнить запрос. Попробуйте ещё раз.") }
    }

    private sealed interface ApiResult { data class Success(val body: JSONObject) : ApiResult; data class Error(val message: String) : ApiResult }
}

private class TokenCryptor {
    private val alias = "duogoal_auth_key"
    private val key: SecretKey get() {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }
    fun encrypt(value: String): String { val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key) }; return Base64.encodeToString(cipher.iv + cipher.doFinal(value.toByteArray()), Base64.NO_WRAP) }
    fun decrypt(value: String): String? = runCatching { val bytes = Base64.decode(value, Base64.NO_WRAP); val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, bytes.copyOfRange(0, 12))) }; String(cipher.doFinal(bytes.copyOfRange(12, bytes.size))) }.getOrNull()
}
