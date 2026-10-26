package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.duogoal_android.ui.auth.AuthMode
import com.example.duogoal_android.ui.auth.AuthUiState
import com.example.duogoal_android.ui.components.DuoBrand
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

@Composable
fun AuthScreen(
    state: AuthUiState,
    onMode: (AuthMode) -> Unit,
    onUsername: (String) -> Unit,
    onEmail: (String) -> Unit,
    onPassword: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val c = DuoTheme.colors
    val focusManager = LocalFocusManager.current
    val register = state.mode == AuthMode.Register
    val submit = { focusManager.clearFocus(); onSubmit() }
    BoxWithConstraints(Modifier.fillMaxSize().background(c.bg).imePadding()) {
        val compact = maxHeight < 600.dp
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 26.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = 440.dp).fillMaxWidth()) {
                DuoBrand()
                TogetherIllustration(Modifier.fillMaxWidth().height(if (compact) 100.dp else 160.dp))
                DuoText("Твоя цель.\nВаш путь.", if (compact) 32 else 38, c.fg, FontWeight.SemiBold, letterSpacing = -0.045, lineHeight = 1.08)
                DuoText(
                    "Большие перемены начинаются\nс маленьких шагов. И своего человека.",
                    14, c.fgMuted, lineHeight = 1.6,
                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                )
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.accentSoft).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    AuthTab("Вход", !register, !state.loading, Modifier.weight(1f)) { onMode(AuthMode.Login) }
                    AuthTab("Регистрация", register, !state.loading, Modifier.weight(1f)) { onMode(AuthMode.Register) }
                }
                Column(Modifier.padding(top = 22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (register) {
                        AuthField("Имя пользователя", "Как тебя найти", state.username, onUsername, DuoIcons.User, "auth-username", enabled = !state.loading)
                    }
                    AuthField("Email", "you@example.com", state.email, onEmail, DuoIcons.Mail, "auth-email", enabled = !state.loading, keyboardType = KeyboardType.Email)
                    AuthField("Пароль", "Твой пароль", state.password, onPassword, DuoIcons.Lock, "auth-password", enabled = !state.loading, password = true, onDone = submit)
                    state.error?.let { message ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(c.danger.copy(alpha = 0.09f))
                                .semantics { error(message); liveRegion = LiveRegionMode.Polite }.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            DuoIcon(DuoIcons.AlertCircle, c.danger, 18.dp)
                            DuoText(message, 13, c.danger, modifier = Modifier.weight(1f))
                        }
                    }
                    PrimaryButton(
                        if (register) "Создать аккаунт" else "Войти",
                        modifier = Modifier.testTag("auth-submit"), icon = DuoIcons.ArrowRight,
                        enabled = !state.loading, loading = state.loading, onClick = submit,
                    )
                }
                Box(Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 12.dp), contentAlignment = Alignment.Center) {
                    DuoText("Вместе легче сделать следующий шаг.", 12, c.fgMuted)
                }
            }
        }
    }
}

@Composable
private fun AuthTab(text: String, active: Boolean, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val c = DuoTheme.colors
    Box(
        modifier.heightIn(min = 44.dp).clip(RoundedCornerShape(10.dp)).background(if (active) c.surface else Color.Transparent)
            .semantics { selected = active }.clickable(enabled = enabled, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 12.dp), contentAlignment = Alignment.Center,
    ) { DuoText(text, 14, if (active) c.fg else c.fgMuted, if (active) FontWeight.SemiBold else FontWeight.Medium) }
}

@Composable
private fun AuthField(
    label: String, placeholder: String, value: String, onValue: (String) -> Unit, icon: ImageVector,
    tag: String, enabled: Boolean, keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false, onDone: () -> Unit = {},
) {
    val c = DuoTheme.colors
    val focusManager = LocalFocusManager.current
    var focused by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DuoText(label, 12, c.fgMuted, FontWeight.Medium, modifier = Modifier.padding(start = 2.dp))
        BasicTextField(
            value = value, onValueChange = onValue, enabled = enabled,
            modifier = Modifier.fillMaxWidth().testTag(tag).onFocusChanged { focused = it.isFocused }.semantics { contentDescription = label },
            textStyle = TextStyle(color = c.fg, fontSize = 16.sp), cursorBrush = SolidColor(c.accentFg), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboardType, imeAction = if (password) ImeAction.Done else ImeAction.Next, autoCorrectEnabled = false),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }, onDone = { onDone() }),
            visualTransformation = if (password && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { innerTextField ->
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 54.dp).clip(RoundedCornerShape(14.dp)).background(c.surfaceInput)
                        .border(if (focused) 1.5.dp else 1.dp, if (focused) c.accentFg else c.borderStrong, RoundedCornerShape(14.dp))
                        .padding(start = 14.dp, end = if (password) 3.dp else 14.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DuoIcon(icon, if (focused) c.accentFg else c.fgMuted, 18.dp)
                    Box(Modifier.weight(1f).padding(vertical = 16.dp)) {
                        if (value.isEmpty()) DuoText(placeholder, 14, c.fgSubtle)
                        innerTextField()
                    }
                    if (password) {
                        Box(
                            Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                                .semantics { contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль" }
                                .clickable(enabled = enabled, role = Role.Button) { passwordVisible = !passwordVisible },
                            contentAlignment = Alignment.Center,
                        ) { DuoIcon(if (passwordVisible) DuoIcons.EyeOff else DuoIcons.Eye, c.fgMuted, 18.dp) }
                    }
                }
            },
        )
    }
}

@Composable
private fun TogetherIllustration(modifier: Modifier = Modifier) {
    val c = DuoTheme.colors
    Box(modifier, contentAlignment = Alignment.Center) {
        Box(Modifier.size(240.dp, 130.dp).background(Brush.radialGradient(listOf(c.accent.copy(alpha = 0.10f), Color.Transparent))))
        Canvas(Modifier.size(230.dp, 112.dp)) {
            rotate(-22f) { drawOval(c.border, topLeft = Offset(12.dp.toPx(), 20.dp.toPx()), size = Size(206.dp.toPx(), 72.dp.toPx()), style = Stroke(1.dp.toPx())) }
            rotate(24f) { drawOval(c.border, topLeft = Offset(30.dp.toPx(), 9.dp.toPx()), size = Size(170.dp.toPx(), 94.dp.toPx()), style = Stroke(1.dp.toPx())) }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.rotate(-12f).shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = c.fg.copy(alpha = 0.04f), spotColor = c.fg.copy(alpha = 0.04f))
                    .size(74.dp, 86.dp).clip(RoundedCornerShape(24.dp)).background(c.surface).border(1.dp, c.border, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) { DuoIcon(DuoIcons.Footprints, c.accentFg, 32.dp) }
            Box(
                Modifier.padding(top = 16.dp).rotate(12f).size(74.dp, 86.dp).clip(RoundedCornerShape(24.dp)).background(c.accent),
                contentAlignment = Alignment.Center,
            ) { DuoIcon(DuoIcons.Sparkles, c.onAccent, 32.dp) }
        }
    }
}

@Preview(name = "Вход · светлая", widthDp = 392, heightDp = 852)
@Preview(name = "Вход · тёмная", widthDp = 392, heightDp = 852, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AuthPreview() { DuoGoalTheme { AuthScreen(AuthUiState(), {}, {}, {}, {}, {}) } }

@Preview(name = "Регистрация · компактная", widthDp = 320, heightDp = 640, fontScale = 1.2f)
@Composable
private fun RegistrationPreview() { DuoGoalTheme { AuthScreen(AuthUiState(mode = AuthMode.Register), {}, {}, {}, {}, {}) } }
