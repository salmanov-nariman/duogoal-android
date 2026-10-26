package com.example.duogoal_android.data.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.security.KeyStore
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private val Context.authDataStore by preferencesDataStore("secure_auth")

class EncryptedSessionStore(private val dataStore: DataStore<Preferences>) : SessionStore {
    constructor(context: Context) : this(context.applicationContext.authDataStore)
    private val accessKey = stringPreferencesKey("access_token")
    private val refreshKey = stringPreferencesKey("refresh_token")
    private val navigationKey = stringPreferencesKey("navigation_key")
    private val cryptor = TokenCryptor()

    override suspend fun read(): StoredSession? = withContext(Dispatchers.IO) {
        val preferences = dataStore.data.first()
        val access = preferences[accessKey]
        val refresh = preferences[refreshKey]
        if (access == null && refresh == null) return@withContext null
        if (access == null || refresh == null) throw InvalidStoredSessionException()
        try {
            val session = AuthSession(cryptor.decrypt(access), cryptor.decrypt(refresh))
            if (session.accessToken.isBlank() || session.refreshToken.isBlank()) throw InvalidStoredSessionException()
            StoredSession(session, preferences[navigationKey] ?: UUID.randomUUID().toString())
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            throw InvalidStoredSessionException()
        }
    }

    override suspend fun write(session: StoredSession) = withContext(Dispatchers.IO) {
        // Шифруем до транзакции, затем сохраняем обе части ротации вместе.
        val access = cryptor.encrypt(session.tokens.accessToken)
        val refresh = cryptor.encrypt(session.tokens.refreshToken)
        dataStore.edit { preferences ->
            preferences[accessKey] = access
            preferences[refreshKey] = refresh
            preferences[navigationKey] = session.navigationKey
        }
        Unit
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}

private class TokenCryptor {
    private val alias = "duogoal_auth_key"
    private val key: SecretKey by lazy {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias, null) as? SecretKey) ?: KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(
                KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build(),
            )
        }.generateKey()
    }

    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key) }
        return Base64.encodeToString(cipher.iv + cipher.doFinal(value.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
    }

    fun decrypt(value: String): String {
        val bytes = Base64.decode(value, Base64.NO_WRAP)
        require(bytes.size > 12)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, bytes.copyOfRange(0, 12)))
        }
        return String(cipher.doFinal(bytes.copyOfRange(12, bytes.size)), Charsets.UTF_8)
    }
}
