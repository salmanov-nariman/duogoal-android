package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.duogoal_android.ui.auth.AuthMode
import com.example.duogoal_android.ui.auth.AuthUiState
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.theme.DuoTheme

@Composable
fun AuthScreen(state: AuthUiState, onMode: (AuthMode) -> Unit, onUsername: (String) -> Unit, onEmail: (String) -> Unit, onPassword: (String) -> Unit, onSubmit: () -> Unit) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg).statusBarsPadding().navigationBarsPadding().padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(58.dp))
        DuoText("DuoGoal", 30, c.fg, FontWeight.Bold)
        DuoText("К цели — вдвоём", 14, c.fgMuted, modifier = Modifier.padding(top = 8.dp))
        Row(Modifier.fillMaxWidth().padding(top = 44.dp).clip(RoundedCornerShape(12.dp)).background(c.surfaceInput).padding(4.dp)) {
            AuthTab("Вход", state.mode == AuthMode.Login, Modifier.weight(1f)) { onMode(AuthMode.Login) }
            AuthTab("Регистрация", state.mode == AuthMode.Register, Modifier.weight(1f)) { onMode(AuthMode.Register) }
        }
        Column(Modifier.fillMaxWidth().padding(top = 22.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
            if (state.mode == AuthMode.Register) AuthField("Имя пользователя", state.username, onUsername)
            AuthField("Email", state.email, onEmail)
            AuthField("Пароль", state.password, onPassword, password = true)
            state.error?.let { DuoText(it, 13, c.danger, modifier = Modifier.padding(top = 2.dp)) }
            PrimaryButton(if (state.mode == AuthMode.Login) "Войти" else "Создать аккаунт", Modifier.padding(top = 5.dp), 54.dp, onClick = onSubmit)
            if (state.loading) DuoText("Подключаемся к серверу…", 13, c.fgMuted, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable private fun AuthTab(text: String, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val c = DuoTheme.colors
    Box(modifier.height(38.dp).clip(RoundedCornerShape(9.dp)).background(if (active) c.surface else Color.Transparent).clickable(onClick = onClick), contentAlignment = Alignment.Center) { DuoText(text, 14, if (active) c.fg else c.fgMuted, if (active) FontWeight.SemiBold else FontWeight.Medium) }
}
@Composable private fun AuthField(label: String, value: String, onValue: (String) -> Unit, password: Boolean = false) {
    val c = DuoTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) { DuoText(label, 12, c.fgMuted, FontWeight.Medium); BasicTextField(value, onValue, Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(13.dp)).background(c.surfaceInput).border(1.dp, c.borderStrong, RoundedCornerShape(13.dp)).padding(horizontal = 15.dp, vertical = 16.dp), textStyle = TextStyle(color = c.fg, fontSize = 15.sp), singleLine = true, visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None) }
}
