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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.Avatar
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.ScreenTopBar
import com.example.duogoal_android.ui.components.StatusChip
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 6c — Цель в ожидании ответа напарника. */
@Composable
fun GoalWaitingScreen(onBack: () -> Unit = {}) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        ScreenTopBar("", onBack = onBack, trailing = {
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                DuoIcon(DuoIcons.MoreHorizontal, c.fgMuted, 20.dp)
            }
        })
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp)) {
            DuoText(
                "ЯЗЫКИ · 14 ДНЕЙ · ДО 21:00", 11, c.accentFg, FontWeight.SemiBold,
                letterSpacing = 0.08,
            )
            DuoText(
                "Английский\nкаждый день", 32, c.fg, FontWeight.SemiBold,
                letterSpacing = -0.015, modifier = Modifier.padding(top = 6.dp),
            )
            DuoText(
                "30 минут в день: приложение или разговорная практика.", 14, c.fgMuted,
                lineHeight = 1.5, modifier = Modifier.padding(top = 10.dp),
            )

            // Карточка ожидания
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(c.surface)
                    .border(1.dp, c.border, RoundedCornerShape(24.dp))
                    .padding(horizontal = 20.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.size(52.dp).clip(CircleShape).background(c.accentSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    DuoIcon(DuoIcons.Clock, c.accentFg, 24.dp)
                }
                DuoText(
                    "Ожидание ответа", 19, c.fg, FontWeight.Bold,
                    modifier = Modifier.padding(top = 14.dp),
                )
                DuoText(
                    "Миша ещё не ответил. Цель начнётся после согласия.", 13, c.fgMuted,
                    lineHeight = 1.5, modifier = Modifier.padding(top = 6.dp),
                )
            }

            // Карточка напарника
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(c.surface)
                    .border(1.dp, c.border, RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(Modifier.alpha(0.6f)) {
                    Avatar("МК", c.avatar3, 44.dp, 16)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DuoText("Миша Ковалёв", 15, c.fg, FontWeight.SemiBold)
                    StatusChip("запрос отправлен", c.warningSoft, c.warning)
                }
            }

            Spacer(Modifier.weight(1f))
            Column(
                Modifier.fillMaxWidth().padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                DuoText("Отменить цель", 14, c.danger, FontWeight.Medium)
                DuoText("Доступно, пока напарник не принял запрос", 11, c.fgSubtle)
            }
        }
    }
}

@Preview(name = "Ожидание · светлая", heightDp = 852, widthDp = 392)
@Preview(name = "Ожидание · тёмная", heightDp = 852, widthDp = 392, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GoalWaitingPreviewDark() {
    DuoGoalTheme { GoalWaitingScreen() }
}
