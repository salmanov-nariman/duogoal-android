package com.example.duogoal_android

import android.app.Application
import com.example.duogoal_android.data.auth.AuthRepository
import com.example.duogoal_android.data.auth.EncryptedSessionStore
import com.example.duogoal_android.data.auth.HttpAuthApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class DuoGoalApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val authRepository: AuthRepository by lazy {
        AuthRepository(HttpAuthApi(), EncryptedSessionStore(this), applicationScope)
    }
}
