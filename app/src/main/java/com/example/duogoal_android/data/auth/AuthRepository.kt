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
    private var restoreJob: Job? = null
    private var logoutJob: Job? = null

    init { restoreSession() }

    fun retryRestoration() {
        if (session.value is SessionState.RestoreFailed) restoreSession()
    }

    suspend fun login(email: String, password: String): AuthResult = authenticate { api.login(email, password) }

    suspend fun register(username: String, email: String, password: String): AuthResult =
        authenticate(isRegistration = true) { api.register(username, email, password) }

    /** Сначала закрываем приватный UI; успех выхода публикуется только после очистки диска. */
    fun logout() {
        if (logoutJob?.isActive == true) return
        val operation = synchronized(stateLock) {
            generation.incrementAndGet().also { mutableSession.value = SessionState.SigningOut }
        }
        restoreJob?.cancel()
        logoutJob = scope.launch { clearSession(operation) }
    }

    private suspend fun authenticate(isRegistration: Boolean = false, request: suspend () -> AuthResult): AuthResult {
        val operation = synchronized(stateLock) {
            if (session.value !is SessionState.SignedOut) return superseded()
            generation.incrementAndGet()
        }
        val result = request()
        if (operation != generation.get()) return superseded()
        if (result !is AuthResult.Success) return result
        if (result.session.accessToken.isBlank() || result.session.refreshToken.isBlank()) {
            return AuthResult.Failure("Сервер вернул неполную сессию. Попробуйте позже.", AuthFailureKind.Protocol, accountCreated = isRegistration)
        }
        return storageMutex.withLock {
            if (operation != generation.get()) return@withLock superseded()
            try {
                val saved = StoredSession(result.session, UUID.randomUUID().toString())
                store.write(saved)
                if (operation != generation.get()) return@withLock superseded()
                if (!publish(operation, SessionState.SignedIn(saved.navigationKey))) return@withLock superseded()
                result
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                AuthResult.Failure("Не удалось сохранить сессию на устройстве. Попробуйте войти ещё раз.", AuthFailureKind.Storage, accountCreated = isRegistration)
            }
        }
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
                        if (result.session.accessToken.isBlank() || result.session.refreshToken.isBlank()) {
                            publish(operation, SessionState.RestoreFailed("Сервер вернул неполную сессию. Повторите попытку позже."))
                            return@withLock
                        }
                        store.write(saved.copy(tokens = result.session))
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
