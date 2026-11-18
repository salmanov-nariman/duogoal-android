package com.example.duogoal_android.data.auth

data class AuthSession(val accessToken: String, val refreshToken: String) {
    override fun toString(): String = "AuthSession(<redacted>)"
}

/** Локальный ключ навигации сохраняется при refresh и меняется при каждом новом входе. */
data class StoredSession(val tokens: AuthSession, val navigationKey: String) {
    override fun toString(): String = "StoredSession(<redacted>)"
}

enum class AuthFailureKind {
    Rejected, Unavailable, Protocol, Storage, Superseded, VerificationRequired, RegistrationExpired,
}

sealed interface AuthResult<out T> {
    data class Success<T>(val value: T) : AuthResult<T>
    data class Failure(
        val message: String,
        val kind: AuthFailureKind,
    ) : AuthResult<Nothing>
}

sealed interface SessionState {
    data object Restoring : SessionState
    data class SignedOut(val message: String? = null) : SessionState
    data class SignedIn(val navigationKey: String) : SessionState
    data class RestoreFailed(val message: String) : SessionState
    data object SigningOut : SessionState
    data class SignOutFailed(val message: String) : SessionState
}

interface AuthApi {
    suspend fun login(email: String, password: String): AuthResult<AuthSession>
    suspend fun register(username: String, email: String, password: String): AuthResult<String>
    suspend fun verifyEmail(email: String, code: String): AuthResult<Unit>
    suspend fun resendEmail(email: String): AuthResult<Unit>
    suspend fun refresh(refreshToken: String): AuthResult<AuthSession>
}

interface SessionStore {
    suspend fun read(): StoredSession?
    suspend fun write(session: StoredSession)
    suspend fun clear()
}

class InvalidStoredSessionException : Exception("Saved session cannot be decrypted")
