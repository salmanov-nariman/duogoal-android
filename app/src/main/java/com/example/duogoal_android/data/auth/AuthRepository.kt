package com.example.duogoal_android.data.auth

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Единственный владелец сессии в процессе приложения. Не зависит от Activity. */
class AuthRepository(
    private val api: AuthApi,
    private val store: SessionStore,
    private val scope: CoroutineScope,
) {
    private val mutableSession = MutableStateFlow<SessionState>(SessionState.Restoring)
    val session: StateFlow<SessionState> = mutableSession.asStateFlow()
    private val generation = AtomicLong(0)
    private val stateLock = Any()
    private val storageMutex = Mutex()
    private val signedOutRequestMutex = Mutex()
    private var restoreJob: Job? = null
    private var logoutJob: Job? = null

    init { restoreSession() }

    fun retryRestoration() {
        if (session.value is SessionState.RestoreFailed) restoreSession()
    }

    suspend fun login(email: String, password: String): AuthResult<AuthSession> = signedOutRequestMutex.withLock request@{
        val operation = beginSignedOutRequest() ?: return@request superseded()
        val result = api.login(email, password)
        if (operation != generation.get()) return@request superseded()
        if (result !is AuthResult.Success) return@request result
        if (result.value.accessToken.isBlank() || result.value.refreshToken.isBlank()) {
            return@request AuthResult.Failure("Сервер вернул неполную сессию. Попробуйте позже.", AuthFailureKind.Protocol)
        }
        storageMutex.withLock {
            if (operation != generation.get()) return@withLock superseded()
            try {
                val saved = StoredSession(result.value, UUID.randomUUID().toString())
                store.write(saved)
                if (!publish(operation, SessionState.SignedIn(saved.navigationKey))) return@withLock superseded()
                result
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                AuthResult.Failure("Не удалось сохранить сессию на устройстве. Попробуйте войти ещё раз.", AuthFailureKind.Storage)
            }
        }
    }

    suspend fun register(username: String, email: String, password: String): AuthResult<String> =
        requestWhileSignedOut { api.register(username, email, password) }

    suspend fun verifyEmail(email: String, code: String): AuthResult<Unit> =
        requestWhileSignedOut { api.verifyEmail(email, code) }

    suspend fun resendEmail(email: String): AuthResult<Unit> =
        requestWhileSignedOut { api.resendEmail(email) }

    /** Сначала закрываем приватный UI; успех выхода публикуется только после очистки диска. */
    fun logout() {
        if (logoutJob?.isActive == true) return
        val operation = synchronized(stateLock) {
            generation.incrementAndGet().also { mutableSession.value = SessionState.SigningOut }
        }
        restoreJob?.cancel()
        logoutJob = scope.launch { clearSession(operation) }
    }

    private fun beginSignedOutRequest(): Long? =
        synchronized(stateLock) {
            if (session.value is SessionState.SignedOut) generation.incrementAndGet() else null
        }

    private suspend fun <T> requestWhileSignedOut(request: suspend () -> AuthResult<T>): AuthResult<T> = signedOutRequestMutex.withLock {
        val operation = beginSignedOutRequest() ?: return@withLock superseded()
        val result = request()
        if (operation == generation.get()) result else superseded()
    }

    private fun restoreSession() {
        if (restoreJob?.isActive == true) return
        val operation = generation.get()
        mutableSession.value = SessionState.Restoring
        restoreJob = scope.launch {
            try {
                val saved = storageMutex.withLock { store.read() }
                if (operation != generation.get()) return@launch
                if (saved == null) {
                    publish(operation, SessionState.SignedOut())
                    return@launch
                }
                when (val result = api.refresh(saved.tokens.refreshToken)) {
                    is AuthResult.Success -> storageMutex.withLock {
                        if (operation != generation.get()) return@withLock
                        if (result.value.accessToken.isBlank() || result.value.refreshToken.isBlank()) {
                            publish(operation, SessionState.RestoreFailed("Сервер вернул неполную сессию. Повторите попытку позже."))
                            return@withLock
                        }
                        store.write(saved.copy(tokens = result.value))
                        publish(operation, SessionState.SignedIn(saved.navigationKey))
                    }
                    is AuthResult.Failure -> {
                        if (result.kind == AuthFailureKind.Rejected) {
                            clearSession(operation, "Сессия истекла. Войдите в аккаунт снова.")
                        } else {
                            publish(operation, SessionState.RestoreFailed(result.message))
                        }
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: InvalidStoredSessionException) {
                clearSession(operation, "Не удалось восстановить сохранённую сессию. Войдите снова.")
            } catch (_: Exception) {
                publish(operation, SessionState.RestoreFailed("Не удалось прочитать или сохранить сессию. Попробуйте ещё раз."))
            }
        }
    }

    private suspend fun clearSession(operation: Long, message: String? = null) {
        storageMutex.withLock {
            if (operation != generation.get()) return@withLock
            try {
                store.clear()
                publish(operation, SessionState.SignedOut(message))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                publish(operation, SessionState.SignOutFailed("Не удалось удалить сессию с устройства. Повторите выход."))
            }
        }
    }

    private fun publish(operation: Long, state: SessionState): Boolean =
        synchronized(stateLock) {
            if (operation != generation.get()) return@synchronized false
            mutableSession.value = state
            true
        }

    private fun superseded() = AuthResult.Failure("Операция отменена.", AuthFailureKind.Superseded)
}
