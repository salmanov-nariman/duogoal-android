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

enum class AuthMode { Login, Register, VerifyEmail }

enum class AuthRequest { Submit, Resend }

data class AuthUiState(
    val mode: AuthMode = AuthMode.Login,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val code: String = "",
    val request: AuthRequest? = null,
    val error: String? = null,
    val notice: String? = null,
) {
    val loading: Boolean get() = request != null

    override fun toString(): String = "AuthUiState(mode=$mode, loading=$loading)"
}

/** ViewModel сохраняет форму при повороте. Пароль и код хранятся только в памяти. */
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

    fun setMode(mode: AuthMode) = edit {
        if (this.mode == AuthMode.VerifyEmail) AuthUiState(mode = mode, email = email)
        else copy(mode = mode, error = null, notice = null)
    }

    fun updateUsername(value: String) = edit { copy(username = value, error = null) }
    fun updateEmail(value: String) = edit { copy(email = value, error = null) }
    fun updatePassword(value: String) = edit { copy(password = value, error = null) }
    fun updateCode(value: String) = edit {
        copy(code = value.filter { it in '0'..'9' }.take(CODE_LENGTH), error = null)
    }

    fun submit() {
        val current = state.value
        if (current.loading) return
        val email = current.email.trim()
        val error = when {
            current.mode == AuthMode.VerifyEmail ->
                if (current.code.length == CODE_LENGTH) null else "Введите шестизначный код"
            !EMAIL.matches(email) -> "Введите корректный email"
            current.password.isBlank() -> "Введите пароль"
            current.mode == AuthMode.Register && current.username.trim().isBlank() -> "Введите имя пользователя"
            else -> null
        }
        if (error != null) {
            setState(current.copy(error = error))
            return
        }
        runRequest(current, AuthRequest.Submit) { current ->
            when (current.mode) {
                AuthMode.Login -> login(current, email)
                AuthMode.Register -> when (val result = repository.register(current.username.trim(), email, current.password)) {
                    is AuthResult.Success -> current.copy(mode = AuthMode.VerifyEmail, email = result.value)
                    is AuthResult.Failure -> failed(current, result)
                }
                AuthMode.VerifyEmail -> confirmEmail(current)
            }
        }
    }

    fun resendCode() {
        val current = state.value
        if (current.loading || current.mode != AuthMode.VerifyEmail) return
        runRequest(current, AuthRequest.Resend) { current ->
            when (val result = repository.resendEmail(current.email)) {
                is AuthResult.Success -> current.copy(code = "", notice = "Новый код отправлен.")
                is AuthResult.Failure -> when (result.kind) {
                    AuthFailureKind.RegistrationExpired -> AuthUiState(
                        mode = AuthMode.Register,
                        email = current.email,
                        username = current.username,
                        error = result.message,
                    )
                    else -> failed(current, result)
                }
            }
        }
    }

    private suspend fun login(current: AuthUiState, email: String): AuthUiState =
        when (val result = repository.login(email, current.password)) {
            is AuthResult.Success -> AuthUiState()
            is AuthResult.Failure -> if (result.kind == AuthFailureKind.VerificationRequired) {
                current.copy(mode = AuthMode.VerifyEmail, email = email)
            } else failed(current, result)
        }

    private suspend fun confirmEmail(current: AuthUiState): AuthUiState {
        when (val result = repository.verifyEmail(current.email, current.code)) {
            is AuthResult.Failure -> return failed(current, result)
            is AuthResult.Success -> Unit
        }
        currentCoroutineContext().ensureActive()
        val signIn = AuthUiState(email = current.email, notice = "Почта подтверждена. Войдите в аккаунт.")
        if (current.password.isBlank()) return signIn
        return when (val result = repository.login(current.email, current.password)) {
            is AuthResult.Success -> AuthUiState()
            is AuthResult.Failure -> failed(signIn, result)
        }
    }

    private fun failed(current: AuthUiState, failure: AuthResult.Failure): AuthUiState =
        if (failure.kind == AuthFailureKind.Superseded) AuthUiState() else current.copy(error = failure.message)

    private fun runRequest(current: AuthUiState, request: AuthRequest, block: suspend (AuthUiState) -> AuthUiState) {
        val ready = current.copy(error = null, notice = null)
        setState(ready.copy(request = request))
        requestJob = viewModelScope.launch {
            try {
                val result = block(ready)
                currentCoroutineContext().ensureActive()
                setState(result)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                setState(ready.copy(error = "Не удалось выполнить запрос. Попробуйте ещё раз."))
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
        const val CODE_LENGTH = 6
        val EMAIL = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }
}
