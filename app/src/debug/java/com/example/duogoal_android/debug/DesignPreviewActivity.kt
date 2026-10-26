package com.example.duogoal_android.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.duogoal_android.ui.auth.AuthMode
import com.example.duogoal_android.ui.auth.AuthUiState
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.screens.AuthScreen
import com.example.duogoal_android.ui.screens.ChatScreen
import com.example.duogoal_android.ui.screens.ChatsScreen
import com.example.duogoal_android.ui.screens.CreateGoalScreen
import com.example.duogoal_android.ui.screens.FeedScreen
import com.example.duogoal_android.ui.screens.GoalActiveScreen
import com.example.duogoal_android.ui.screens.GoalWaitingScreen
import com.example.duogoal_android.ui.screens.InvitationScreen
import com.example.duogoal_android.ui.screens.NotificationsScreen
import com.example.duogoal_android.ui.screens.PartnerPickerScreen
import com.example.duogoal_android.ui.screens.ProfileScreen
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Изолированная галерея: нет ViewModel авторизации, HTTP-запросов или токенов. */
class DesignPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var dark by rememberSaveable { mutableStateOf(intent.getBooleanExtra("dark", systemDark)) }
            var route by rememberSaveable { mutableStateOf(intent.getStringExtra("screen") ?: "Вход") }
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
            DuoGoalTheme(darkTheme = dark) {
                val c = DuoTheme.colors
                Column(Modifier.fillMaxSize().background(c.bg).safeDrawingPadding()) {
                    Row(Modifier.fillMaxWidth().background(c.surface).horizontalScroll(rememberScrollState()).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PreviewControl(if (dark) "Тёмная" else "Светлая", true) { dark = !dark }
                        PreviewRoutes.forEach { label -> PreviewControl(label, route == label) { route = label } }
                    }
                    Box(Modifier.weight(1f)) { GalleryContent(route) { route = it } }
                }
            }
        }
    }
}

private val PreviewRoutes = listOf("Вход", "Регистрация", "Цель", "Лента", "Профиль", "Чаты", "Чат", "Создание", "Ожидание", "Напарник", "Приглашение", "Уведомления")

@Composable
private fun PreviewControl(label: String, selected: Boolean, onClick: () -> Unit) {
    val c = DuoTheme.colors
    Box(Modifier.clip(RoundedCornerShape(10.dp)).background(if (selected) c.accentSoft else c.bg).clickable(role = Role.Button, onClick = onClick).heightIn(min = 44.dp).padding(12.dp)) {
        DuoText(label, 12, if (selected) c.accentFg else c.fgMuted)
    }
}

@Composable
private fun GalleryContent(route: String, navigate: (String) -> Unit) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    // Демонстрационный пароль не сохраняется в saved instance state.
    var password by androidx.compose.runtime.remember { mutableStateOf("") }
    val back = { navigate("Профиль") }
    when (route) {
        "Вход", "Регистрация" -> AuthScreen(
            AuthUiState(mode = if (route == "Регистрация") AuthMode.Register else AuthMode.Login, username = username, email = email, password = password),
            { navigate(if (it == AuthMode.Register) "Регистрация" else "Вход") },
            { username = it }, { email = it }, { password = it }, {},
        )
        "Цель" -> GoalActiveScreen(onBack = back, onOpenChat = { navigate("Чат") })
        "Лента" -> FeedScreen { navigate("Уведомления") }
        "Профиль" -> ProfileScreen({ navigate("Уведомления") }, { navigate("Цель") }, { navigate("Ожидание") }, { navigate("Создание") })
        "Чаты" -> ChatsScreen({ navigate("Чат") }, { navigate("Уведомления") })
        "Чат" -> ChatScreen { navigate("Чаты") }
        "Создание" -> CreateGoalScreen(back) { navigate("Напарник") }
        "Ожидание" -> GoalWaitingScreen(back)
        "Напарник" -> PartnerPickerScreen(back) { navigate("Ожидание") }
        "Приглашение" -> InvitationScreen(back, { navigate("Цель") }, back)
        "Уведомления" -> NotificationsScreen(back) { navigate("Приглашение") }
    }
}
