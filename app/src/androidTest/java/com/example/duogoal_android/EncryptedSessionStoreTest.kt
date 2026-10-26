package com.example.duogoal_android

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.platform.app.InstrumentationRegistry
import com.example.duogoal_android.data.auth.AuthSession
import com.example.duogoal_android.data.auth.EncryptedSessionStore
import com.example.duogoal_android.data.auth.InvalidStoredSessionException
import com.example.duogoal_android.data.auth.StoredSession
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/** Настоящий Keystore/DataStore, но отдельный временный файл: пользовательская сессия не затрагивается. */
class EncryptedSessionStoreTest {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)
    private lateinit var directory: File
    private lateinit var file: File
    private lateinit var store: EncryptedSessionStore
    private lateinit var dataStore: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>

    @Before fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        directory = File(context.cacheDir, "session-store-test-${UUID.randomUUID()}").apply { check(mkdirs()) }
        file = File(directory, "test.preferences_pb")
        dataStore = PreferenceDataStoreFactory.create(scope = scope, produceFile = { file })
        store = EncryptedSessionStore(dataStore)
    }

    @After fun tearDown() = runBlocking {
        job.cancelAndJoin()
        directory.deleteRecursively()
        Unit
    }

    @Test fun sessionCanBeReadByNewStoreAndTokensAreNotPlaintext() = runBlocking {
        val session = StoredSession(AuthSession("plaintext-test-access", "plaintext-test-refresh"), "navigation-test")
        store.write(session)
        assertEquals(session, EncryptedSessionStore(dataStore).read())
        val disk = String(file.readBytes(), Charsets.ISO_8859_1)
        assertFalse(disk.contains("plaintext-test-access"))
        assertFalse(disk.contains("plaintext-test-refresh"))
        store.clear()
        assertNull(EncryptedSessionStore(dataStore).read())
    }

    @Test fun oldEncryptedSessionWithoutNavigationKeyIsStillReadable() = runBlocking {
        val tokens = AuthSession("legacy-access", "legacy-refresh")
        store.write(StoredSession(tokens, "old-navigation"))
        dataStore.edit { it.remove(stringPreferencesKey("navigation_key")) }
        val recovered = store.read()!!
        assertEquals(tokens, recovered.tokens)
        assertTrue(recovered.navigationKey.isNotBlank())
    }

    @Test fun damagedCiphertextIsReportedAndCanBeCleared() = runBlocking {
        store.write(StoredSession(AuthSession("test-access", "test-refresh"), "test-navigation"))
        dataStore.edit { it[stringPreferencesKey("access_token")] = "not-valid-encrypted-data" }
        try {
            store.read()
            fail("Corrupt session must not be accepted")
        } catch (_: InvalidStoredSessionException) {
            store.clear()
            assertNull(store.read())
        }
    }
}
