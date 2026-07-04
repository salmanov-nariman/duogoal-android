package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 5 — Вход. */
@Composable
fun LoginScreen(onLogin: () -> Unit = {}) {
    val c = DuoTheme.colors
    Box(Modifier.fillMaxSize().background(c.bg)) {
        // Фиолетовое свечение сверху, как в дизайне
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .size(width = 420.dp, height = 340.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(c.accent.copy(alpha = if (c.isDark) 0.20f else 0.16f), Color.Transparent),
                        center = Offset.Unspecified,
                    )
                )
        )
        Column(
            Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                Modifier.padding(top = 52.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (c.isDark) Color(0x0DFFFFFF) else Color.White)
                        .border(1.dp, c.borderStrong, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    DuoIcon(DuoIcons.Logo, c.accent, 40.dp)
                }
                DuoText("DuoGoal", 30, c.fg, FontWeight.Bold, letterSpacing = -0.02)
                DuoText("К цели — вдвоём", 14, c.fgMuted)
            }

            // Переключатель Вход / Регистрация
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (c.isDark) Color(0x0FFFFFFF) else Color(0xFFEAE8E2))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                SegmentTab("Вход", active = true, Modifier.weight(1f))
                SegmentTab("Регистрация", active = false, Modifier.weight(1f))
            }

            Column(
                Modifier.fillMaxWidth().padding(top = 22.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                LoginField(label = "Email", value = "anya@post.ru", icon = DuoIcons.Mail)
                LoginField(
                    label = "Пароль",
                    value = "••••••••",
                    icon = DuoIcons.Lock,
                    trailingIcon = DuoIcons.Eye,
                    focused = true,
                )
                DuoText(
                    "Забыл пароль?", 13, c.accent, FontWeight.Medium,
                    modifier = Modifier.align(Alignment.End),
                )
                PrimaryButton("Войти", Modifier.padding(top = 4.dp), height = 54.dp, onClick = onLogin)
            }

            Spacer(Modifier.weight(1f))
            Row(Modifier.padding(top = 22.dp, bottom = 28.dp)) {
                DuoText("Нет аккаунта? ", 13, c.fgMuted)
                DuoText("Создать", 13, c.accent, FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SegmentTab(text: String, active: Boolean, modifier: Modifier = Modifier) {
    val c = DuoTheme.colors
    val bg = when {
        !active -> Color.Transparent
        c.isDark -> Color(0xFF1A1D23)
        else -> Color.White
    }
    Box(
        modifier.height(38.dp).clip(RoundedCornerShape(9.dp)).background(bg),
        contentAlignment = Alignment.Center,
    ) {
        DuoText(
            text, 14,
            if (active) c.fg else c.fgMuted,
            if (active) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun LoginField(
    label: String,
    value: String,
    icon: ImageVector,
    trailingIcon: ImageVector? = null,
    focused: Boolean = false,
) {
    val c = DuoTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        DuoText(label, 12, c.fgMuted, FontWeight.Medium, modifier = Modifier.padding(start = 2.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(c.surfaceInput)
                .border(
                    if (focused) 1.5.dp else 1.dp,
                    if (focused) c.accent else c.borderStrong,
                    RoundedCornerShape(13.dp),
                )
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            DuoIcon(icon, c.fgMuted, 18.dp)
            DuoText(value, 15, c.fg, modifier = Modifier.weight(1f))
            if (trailingIcon != null) DuoIcon(trailingIcon, c.fgMuted, 18.dp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF07080A, heightDp = 852, widthDp = 392)
@Composable
private fun LoginPreviewDark() {
    DuoGoalTheme(darkTheme = true) { LoginScreen() }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF, heightDp = 852, widthDp = 392)
@Composable
private fun LoginPreviewLight() {
    DuoGoalTheme(darkTheme = false) { LoginScreen() }
}
