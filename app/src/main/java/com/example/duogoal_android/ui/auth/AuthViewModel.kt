package com.example.duogoal_android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewModelScope
import com.example.duogoal_android.data.auth.AuthFailureKind
import com.example.duogoal_android.data.auth.AuthRepository
import com.example.duogoal_android.data.auth.AuthResult
import com.example.duogoal_android.data.auth.SessionState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthMode { Login, Register }

data class AuthUiState(
    val mode: AuthMode = AuthMode.Login,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
) {
    override fun toString(): String = "AuthUiState(mode=$mode, loading=$loading)"
}

/** Форма переживает поворот через ViewModel; пароль не попадает в SavedStateHandle. */
class AuthViewModel(
    private val repository: AuthRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        AuthUiState(
            mode = runCatching { AuthMode.valueOf(savedState.get<String>(MODE).orEmpty()) }.getOrDefault(AuthMode.Login),
            username = savedState[USERNAME] ?: "",
            email = savedState[EMAIL_KEY] ?: "",
        ),
    )
    val state = mutableState.asStateFlow()
    private var requestJob: Job? = null

    init {
        viewModelScope.launch {
            repository.session.collect { session ->
                if (session is SessionState.SignedIn || session == SessionState.SigningOut || session is SessionState.SignOutFailed) {
                    requestJob?.cancel()
                    setState(AuthUiState())
                }
            }
        }
    }

    fun setMode(mode: AuthMode) = edit { copy(mode = mode, error = null) }
    fun updateUsername(value: String) = edit { copy(username = value, error = null) }
    fun updateEmail(value: String) = edit { copy(email = value, error = null) }
    fun updatePassword(value: String) = edit { copy(password = value, error = null) }

    fun submit() {
        val current = state.value
        if (current.loading) return
        val email = current.email.trim()
        val error = when {
            !EMAIL.matches(email) -> "Введите корректный email"
            current.password.isBlank() -> "Введите пароль"
            current.mode == AuthMode.Register && current.username.trim().isBlank() -> "Введите имя пользователя"
            else -> null
        }
        if (error != null) {
            setState(current.copy(error = error))
            return
        }
        setState(current.copy(loading = true, error = null))
        requestJob = viewModelScope.launch {
            try {
                val result = if (current.mode == AuthMode.Login) repository.login(email, current.password)
                else repository.register(current.username.trim(), email, current.password)
                currentCoroutineContext().ensureActive()
                setState(when (result) {
                    is AuthResult.Success -> AuthUiState()
                    is AuthResult.Failure -> if (result.kind == AuthFailureKind.Superseded) AuthUiState() else current.copy(
                        mode = if (result.accountCreated) AuthMode.Login else current.mode,
                        loading = false,
                        error = result.message,
                    )
                })
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                setState(current.copy(loading = false, error = "Не удалось выполнить вход. Попробуйте ещё раз."))
            }
        }
    }

    private fun edit(change: AuthUiState.() -> AuthUiState) {
        if (!state.value.loading) setState(state.value.change())
    }

    private fun setState(value: AuthUiState) {
        mutableState.value = value
        savedState[MODE] = value.mode.name
        savedState[USERNAME] = value.username
        savedState[EMAIL_KEY] = value.email
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            require(modelClass.isAssignableFrom(AuthViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, extras.createSavedStateHandle()) as T
        }
    }

    private companion object {
        const val MODE = "auth.mode"
        const val USERNAME = "auth.username"
        const val EMAIL_KEY = "auth.email"
        val EMAIL = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }
}
