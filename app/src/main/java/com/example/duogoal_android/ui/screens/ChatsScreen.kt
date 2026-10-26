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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.Avatar
import com.example.duogoal_android.ui.components.BellWithDot
import com.example.duogoal_android.ui.components.DuoCard
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.StatusChip
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

@Composable
fun ChatsScreen(onOpenChat: () -> Unit = {}, onOpenNotifications: () -> Unit = {}) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg).verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            DuoText("На связи", 30, c.fg, FontWeight.SemiBold, letterSpacing = -0.035, modifier = Modifier.weight(1f))
            BellWithDot(onClick = onOpenNotifications)
        }
        DuoText("Поддержка ближе, чем кажется.", 14, c.fgMuted, modifier = Modifier.padding(bottom = 6.dp))
        ChatCard("МК", c.avatar3, "Миша Ковалёв", "14:02", "Английский каждый день", "Отчёт · день 6 — 30 минут практики", unread = true, reported = true, onClick = onOpenChat)
        ChatCard("КМ", c.avatar4, "Катя Мельник", "Вчера", "Утренняя растяжка", "Приняла приглашение — начинаем", onClick = onOpenChat)
        ChatCard("ЛГ", c.avatar5, "Лена Гурьева", "12 мая", "Испанский с нуля · завершена", "Спасибо за эти 30 дней", onClick = onOpenChat)
    }
}

@Composable
private fun ChatCard(
    initials: String, color: Color, name: String, time: String, goal: String, message: String,
    unread: Boolean = false, reported: Boolean = false, onClick: () -> Unit,
) {
    val c = DuoTheme.colors
    DuoCard(Modifier.clip(RoundedCornerShape(24.dp)).clickable(role = Role.Button, onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Avatar(initials, color, 46.dp, 16)
            Column(Modifier.weight(1f)) {
                DuoText(name, 16, c.fg, FontWeight.SemiBold)
                DuoText(time, 11, if (unread) c.accentFg else c.fgMuted, modifier = Modifier.padding(top = 3.dp))
            }
            if (unread) StatusChip("2", c.accent, c.onAccent)
        }
        DuoText(goal, 12, c.accentFg, FontWeight.Medium, modifier = Modifier.padding(top = 16.dp))
        DuoText(message, 14, if (unread) c.fg else c.fgMuted, lineHeight = 1.5, modifier = Modifier.padding(top = 6.dp))
        if (reported) StatusChip("✓ Напарник отчитался сегодня", c.successSoft, c.success, Modifier.padding(top = 12.dp))
    }
}

@Preview(name = "Чаты · светлая", widthDp = 392, heightDp = 852)
@Preview(name = "Чаты · тёмная", widthDp = 392, heightDp = 852, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatsPreview() { DuoGoalTheme { ChatsScreen() } }
