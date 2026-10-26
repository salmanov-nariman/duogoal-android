package com.example.duogoal_android.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.duogoal_android.data.auth.AuthRepository
import com.example.duogoal_android.data.auth.AuthResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class AuthMode { Login, Register }
data class AuthUiState(val mode: AuthMode = AuthMode.Login, val username: String = "", val email: String = "", val password: String = "", val loading: Boolean = false, val authenticated: Boolean = false, val error: String? = null)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application.applicationContext)
    var state = kotlinx.coroutines.flow.MutableStateFlow(AuthUiState())
        private set

    init { restoreSession() }
    fun setMode(mode: AuthMode) { state.value = state.value.copy(mode = mode, error = null) }
    fun updateUsername(value: String) { state.value = state.value.copy(username = value, error = null) }
    fun updateEmail(value: String) { state.value = state.value.copy(email = value, error = null) }
    fun updatePassword(value: String) { state.value = state.value.copy(password = value, error = null) }

    fun submit() {
        val current = state.value
        val error = when { current.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(current.email).matches() -> "Введите корректный email"; current.password.isBlank() -> "Введите пароль"; current.mode == AuthMode.Register && current.username.trim().isBlank() -> "Введите имя пользователя"; else -> null }
        if (error != null) { state.value = current.copy(error = error); return }
        state.value = current.copy(loading = true, error = null)
        viewModelScope.launch { applyResult(if (current.mode == AuthMode.Login) repository.login(current.email.trim(), current.password) else repository.register(current.username.trim(), current.email.trim(), current.password)) }
    }

    private fun restoreSession() = viewModelScope.launch {
        val existing = repository.session.first() ?: return@launch
        state.value = state.value.copy(loading = true)
        applyResult(repository.refresh(existing.refreshToken))
    }
    private fun applyResult(result: AuthResult) { state.value = when (result) { is AuthResult.Success -> state.value.copy(loading = false, authenticated = true, error = null); is AuthResult.Failure -> state.value.copy(loading = false, error = result.message) } }
}
