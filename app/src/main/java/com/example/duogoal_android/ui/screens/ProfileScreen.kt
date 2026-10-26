package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.Avatar
import com.example.duogoal_android.ui.components.BellWithDot
import com.example.duogoal_android.ui.components.DuoCard
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.GoalProgressRing
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.components.StatusChip
import com.example.duogoal_android.ui.components.SecondaryButton
import androidx.compose.ui.platform.testTag
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

@Composable
fun ProfileScreen(
    onOpenNotifications: () -> Unit = {}, onOpenActiveGoal: () -> Unit = {},
    onOpenWaitingGoal: () -> Unit = {}, onCreateGoal: () -> Unit = {},
    onLogout: (() -> Unit)? = null,
) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg).verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            DuoText("Твой путь", 30, c.fg, FontWeight.SemiBold, letterSpacing = -0.035, modifier = Modifier.weight(1f))
            BellWithDot(onClick = onOpenNotifications)
        }
        DuoCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Avatar("АС", c.avatar1, 60.dp, 22)
                Column(Modifier.weight(1f)) {
                    DuoText("Аня Соколова", 21, c.fg, FontWeight.SemiBold, letterSpacing = -0.02)
                    DuoText("@anya · с марта в DuoGoal", 12, c.fgMuted, modifier = Modifier.padding(top = 4.dp))
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 24.dp).clip(RoundedCornerShape(16.dp)).background(c.bg).padding(vertical = 16.dp)) {
                Stat("47", "поддержек", Modifier.weight(1f))
                Stat("6", "достижений", Modifier.weight(1f))
                Stat("128", "дней пути", Modifier.weight(1f))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            DuoText("ТВОИ ЦЕЛИ", 11, c.accentFg, FontWeight.SemiBold, letterSpacing = 0.09)
            DuoText("То, что имеет значение.", 21, c.fg, FontWeight.SemiBold, letterSpacing = -0.025)
        }
        DuoCard(Modifier.clip(RoundedCornerShape(24.dp)).clickable(role = Role.Button, onClick = onOpenActiveGoal)) {
            StatusChip("В процессе", c.successSoft, c.success)
            DuoText("Английский каждый день", 19, c.fg, FontWeight.SemiBold, modifier = Modifier.padding(top = 14.dp))
            Row(Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GoalProgressRing(6, 14)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DuoText("С Мишей", 15, c.fg, FontWeight.Medium)
                    DuoText("6 дней вы выбираете свою цель вместе.", 12, c.fgMuted, lineHeight = 1.6)
                    DuoIcon(DuoIcons.ArrowRight, c.accentFg, 20.dp)
                }
            }
        }
        GoalCard("Утренняя растяжка", "Запрос у Кати · 21 день", "Ожидание ответа", c.warningSoft, c.warning, onOpenWaitingGoal)
        GoalCard("Читать 20 минут в день", "30 дней для новых историй", "Ищем напарника", c.accentSoft, c.accentFg)
        GoalCard("Испанский с нуля", "С Леной · 30 из 30 дней", "Завершена", c.successSoft, c.success)
        PrimaryButton("Создать новую цель", icon = DuoIcons.Plus, onClick = onCreateGoal)
        if (onLogout != null) {
            SecondaryButton("Выйти из аккаунта", Modifier.testTag("profile-logout"), onClick = onLogout)
        }
    }
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier) {
    val c = DuoTheme.colors
    Column(modifier.padding(horizontal = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        DuoText(value, 24, c.fg, FontWeight.SemiBold, letterSpacing = -0.03)
        DuoText(label, 11, c.fgMuted, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun GoalCard(
    title: String, subtitle: String, status: String,
    statusBackground: androidx.compose.ui.graphics.Color, statusColor: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)? = null,
) {
    val c = DuoTheme.colors
    val interaction = if (onClick != null) Modifier.clip(RoundedCornerShape(24.dp)).clickable(role = Role.Button, onClick = onClick) else Modifier
    DuoCard(interaction) {
        StatusChip(status, statusBackground, statusColor)
        DuoText(title, 17, c.fg, FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp))
        DuoText(subtitle, 12, c.fgMuted, modifier = Modifier.padding(top = 6.dp))
    }
}

@Preview(name = "Профиль · светлая", widthDp = 392, heightDp = 852)
@Preview(name = "Профиль · тёмная", widthDp = 392, heightDp = 852, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfilePreview() { DuoGoalTheme { ProfileScreen() } }
