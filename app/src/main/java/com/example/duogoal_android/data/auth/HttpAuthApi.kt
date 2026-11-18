package com.example.duogoal_android.data.auth

import com.example.duogoal_android.BuildConfig
import java.io.IOException
import java.net.InetAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

/** Сеть возвращает токены, но никогда не сохраняет их сама. */
class HttpAuthApi(private val baseUrl: String = BuildConfig.API_BASE_URL) : AuthApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(false)
        .retryOnConnectionFailure(false)
        .apply {
            val ingressIp = BuildConfig.HLT_INGRESS_IPV4
            if (BuildConfig.DEBUG && ingressIp.isNotEmpty()) {
                proxy(Proxy.NO_PROXY)
                dns(object : Dns {
                    override fun lookup(hostname: String): List<InetAddress> =
                        if (hostname.equals("hlt.com", ignoreCase = true)) listOf(InetAddress.getByName(ingressIp))
                        else Dns.SYSTEM.lookup(hostname)
                })
            }
        }
        .build()

    override suspend fun login(email: String, password: String): AuthResult<AuthSession> {
        val response = post("login", JSONObject().put("email", email).put("password", password))
        if (response is Response.Failure && response.status == 403 && response.serverMessage == "Подтвердите почту") {
            return response.error.copy(kind = AuthFailureKind.VerificationRequired)
        }
        return tokens(response)
    }

    override suspend fun register(username: String, email: String, password: String): AuthResult<String> {
        val payload = JSONObject().put("username", username).put("email", email).put("password", password)
        return when (val response = post("register", payload)) {
            is Response.Failure -> response.error
            is Response.Success -> {
                val registeredEmail = response.body.optString("email")
                if (response.status == 201 && registeredEmail.isNotBlank()) AuthResult.Success(registeredEmail)
                else unexpectedResponse()
            }
        }
    }

    override suspend fun verifyEmail(email: String, code: String): AuthResult<Unit> {
        val payload = JSONObject().put("email", email).put("verificationCode", code)
        return when (val response = post("emailVerify", payload)) {
            is Response.Success -> emptySuccess(response)
            is Response.Failure -> when (response.status) {
                400 -> response.error.copy(message = "Не удалось подтвердить почту. Проверьте код или запросите новый.")
                410 -> response.error.copy(message = "Срок действия кода истёк. Запросите новый код.")
                429 -> response.error.copy(message = "Попытки закончились. Запросите новый код.")
                else -> response.error
            }
        }
    }

    override suspend fun resendEmail(email: String): AuthResult<Unit> =
        when (val response = post("emailResend", JSONObject().put("email", email))) {
            is Response.Success -> emptySuccess(response)
            is Response.Failure -> when (response.status) {
                410 -> AuthResult.Failure("Зарегистрируйтесь заново, чтобы получить код.", AuthFailureKind.RegistrationExpired)
                429 -> response.error.copy(message = "Код запрашивали недавно. Попробуйте чуть позже.")
                503 -> response.error.copy(message = "Не удалось отправить код. Попробуйте ещё раз.")
                else -> response.error
            }
        }

    override suspend fun refresh(refreshToken: String): AuthResult<AuthSession> =
        tokens(post("refresh", JSONObject().put("refreshToken", refreshToken)))

    private fun tokens(response: Response): AuthResult<AuthSession> = when (response) {
        is Response.Failure -> response.error
        is Response.Success -> {
            val access = response.body.optString("accessToken")
            val refresh = response.body.optString("refreshToken")
            if (access.isBlank() || refresh.isBlank()) {
                AuthResult.Failure("Сервер вернул неполный ответ. Попробуйте позже.", AuthFailureKind.Protocol)
            } else AuthResult.Success(AuthSession(access, refresh))
        }
    }

    private fun emptySuccess(response: Response.Success): AuthResult<Unit> =
        if (response.status == 204) AuthResult.Success(Unit) else unexpectedResponse()

    private fun unexpectedResponse() =
        AuthResult.Failure("Сервер вернул некорректный ответ. Попробуйте позже.", AuthFailureKind.Protocol)

    private suspend fun post(path: String, payload: JSONObject): Response = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${baseUrl.trimEnd('/')}/api/auth/$path")
                .header("Accept", "application/json")
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()
            val (status, body) = client.newCall(request).execute().use {
                it.code to it.body?.string().orEmpty()
            }
            if (status in 200..299) {
                Response.Success(status, if (body.isBlank()) JSONObject() else JSONObject(body))
            } else {
                val message = runCatching {
                    val json = JSONObject(body)
                    json.optString("errorText").ifBlank { json.optString("message") }
                }.getOrDefault("")
                val displayMessage = message.ifBlank {
                    if (BuildConfig.DEBUG) {
                        "Сервер вернул HTTP $status на POST /api/auth/$path."
                    } else {
                        "Сервер временно недоступен. Попробуйте ещё раз."
                    }
                }
                Response.Failure(AuthResult.Failure(displayMessage, failureKind(status)), status, message)
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: JSONException) {
            Response.Failure(unexpectedResponse())
        } catch (_: IOException) {
            Response.Failure(AuthResult.Failure("Не удалось подключиться к серверу. Проверьте интернет и повторите попытку.", AuthFailureKind.Unavailable))
        } catch (_: Exception) {
            Response.Failure(AuthResult.Failure("Не удалось выполнить запрос. Попробуйте ещё раз.", AuthFailureKind.Unavailable))
        }
    }

    private sealed interface Response {
        data class Success(val status: Int, val body: JSONObject) : Response
        data class Failure(
            val error: AuthResult.Failure,
            val status: Int? = null,
            val serverMessage: String = "",
        ) : Response
    }
}

/** В HLT refresh с недействительным токеном возвращает 400, а validate — 401. */
internal fun failureKind(status: Int): AuthFailureKind =
    if (status in listOf(400, 401, 403)) AuthFailureKind.Rejected else AuthFailureKind.Unavailable
