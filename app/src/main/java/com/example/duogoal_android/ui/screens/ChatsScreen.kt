package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.Avatar
import com.example.duogoal_android.ui.components.BellWithDot
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 6f — Список чатов. */
@Composable
fun ChatsScreen(
    onOpenChat: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(start = 20.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DuoText(
                "Чаты", 24, c.fg, FontWeight.Bold, letterSpacing = -0.02,
                modifier = Modifier.weight(1f),
            )
            BellWithDot(onClick = onOpenNotifications)
        }

        Column(Modifier.weight(1f)) {
            ChatRow(
                initials = "МК",
                avatarColor = c.avatar3,
                name = "Миша Ковалёв",
                time = "14:02",
                timeAccent = true,
                goal = "Английский каждый день",
                message = "Отчёт · день 6 — 30 минут",
                messageIsRead = false,
                unreadCount = "2",
                reportedToday = true,
                onClick = onOpenChat,
            )
            Divider()
            ChatRow(
                initials = "КМ",
                avatarColor = c.avatar4,
                name = "Катя Мельник",
                time = "вчера",
                goal = "Утренняя растяжка",
                message = "Приняла приглашение — начинаем",
            )
            Divider()
            ChatRow(
                initials = "ЛГ",
                avatarColor = c.avatar5,
                name = "Лена Гурьева",
                time = "12 мая",
                goal = "Испанский с нуля · завершена",
                message = "Спасибо за эти 30 дней",
            )
        }
    }
}

@Composable
private fun Divider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(DuoTheme.colors.divider)
    )
}

@Composable
private fun ChatRow(
    initials: String,
    avatarColor: Color,
    name: String,
    time: String,
    goal: String,
    message: String,
    timeAccent: Boolean = false,
    messageIsRead: Boolean = true,
    unreadCount: String? = null,
    reportedToday: Boolean = false,
    onClick: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Avatar(initials, avatarColor, 48.dp, 17)
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                DuoText(name, 15, c.fg, FontWeight.SemiBold, modifier = Modifier.weight(1f))
                DuoText(time, 11, if (timeAccent) c.accent else c.fgMuted)
            }
            DuoText(goal, 12, c.fgMuted, modifier = Modifier.padding(top = 1.dp))
            Row(
                Modifier.fillMaxWidth().padding(top = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DuoText(
                    message, 14,
                    if (messageIsRead) c.fgMuted else c.fg,
                    if (messageIsRead) FontWeight.Normal else FontWeight.Medium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                if (unreadCount != null) {
                    Box(
                        Modifier
                            .sizeIn(minWidth = 20.dp, minHeight = 20.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(c.accent)
                            .padding(horizontal = 7.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        DuoText(unreadCount, 11, c.onAccent)
                    }
                }
            }
            if (reportedToday) {
                Row(
                    Modifier
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(c.successSoft)
                        .padding(horizontal = 9.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    DuoIcon(DuoIcons.Check, c.success, 11.dp)
                    DuoText("напарник отчитался сегодня", 11, c.success, FontWeight.Medium)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF07080A, heightDp = 800, widthDp = 392)
@Composable
private fun ChatsPreviewDark() {
    DuoGoalTheme(darkTheme = true) { ChatsScreen() }
}
