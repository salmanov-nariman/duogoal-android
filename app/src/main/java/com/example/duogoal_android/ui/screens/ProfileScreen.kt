package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.duogoal_android.ui.components.BellWithDot
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.components.SectionLabel
import com.example.duogoal_android.ui.components.StatusChip
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 4 — Профиль. */
@Composable
fun ProfileScreen(
    onOpenNotifications: () -> Unit = {},
    onOpenActiveGoal: () -> Unit = {},
    onOpenWaitingGoal: () -> Unit = {},
    onCreateGoal: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        // Шапка
        Box(Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 12.dp)) {
            Box(
                Modifier.size(44.dp).align(Alignment.CenterStart),
                contentAlignment = Alignment.Center,
            ) {
                DuoIcon(DuoIcons.Settings, c.fg, 21.dp)
            }
            DuoText(
                "Профиль", 18, c.fg, FontWeight.SemiBold, letterSpacing = -0.01,
                modifier = Modifier.align(Alignment.Center),
            )
            Box(Modifier.align(Alignment.CenterEnd)) {
                BellWithDot(onClick = onOpenNotifications)
            }
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 2.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Карточка пользователя
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(c.surface)
                    .border(1.dp, c.border, RoundedCornerShape(22.dp))
                    .padding(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Avatar("АС", c.avatar1, 60.dp, 24, ringColor = c.border, ringWidth = 3.dp)
                    Column {
                        DuoText("Аня Соколова", 20, c.fg, FontWeight.Bold, letterSpacing = -0.01)
                        DuoText("@anya · в DuoGoal с марта", 12, c.fgMuted, modifier = Modifier.padding(top = 2.dp))
                    }
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                        .height(1.dp)
                        .background(c.border)
                )
                Row(
                    Modifier.fillMaxWidth().padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Stat("47", "лайков", Modifier.weight(1f))
                    Box(Modifier.width(1.dp).height(30.dp).background(c.border))
                    Stat("6", "достижений", Modifier.weight(1f))
                    Box(Modifier.width(1.dp).height(30.dp).background(c.border))
                    Stat("128", "дней всего", Modifier.weight(1f))
                }
            }

            SectionLabel("Цели", Modifier.padding(horizontal = 4.dp))

            // Активная цель с прогрессом
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(c.surface)
                    .border(1.dp, c.border, RoundedCornerShape(18.dp))
                    .clickable(onClick = onOpenActiveGoal)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        DuoText("Английский каждый день", 15, c.fg, FontWeight.SemiBold)
                        DuoText("с Мишей · день 6 из 14", 12, c.fgMuted, modifier = Modifier.padding(top = 2.dp))
                    }
                    StatusChip("активна", c.successSoft, c.success)
                }
                Row(
                    Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row {
                        Avatar("АС", c.avatar1, 26.dp, 10, ringColor = c.surface, ringWidth = 2.dp)
                        Box(Modifier.padding(start = 17.dp)) {
                            Avatar("МК", c.avatar3, 26.dp, 10, ringColor = c.surface, ringWidth = 2.dp)
                        }
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (c.isDark) c.borderStrong else c.navBorder),
                    ) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.43f)
                                .background(c.accent)
                        )
                    }
                    DuoText("стрик 6", 12, c.accent, FontWeight.SemiBold)
                }
            }

            GoalCard(
                title = "Утренняя растяжка",
                subtitle = "запрос у Кати · 21 день",
                chipText = "ожидание ответа",
                chipBg = c.warningSoft,
                chipFg = c.warning,
                onClick = onOpenWaitingGoal,
            )
            GoalCard(
                title = "Читать 20 минут в день",
                subtitle = "30 дней",
                chipText = "без напарника",
                chipBg = c.surfaceInput,
                chipFg = c.fgMuted,
            )
            GoalCard(
                title = "Испанский с нуля",
                subtitle = "с Леной · 30 из 30",
                chipText = "завершена",
                chipBg = c.accentSoft,
                chipFg = c.accent,
            )

            PrimaryButton(
                "Создать новую цель",
                Modifier.padding(top = 2.dp, bottom = 8.dp),
                icon = DuoIcons.Plus,
                onClick = onCreateGoal,
            )
        }
    }
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier = Modifier) {
    val c = DuoTheme.colors
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        DuoText(value, 22, c.fg, FontWeight.Bold, letterSpacing = -0.01)
        DuoText(label, 11, c.fgMuted, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun GoalCard(
    title: String,
    subtitle: String,
    chipText: String,
    chipBg: androidx.compose.ui.graphics.Color,
    chipFg: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(c.surface)
            .border(1.dp, c.border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            DuoText(title, 15, c.fg, FontWeight.SemiBold)
            DuoText(subtitle, 12, c.fgMuted, modifier = Modifier.padding(top = 2.dp))
        }
        StatusChip(chipText, chipBg, chipFg)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF07080A, heightDp = 800, widthDp = 392)
@Composable
private fun ProfilePreviewDark() {
    DuoGoalTheme(darkTheme = true) { ProfileScreen() }
}
