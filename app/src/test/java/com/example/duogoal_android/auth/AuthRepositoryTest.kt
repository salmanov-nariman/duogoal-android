package com.example.duogoal_android.auth

import com.example.duogoal_android.data.auth.AuthFailureKind
import com.example.duogoal_android.data.auth.AuthRepository
import com.example.duogoal_android.data.auth.AuthResult
import com.example.duogoal_android.data.auth.AuthSession
import com.example.duogoal_android.data.auth.InvalidStoredSessionException
import com.example.duogoal_android.data.auth.SessionState
import com.example.duogoal_android.data.auth.failureKind
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {
    @Test fun emptyStoreOpensSignInWithoutRequest() = runTest {
        val api = FakeAuthApi()
        val repository = AuthRepository(api, FakeSessionStore(), this)
        advanceUntilIdle()
        assertEquals(SessionState.SignedOut(), repository.session.value)
        assertEquals(0, api.refreshCalls)
    }

    @Test fun restorationRotatesBothTokensAndPreservesNavigationIdentity() = runTest {
        val store = FakeSessionStore(existingSession())
        val repository = AuthRepository(FakeAuthApi(), store, this)
        assertEquals(SessionState.Restoring, repository.session.value)
        advanceUntilIdle()
        assertEquals(SessionState.SignedIn("existing-navigation"), repository.session.value)
        assertEquals(AuthSession("rotated-access", "rotated-refresh"), store.saved?.tokens)
    }

    @Test fun privateUiWaitsUntilRotatedTokensAreSaved() = runTest {
        val disk = CompletableDeferred<Unit>()
        val store = FakeSessionStore(existingSession()).apply { beforeWrite = { disk.await() } }
        val repository = AuthRepository(FakeAuthApi(), store, this)
        runCurrent()
        assertEquals(SessionState.Restoring, repository.session.value)
        disk.complete(Unit)
        advanceUntilIdle()
        assertTrue(repository.session.value is SessionState.SignedIn)
    }

    @Test fun rejectedRefreshClearsSessionAndExplainsExpiry() = runTest {
        val store = FakeSessionStore(existingSession())
        val api = FakeAuthApi().apply { onRefresh = { AuthResult.Failure("Expired", AuthFailureKind.Rejected) } }
        val repository = AuthRepository(api, store, this)
        advanceUntilIdle()
        assertNull(store.saved)
        assertNotNull((repository.session.value as SessionState.SignedOut).message)
    }

    @Test fun offlineRestorationKeepsCredentialsAndCanBeRetried() = runTest {
        val original = existingSession()
        val store = FakeSessionStore(original)
        val api = FakeAuthApi().apply { onRefresh = { AuthResult.Failure("Offline", AuthFailureKind.Unavailable) } }
        val repository = AuthRepository(api, store, this)
        advanceUntilIdle()
        assertTrue(repository.session.value is SessionState.RestoreFailed)
        assertEquals(original, store.saved)
        assertEquals(0, store.clears)
        api.onRefresh = { AuthResult.Success(AuthSession("retry-access", "retry-refresh")) }
        repository.retryRestoration()
        repository.retryRestoration()
        advanceUntilIdle()
        assertEquals(SessionState.SignedIn(original.navigationKey), repository.session.value)
        assertEquals(2, api.refreshCalls)
    }

    @Test fun malformedRefreshDoesNotDestroySavedCredentials() = runTest {
        val store = FakeSessionStore(existingSession())
        val api = FakeAuthApi().apply { onRefresh = { AuthResult.Success(AuthSession("", "")) } }
        val repository = AuthRepository(api, store, this)
        advanceUntilIdle()
        assertTrue(repository.session.value is SessionState.RestoreFailed)
        assertEquals(existingSession(), store.saved)
        assertEquals(0, store.clears)
    }

    @Test fun corruptedCredentialsAreRemovedWithoutCrashing() = runTest {
        val store = FakeSessionStore(existingSession()).apply { readError = InvalidStoredSessionException() }
        val repository = AuthRepository(FakeAuthApi(), store, this)
        advanceUntilIdle()
        assertTrue(repository.session.value is SessionState.SignedOut)
        assertNull(store.saved)
    }

    @Test fun diskReadFailureIsRecoverableAndDoesNotEraseCredentials() = runTest {
        val store = FakeSessionStore(existingSession()).apply { readError = IOException("disk unavailable") }
        val repository = AuthRepository(FakeAuthApi(), store, this)
        advanceUntilIdle()
        assertTrue(repository.session.value is SessionState.RestoreFailed)
        assertEquals(0, store.clears)
        assertNotNull(store.saved)
    }

    @Test fun failedPersistenceNeverOpensPrivateUi() = runTest {
        val store = FakeSessionStore().apply { writeError = IOException("disk full") }
        val repository = AuthRepository(FakeAuthApi(), store, this)
        advanceUntilIdle()
        val result = repository.login("user@example.com", "test") as AuthResult.Failure
        assertEquals(AuthFailureKind.Storage, result.kind)
        assertTrue(repository.session.value is SessionState.SignedOut)
    }

    @Test fun logoutImmediatelyClosesPrivateUiAndClearsDisk() = runTest {
        val store = FakeSessionStore(existingSession())
        val repository = AuthRepository(FakeAuthApi(), store, this)
        advanceUntilIdle()
        repository.logout()
        assertEquals(SessionState.SigningOut, repository.session.value)
        advanceUntilIdle()
        assertEquals(SessionState.SignedOut(), repository.session.value)
        assertNull(store.saved)
        val apiAfterRestart = FakeAuthApi()
        val restarted = AuthRepository(apiAfterRestart, store, this)
        advanceUntilIdle()
        assertTrue(restarted.session.value is SessionState.SignedOut)
        assertEquals(0, apiAfterRestart.refreshCalls)
    }

    @Test fun logoutFailureStaysBlockedUntilClearCanBeRetried() = runTest {
        val store = FakeSessionStore(existingSession())
        val repository = AuthRepository(FakeAuthApi(), store, this)
        advanceUntilIdle()
        store.clearError = IOException("disk unavailable")
        repository.logout()
        advanceUntilIdle()
        assertTrue(repository.session.value is SessionState.SignOutFailed)
        assertNotNull(store.saved)
        store.clearError = null
        repository.logout()
        advanceUntilIdle()
        assertTrue(repository.session.value is SessionState.SignedOut)
        assertNull(store.saved)
    }

    @Test fun lateLoginCannotResurrectLoggedOutSession() = runTest {
        val response = CompletableDeferred<AuthResult>()
        val api = FakeAuthApi().apply { onLogin = { response.await() } }
        val store = FakeSessionStore()
        val repository = AuthRepository(api, store, this)
        advanceUntilIdle()
        val login = async { repository.login("user@example.com", "test") }
        runCurrent()
        repository.logout()
        runCurrent()
        response.complete(AuthResult.Success(AuthSession("late-access", "late-refresh")))
        advanceUntilIdle()
        assertEquals(AuthFailureKind.Superseded, (login.await() as AuthResult.Failure).kind)
        assertEquals(0, store.writes)
        assertNull(store.saved)
        assertTrue(repository.session.value is SessionState.SignedOut)
    }

    @Test fun lateRefreshCannotResurrectLoggedOutSessionEvenIfApiIgnoresCancellation() = runTest {
        val response = CompletableDeferred<AuthResult>()
        val api = FakeAuthApi().apply { onRefresh = { withContext(NonCancellable) { response.await() } } }
        val store = FakeSessionStore(existingSession())
        val repository = AuthRepository(api, store, this)
        runCurrent()
        repository.logout()
        runCurrent()
        response.complete(AuthResult.Success(AuthSession("late-access", "late-refresh")))
        advanceUntilIdle()
        assertNull(store.saved)
        assertEquals(0, store.writes)
        assertTrue(repository.session.value is SessionState.SignedOut)
    }

    @Test fun logoutWinsIfLoginWasAlreadyWritingToDisk() = runTest {
        val disk = CompletableDeferred<Unit>()
        val store = FakeSessionStore().apply { beforeWrite = { disk.await() } }
        val repository = AuthRepository(FakeAuthApi(), store, this)
        advanceUntilIdle()
        val login = async { repository.login("user@example.com", "test") }
        runCurrent()
        repository.logout()
        runCurrent()
        assertEquals(SessionState.SigningOut, repository.session.value)
        disk.complete(Unit)
        advanceUntilIdle()
        assertTrue(login.await() is AuthResult.Failure)
        assertNull(store.saved)
        assertTrue(repository.session.value is SessionState.SignedOut)
    }

    @Test fun signingInAgainGetsNewNavigationIdentity() = runTest {
        val store = FakeSessionStore()
        val repository = AuthRepository(FakeAuthApi(), store, this)
        advanceUntilIdle()
        repository.login("first@example.com", "test")
        val firstKey = (repository.session.value as SessionState.SignedIn).navigationKey
        repository.logout()
        advanceUntilIdle()
        repository.login("second@example.com", "test")
        assertNotEquals(firstKey, (repository.session.value as SessionState.SignedIn).navigationKey)
    }

    @Test fun lateFailureAfterLogoutIsDiscardedToo() = runTest {
        val response = CompletableDeferred<AuthResult>()
        val api = FakeAuthApi().apply { onLogin = { response.await() } }
        val repository = AuthRepository(api, FakeSessionStore(), this)
        advanceUntilIdle()
        val login = async { repository.login("user@example.com", "test") }
        runCurrent()
        repository.logout()
        runCurrent()
        response.complete(AuthResult.Failure("Offline", AuthFailureKind.Unavailable))
        advanceUntilIdle()
        assertEquals(AuthFailureKind.Superseded, (login.await() as AuthResult.Failure).kind)
    }

    @Test fun onlyExplicitAuthRejectionInvalidatesSession() {
        listOf(400, 401, 403).forEach { assertEquals(AuthFailureKind.Rejected, failureKind(it)) }
        listOf(301, 404, 429, 500, 503).forEach { assertEquals(AuthFailureKind.Unavailable, failureKind(it)) }
    }

    @Test fun tokenContainersDoNotExposeSecretsThroughToString() {
        assertFalse(AuthSession("secret-access", "secret-refresh").toString().contains("secret"))
        assertFalse(existingSession().toString().contains("old-access"))
    }

    @Test fun registrationPersistenceFailureDoesNotSuggestCreatingAccountAgain() = runTest {
        val store = FakeSessionStore().apply { writeError = IOException("disk full") }
        val repository = AuthRepository(FakeAuthApi(), store, this)
        advanceUntilIdle()
        val result = repository.register("testuser", "user@example.com", "test") as AuthResult.Failure
        assertTrue(result.accountCreated)
        assertTrue(repository.session.value is SessionState.SignedOut)
    }
}
