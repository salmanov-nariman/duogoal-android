package com.example.duogoal_android.data.auth

import com.example.duogoal_android.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

/** Сеть возвращает токены, но никогда не сохраняет их сама. */
class HttpAuthApi(private val baseUrl: String = BuildConfig.API_BASE_URL) : AuthApi {
    override suspend fun login(email: String, password: String): AuthResult =
        tokens("login", JSONObject().put("email", email).put("password", password))

    override suspend fun register(username: String, email: String, password: String): AuthResult {
        when (val result = post("register", JSONObject().put("username", username).put("email", email).put("password", password))) {
            is Response.Failure -> return result.error
            is Response.Success -> Unit
        }
        return when (val login = login(email, password)) {
            is AuthResult.Success -> login
            is AuthResult.Failure -> login.copy(message = "Аккаунт создан. Не удалось войти автоматически: ${login.message}", accountCreated = true)
        }
    }

    override suspend fun refresh(refreshToken: String): AuthResult =
        tokens("refresh", JSONObject().put("refreshToken", refreshToken))

    private suspend fun tokens(path: String, payload: JSONObject): AuthResult = when (val response = post(path, payload)) {
        is Response.Failure -> response.error
        is Response.Success -> {
            val access = response.body.optString("accessToken")
            val refresh = response.body.optString("refreshToken")
            if (access.isBlank() || refresh.isBlank()) {
                AuthResult.Failure("Сервер вернул неполный ответ. Попробуйте позже.", AuthFailureKind.Protocol)
            } else AuthResult.Success(AuthSession(access, refresh))
        }
    }

    private suspend fun post(path: String, payload: JSONObject): Response = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL("${baseUrl.trimEnd('/')}/api/auth/$path").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 15_000
                doOutput = true
                // Не пересылать пароль/refresh token по перенаправлениям на другой адрес.
                instanceFollowRedirects = false
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }
            connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(payload.toString()) }
            val status = connection.responseCode
            val body = (if (status in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (status in 200..299) {
                Response.Success(if (body.isBlank()) JSONObject() else JSONObject(body))
            } else {
                val message = runCatching { JSONObject(body).optString("errorText").ifBlank { JSONObject(body).optString("message") } }.getOrDefault("")
                Response.Failure(AuthResult.Failure(message.ifBlank { "Сервер временно недоступен. Попробуйте ещё раз." }, failureKind(status)))
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: JSONException) {
            Response.Failure(AuthResult.Failure("Сервер вернул некорректный ответ. Попробуйте позже.", AuthFailureKind.Protocol))
        } catch (_: IOException) {
            Response.Failure(AuthResult.Failure("Не удалось подключиться к серверу. Проверьте интернет и повторите попытку.", AuthFailureKind.Unavailable))
        } catch (_: Exception) {
            Response.Failure(AuthResult.Failure("Не удалось выполнить запрос. Попробуйте ещё раз.", AuthFailureKind.Unavailable))
        } finally {
            connection?.disconnect()
        }
    }

    private sealed interface Response {
        data class Success(val body: JSONObject) : Response
        data class Failure(val error: AuthResult.Failure) : Response
    }
}

/** В HLT refresh с недействительным токеном возвращает 400, а validate — 401. */
internal fun failureKind(status: Int): AuthFailureKind =
    if (status in listOf(400, 401, 403)) AuthFailureKind.Rejected else AuthFailureKind.Unavailable
