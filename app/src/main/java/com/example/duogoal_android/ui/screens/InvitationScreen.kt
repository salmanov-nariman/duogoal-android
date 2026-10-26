package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.Avatar
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.components.ScreenTopBar
import com.example.duogoal_android.ui.components.SecondaryButton
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 6e — Входящее приглашение в напарники. */
@Composable
fun InvitationScreen(
    onBack: () -> Unit = {},
    onAccept: () -> Unit = {},
    onDecline: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        ScreenTopBar("Приглашение", onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, top = 12.dp)) {
            Column(
                Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar("АС", c.avatar1, 64.dp, 24, ringColor = c.bg, ringWidth = 3.dp)
                    Box(Modifier.width(30.dp).height(3.dp).background(c.accent))
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(c.surface)
                            .border(2.dp, c.borderStrong, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        DuoIcon(DuoIcons.User, c.fgSubtle, 24.dp)
                    }
                }
                DuoText(
                    "Аня зовёт тебя\nв напарники", 28, c.fg, FontWeight.SemiBold,
                    letterSpacing = -0.01, modifier = Modifier.widthIn(max = 280.dp),
                )
                DuoText(
                    "Вы будете идти к цели вместе и каждый день отчитываться друг другу.",
                    13, c.fgMuted, lineHeight = 1.5,
                    modifier = Modifier.widthIn(max = 280.dp),
                )
            }

            // Карточка цели
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(c.surface)
                    .border(1.dp, c.border, RoundedCornerShape(24.dp))
                    .padding(18.dp),
            ) {
                DuoText(
                    "ЯЗЫКИ · 14 ДНЕЙ · ДО 21:00", 11, c.fgMuted, FontWeight.SemiBold,
                    letterSpacing = 0.08,
                )
                DuoText(
                    "Английский каждый день", 17, c.fg, FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
                DuoText(
                    "30 минут в день: приложение или разговорная практика.", 13, c.fgMuted,
                    lineHeight = 1.5, modifier = Modifier.padding(top = 6.dp),
                )
            }

            Row(
                Modifier.padding(start = 4.dp, end = 4.dp, top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                DuoIcon(DuoIcons.Clock, c.fgMuted, 16.dp, Modifier.padding(top = 1.dp))
                DuoText(
                    "Пропуск отчёта у любого из вас обнуляет общий стрик пары",
                    12, c.fgMuted, lineHeight = 1.5,
                )
            }

            Spacer(Modifier.weight(1f))
            Column(
                Modifier.padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PrimaryButton("Принять", onClick = onAccept)
                SecondaryButton("Отклонить", onClick = onDecline)
            }
        }
    }
}

@Preview(name = "Приглашение · светлая", heightDp = 852, widthDp = 392)
@Preview(name = "Приглашение · тёмная", heightDp = 852, widthDp = 392, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InvitationPreviewDark() {
    DuoGoalTheme { InvitationScreen() }
}
