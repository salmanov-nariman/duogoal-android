package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.duogoal_android.ui.components.ScreenTopBar
import com.example.duogoal_android.ui.components.SecondaryButton
import com.example.duogoal_android.ui.components.StatusChip
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 6b — Цель (активна). */
@Composable
fun GoalActiveScreen(onBack: () -> Unit = {}) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        ScreenTopBar("", onBack = onBack, trailing = {
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                DuoIcon(DuoIcons.MoreHorizontal, c.fgMuted, 20.dp)
            }
        })
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(Modifier.padding(horizontal = 4.dp)) {
                DuoText(
                    "ЯЗЫКИ · 14 ДНЕЙ · ДО 21:00", 11, c.fgMuted, FontWeight.SemiBold,
                    letterSpacing = 0.08,
                )
                DuoText(
                    "Английский каждый день", 22, c.fg, FontWeight.Bold,
                    letterSpacing = -0.015, modifier = Modifier.padding(top = 6.dp),
                )
            }

            // Карточка стрика пары
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(c.surface)
                    .border(1.dp, c.border, RoundedCornerShape(22.dp))
                    .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar("АС", c.avatar1, 56.dp, 19, ringColor = c.surface, ringWidth = 2.5.dp)
                    Box(Modifier.width(34.dp).height(3.dp).background(c.accent))
                    Avatar("МК", c.avatar3, 56.dp, 19, ringColor = c.surface, ringWidth = 2.5.dp)
                }
                DuoText(
                    "6", 52, c.fg, FontWeight.Bold, letterSpacing = -0.02,
                    modifier = Modifier.padding(top = 8.dp),
                )
                DuoText("дней вместе · стрик пары", 13, c.fgMuted)

                // Календарь на 14 дней
                Column(
                    Modifier.fillMaxWidth().padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(6) { DayCell(state = DayState.Done, modifier = Modifier.weight(1f)) }
                        DayCell(state = DayState.Today, label = "7", modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (d in 8..14) {
                            DayCell(state = DayState.Future, label = "$d", modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Сегодняшний отчёт
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(c.surface)
                    .border(1.dp, c.border, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        DuoText("Сегодня · день 7", 15, c.fg, FontWeight.SemiBold)
                        DuoText(
                            "Отчёт до 21:00 · осталось 3 часа", 12, c.fgMuted,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    StatusChip("✓ Миша отчитался", c.successSoft, c.success)
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.accent),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DuoIcon(DuoIcons.Camera, c.onAccent, 17.dp)
                    DuoText("Отправить отчёт", 15, c.onAccent, FontWeight.SemiBold)
                }
            }

            SecondaryButton("Поделиться в ленте", height = 46.dp, icon = DuoIcons.Share)
            Box(Modifier.height(8.dp))
        }
    }
}

private enum class DayState { Done, Today, Future }

@Composable
private fun DayCell(state: DayState, label: String = "", modifier: Modifier = Modifier) {
    val c = DuoTheme.colors
    val base = modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp))
    when (state) {
        DayState.Done -> Box(base.background(c.accentSoft), contentAlignment = Alignment.Center) {
            DuoIcon(DuoIcons.Check, c.accent, 13.dp)
        }
        DayState.Today -> Box(
            base.border(1.5.dp, c.accent, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            DuoText(label, 12, c.fg, FontWeight.SemiBold)
        }
        DayState.Future -> Box(base.background(c.surfaceInput), contentAlignment = Alignment.Center) {
            DuoText(label, 11, c.fgSubtle)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF07080A, heightDp = 852, widthDp = 392)
@Composable
private fun GoalActivePreviewDark() {
    DuoGoalTheme(darkTheme = true) { GoalActiveScreen() }
}
