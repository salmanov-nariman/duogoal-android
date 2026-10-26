package com.example.duogoal_android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.duogoal_android.DuoGoalApplication
import com.example.duogoal_android.data.auth.SessionState
import com.example.duogoal_android.ui.auth.AuthMode
import com.example.duogoal_android.ui.auth.AuthViewModel
import com.example.duogoal_android.ui.components.DuoBrand
import com.example.duogoal_android.ui.components.DuoCard
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.components.SecondaryButton
import com.example.duogoal_android.ui.navigation.DuoNavigation
import com.example.duogoal_android.ui.screens.AuthScreen
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

@Composable
fun DuoGoalApp() {
    val application = LocalContext.current.applicationContext as DuoGoalApplication
    val repository = application.authRepository
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory(repository))
    val session by repository.session.collectAsStateWithLifecycle()
    val auth by viewModel.state.collectAsStateWithLifecycle()

    SessionGate(session, repository::retryRestoration, repository::logout) { message ->
        var notice by rememberSaveable(message) { mutableStateOf(message) }
        BackHandler(enabled = auth.mode == AuthMode.Register && !auth.loading) {
            notice = null
            viewModel.setMode(AuthMode.Login)
        }
        AuthScreen(
            state = auth.copy(error = auth.error ?: notice),
            onMode = { notice = null; viewModel.setMode(it) },
            onUsername = { notice = null; viewModel.updateUsername(it) },
            onEmail = { notice = null; viewModel.updateEmail(it) },
            onPassword = { notice = null; viewModel.updatePassword(it) },
            onSubmit = { notice = null; viewModel.submit() },
        )
    }
}

/** Приватный NavHost вообще не монтируется до успешного восстановления сессии. */
@Composable
internal fun SessionGate(
    session: SessionState,
    onRetry: () -> Unit,
    onLogout: () -> Unit,
    authContent: @Composable (message: String?) -> Unit,
) {
    val holder = rememberSaveableStateHolder()
    var previousSession by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(session) {
        when (session) {
            is SessionState.SignedIn -> previousSession = session.navigationKey
            is SessionState.SignedOut, SessionState.SigningOut, is SessionState.SignOutFailed -> {
                previousSession?.let(holder::removeState)
                previousSession = null
            }
            else -> Unit // При временном сбое восстановления сохраняем ожидающий back stack.
        }
    }
    DuoGoalTheme {
        Column(
            Modifier.fillMaxSize().background(DuoTheme.colors.bg).statusBarsPadding().navigationBarsPadding(),
        ) {
            when (session) {
                SessionState.Restoring -> SessionStatus("С возвращением", "Восстанавливаем твою сессию…")
                is SessionState.SignedOut -> authContent(session.message)
                is SessionState.SignedIn -> holder.SaveableStateProvider(session.navigationKey) {
                    var confirmLogout by rememberSaveable { mutableStateOf(false) }
                    DuoNavigation(onLogout = { confirmLogout = true })
                    if (confirmLogout) LogoutConfirmation(
                        onDismiss = { confirmLogout = false },
                        onConfirm = { confirmLogout = false; onLogout() },
                    )
                }
                is SessionState.RestoreFailed -> SessionStatus(
                    "Не удалось восстановить вход", session.message,
                    primaryText = "Повторить попытку", onPrimary = onRetry,
                    secondaryText = "Выйти из аккаунта", onSecondary = onLogout,
                )
                SessionState.SigningOut -> SessionStatus("Выходим из аккаунта", "Удаляем сессию с этого устройства…")
                is SessionState.SignOutFailed -> SessionStatus(
                    "Выход не завершён", session.message,
                    primaryText = "Повторить выход", onPrimary = onLogout,
                )
            }
        }
    }
}

@Composable
private fun SessionStatus(
    title: String, message: String,
    primaryText: String? = null, onPrimary: () -> Unit = {},
    secondaryText: String? = null, onSecondary: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().padding(26.dp).testTag("session-status")) {
        DuoBrand()
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            DuoCard {
                DuoText(title, 24, c.fg, FontWeight.SemiBold, letterSpacing = -0.03)
                DuoText(message, 14, c.fgMuted, lineHeight = 1.6, modifier = Modifier.padding(top = 12.dp, bottom = 20.dp))
                if (primaryText != null) PrimaryButton(primaryText, onClick = onPrimary)
                if (secondaryText != null) SecondaryButton(secondaryText, Modifier.padding(top = 12.dp), onClick = onSecondary)
            }
        }
    }
}

@Composable
private fun LogoutConfirmation(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val c = DuoTheme.colors
    Dialog(onDismissRequest = onDismiss) {
        DuoCard {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DuoText("Выйти из аккаунта?", 22, c.fg, FontWeight.SemiBold)
                DuoText("Чтобы вернуться к своим целям, понадобится снова войти.", 14, c.fgMuted, lineHeight = 1.6)
                PrimaryButton("Выйти", Modifier.testTag("confirm-logout"), onClick = onConfirm)
                SecondaryButton("Остаться", onClick = onDismiss)
            }
        }
    }
}
