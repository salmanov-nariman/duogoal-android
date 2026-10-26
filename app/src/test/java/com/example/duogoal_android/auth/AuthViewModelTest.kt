package com.example.duogoal_android.auth

import com.example.duogoal_android.data.auth.AuthFailureKind
import com.example.duogoal_android.data.auth.AuthRepository
import com.example.duogoal_android.data.auth.AuthResult
import com.example.duogoal_android.data.auth.AuthSession
import com.example.duogoal_android.ui.auth.AuthMode
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
        val response = CompletableDeferred<AuthResult>()
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

    @Test fun successfulRegistrationWithFailedLoginSwitchesToSignIn() = runTest {
        val api = FakeAuthApi().apply {
            onRegister = { AuthResult.Failure("Аккаунт создан, сервер недоступен", AuthFailureKind.Unavailable, accountCreated = true) }
        }
        val viewModel = AuthViewModel(AuthRepository(api, FakeSessionStore(), this), SavedStateHandle())
        advanceUntilIdle()
        viewModel.setMode(AuthMode.Register)
        viewModel.updateUsername("testuser")
        viewModel.updateEmail("user@example.com")
        viewModel.updatePassword("test")
        viewModel.submit()
        advanceUntilIdle()
        assertEquals(AuthMode.Login, viewModel.state.value.mode)
        assertEquals("user@example.com", viewModel.state.value.email)
        assertNotNull(viewModel.state.value.error)
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
        val response = CompletableDeferred<AuthResult>()
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
