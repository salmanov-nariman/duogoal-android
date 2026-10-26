package com.example.duogoal_android.auth

import com.example.duogoal_android.data.auth.AuthApi
import com.example.duogoal_android.data.auth.AuthResult
import com.example.duogoal_android.data.auth.AuthSession
import com.example.duogoal_android.data.auth.SessionStore
import com.example.duogoal_android.data.auth.StoredSession

internal class FakeAuthApi : AuthApi {
    var loginCalls = 0
    var registerCalls = 0
    var refreshCalls = 0
    var lastEmail: String? = null
    var onLogin: suspend () -> AuthResult = { AuthResult.Success(AuthSession("new-access", "new-refresh")) }
    var onRegister: suspend () -> AuthResult = { onLogin() }
    var onRefresh: suspend () -> AuthResult = { AuthResult.Success(AuthSession("rotated-access", "rotated-refresh")) }

    override suspend fun login(email: String, password: String): AuthResult {
        loginCalls++
        lastEmail = email
        return onLogin()
    }
    override suspend fun register(username: String, email: String, password: String): AuthResult {
        registerCalls++
        return onRegister()
    }
    override suspend fun refresh(refreshToken: String): AuthResult {
        refreshCalls++
        return onRefresh()
    }
}

internal class FakeSessionStore(var saved: StoredSession? = null) : SessionStore {
    var readError: Exception? = null
    var writeError: Exception? = null
    var clearError: Exception? = null
    var writes = 0
    var clears = 0
    var beforeWrite: suspend () -> Unit = {}

    override suspend fun read(): StoredSession? {
        readError?.let { throw it }
        return saved
    }
    override suspend fun write(session: StoredSession) {
        beforeWrite()
        writeError?.let { throw it }
        writes++
        saved = session
    }
    override suspend fun clear() {
        clears++
        clearError?.let { throw it }
        saved = null
    }
}

internal fun existingSession() = StoredSession(AuthSession("old-access", "old-refresh"), "existing-navigation")
