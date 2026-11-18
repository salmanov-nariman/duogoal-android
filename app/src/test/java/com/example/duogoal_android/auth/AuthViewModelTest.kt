package com.example.duogoal_android.auth

import com.example.duogoal_android.data.auth.AuthFailureKind
import com.example.duogoal_android.data.auth.AuthRepository
import com.example.duogoal_android.data.auth.AuthResult
import com.example.duogoal_android.data.auth.AuthSession
import com.example.duogoal_android.data.auth.SessionState
import com.example.duogoal_android.ui.auth.AuthMode
import com.example.duogoal_android.ui.auth.AuthRequest
import com.example.duogoal_android.ui.auth.AuthViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun invalidInputNeverCallsServer() = runTest {
        val api = FakeAuthApi()
        val viewModel = AuthViewModel(AuthRepository(api, FakeSessionStore(), this), SavedStateHandle())
        advanceUntilIdle()
        viewModel.updateEmail("invalid")
        viewModel.updatePassword("test")
        viewModel.submit()
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.error)
        assertEquals(0, api.loginCalls)
    }

    @Test fun repeatedSubmitAndEditsWhileLoadingDoNotDuplicateRequest() = runTest {
        val response = CompletableDeferred<AuthResult<AuthSession>>()
        val api = FakeAuthApi().apply { onLogin = { response.await() } }
        val viewModel = AuthViewModel(AuthRepository(api, FakeSessionStore(), this), SavedStateHandle())
        advanceUntilIdle()
        viewModel.updateEmail(" user@example.com ")
        viewModel.updatePassword("test")
        viewModel.submit()
        viewModel.submit()
        viewModel.setMode(AuthMode.Register)
        runCurrent()
        assertEquals(1, api.loginCalls)
        assertEquals("user@example.com", api.lastEmail)
        assertEquals(AuthMode.Login, viewModel.state.value.mode)
        response.complete(AuthResult.Success(AuthSession("access", "refresh")))
        advanceUntilIdle()
        assertEquals("", viewModel.state.value.password)
        assertEquals("", viewModel.state.value.email)
        assertFalse(viewModel.state.value.loading)
    }

    @Test fun registrationWaitsForSixDigitCodeBeforeSigningIn() = runTest {
        val api = FakeAuthApi().apply { onRegister = { AuthResult.Success("Registered@example.com") } }
        val store = FakeSessionStore()
        val repository = AuthRepository(api, store, this)
        val viewModel = AuthViewModel(repository, SavedStateHandle())
        advanceUntilIdle()
        viewModel.setMode(AuthMode.Register)
        viewModel.updateUsername("testuser")
        viewModel.updateEmail("user@example.com")
        viewModel.updatePassword("test")
        viewModel.submit()
        advanceUntilIdle()
        assertEquals(AuthMode.VerifyEmail, viewModel.state.value.mode)
        assertEquals("Registered@example.com", viewModel.state.value.email)
        assertEquals(SessionState.SignedOut(), repository.session.value)
        assertEquals(0, api.loginCalls)
        assertEquals(0, store.writes)

        viewModel.updateCode("0123")
        viewModel.submit()
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.error)
        assertEquals(0, api.verifyCalls)

        viewModel.updateCode("01 2345")
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("012345", api.lastCode)
        assertEquals("Registered@example.com", api.lastEmail)
        assertEquals(1, api.verifyCalls)
        assertEquals(1, api.loginCalls)
        assertEquals(1, store.writes)
        assertTrue(repository.session.value is SessionState.SignedIn)
        assertEquals("", viewModel.state.value.password)
        assertEquals("", viewModel.state.value.code)
    }

    @Test fun pendingAccountCanVerifyAfterProcessRecreation() = runTest {
        val api = FakeAuthApi().apply {
            onLogin = { AuthResult.Failure("Подтвердите почту", AuthFailureKind.VerificationRequired) }
        }
        val repository = AuthRepository(api, FakeSessionStore(), this)
        val handle = SavedStateHandle()
        val original = AuthViewModel(repository, handle)
        advanceUntilIdle()
        original.updateEmail("user@example.com")
        original.updatePassword("test")
        original.submit()
        advanceUntilIdle()
        original.updateCode("012")
        assertEquals(AuthMode.VerifyEmail, original.state.value.mode)

        val restored = AuthViewModel(repository, handle)
        assertEquals(AuthMode.VerifyEmail, restored.state.value.mode)
        assertEquals("user@example.com", restored.state.value.email)
        assertEquals("", restored.state.value.password)
        assertEquals("", restored.state.value.code)
        assertFalse(restored.state.value.loading)
        restored.updateCode("012345")
        restored.submit()
        advanceUntilIdle()
        assertEquals(AuthMode.Login, restored.state.value.mode)
        assertEquals("user@example.com", restored.state.value.email)
        assertTrue(restored.state.value.notice.orEmpty().contains("Почта подтверждена"))
        assertEquals(1, api.loginCalls)
    }

    @Test fun failedCodeAllowsResendButConcurrentActionsAreBlocked() = runTest {
        val response = CompletableDeferred<AuthResult<Unit>>()
        val api = FakeAuthApi().apply {
            onVerify = { AuthResult.Failure("Код истёк", AuthFailureKind.Rejected) }
            onResend = { response.await() }
        }
        val handle = SavedStateHandle(mapOf("auth.mode" to AuthMode.VerifyEmail.name, "auth.email" to "user@example.com"))
        val viewModel = AuthViewModel(AuthRepository(api, FakeSessionStore(), this), handle)
        advanceUntilIdle()
        viewModel.updateCode("012345")
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("Код истёк", viewModel.state.value.error)
        assertEquals(0, api.loginCalls)

        viewModel.resendCode()
        viewModel.resendCode()
        viewModel.submit()
        viewModel.setMode(AuthMode.Login)
        runCurrent()
        assertEquals(AuthRequest.Resend, viewModel.state.value.request)
        assertEquals(1, api.resendCalls)
        assertEquals(1, api.verifyCalls)
        response.complete(AuthResult.Success(Unit))
        advanceUntilIdle()
        assertEquals(AuthMode.VerifyEmail, viewModel.state.value.mode)
        assertEquals("", viewModel.state.value.code)
        assertNotNull(viewModel.state.value.notice)
    }

    @Test fun expiredRegistrationReturnsToRegistrationWithEmail() = runTest {
        val api = FakeAuthApi().apply {
            onResend = { AuthResult.Failure("Зарегистрируйтесь заново", AuthFailureKind.RegistrationExpired) }
        }
        val handle = SavedStateHandle(mapOf("auth.mode" to AuthMode.VerifyEmail.name, "auth.email" to "user@example.com"))
        val viewModel = AuthViewModel(AuthRepository(api, FakeSessionStore(), this), handle)
        advanceUntilIdle()
        viewModel.resendCode()
        advanceUntilIdle()
        assertEquals(AuthMode.Register, viewModel.state.value.mode)
        assertEquals("user@example.com", viewModel.state.value.email)
        assertEquals("Зарегистрируйтесь заново", viewModel.state.value.error)
    }

    @Test fun recreatedFormRestoresRegistrationAndEmailButNeverPasswordOrLoading() = runTest {
        val repository = AuthRepository(FakeAuthApi(), FakeSessionStore(), this)
        advanceUntilIdle()
        val handle = SavedStateHandle()
        val original = AuthViewModel(repository, handle)
        original.setMode(AuthMode.Register)
        original.updateUsername("testuser")
        original.updateEmail("user@example.com")
        original.updatePassword("never-save-this")
        val restored = AuthViewModel(repository, handle)
        assertEquals(AuthMode.Register, restored.state.value.mode)
        assertEquals("testuser", restored.state.value.username)
        assertEquals("user@example.com", restored.state.value.email)
        assertEquals("", restored.state.value.password)
        assertFalse(restored.state.value.loading)
        assertFalse(handle.keys().any { it.contains("password") })
    }

    @Test fun logoutClearsFormImmediatelyAndLateFailureCannotRestorePassword() = runTest {
        val response = CompletableDeferred<AuthResult<AuthSession>>()
        val api = FakeAuthApi().apply { onLogin = { withContext(NonCancellable) { response.await() } } }
        val repository = AuthRepository(api, FakeSessionStore(), this)
        val viewModel = AuthViewModel(repository, SavedStateHandle())
        advanceUntilIdle()
        viewModel.updateEmail("user@example.com")
        viewModel.updatePassword("test")
        viewModel.submit()
        runCurrent()
        repository.logout()
        runCurrent()
        assertEquals("", viewModel.state.value.password)
        assertFalse(viewModel.state.value.loading)
        response.complete(AuthResult.Failure("Late failure", AuthFailureKind.Unavailable))
        advanceUntilIdle()
        assertEquals("", viewModel.state.value.password)
        assertNull(viewModel.state.value.error)
    }
}
