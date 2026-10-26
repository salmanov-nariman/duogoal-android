package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.Avatar
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 6g — Чат с напарником. */
@Composable
fun ChatScreen(onBack: () -> Unit = {}) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        // Шапка чата
        Row(
            Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier.size(width = 40.dp, height = 44.dp).clip(CircleShape).clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                DuoIcon(DuoIcons.ArrowLeft, c.fg, 22.dp)
            }
            Avatar("МК", c.avatar3, 38.dp, 14)
            Column(Modifier.weight(1f)) {
                DuoText("Миша Ковалёв", 15, c.fg, FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DuoText("Английский каждый день", 12, c.fgMuted)
                    Box(
                        Modifier.clip(RoundedCornerShape(999.dp)).background(c.accentSoft)
                            .padding(horizontal = 7.dp, vertical = 1.dp),
                    ) {
                        DuoText("стрик 6", 11, c.accent, FontWeight.SemiBold)
                    }
                }
            }
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                DuoIcon(DuoIcons.MoreHorizontal, c.fgMuted, 20.dp)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.divider))

        // Сообщения
        Column(
            Modifier.weight(1f).padding(top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DuoText(
                "сегодня", 11, c.fgSubtle,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            // Карточка отчёта (входящее)
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(0.82f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp))
                    .background(c.surface)
                    .border(
                        1.dp, c.border,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp),
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    DuoText(
                        "ОТЧЁТ · ДЕНЬ 6", 11, c.accent, FontWeight.SemiBold,
                        letterSpacing = 0.04, modifier = Modifier.weight(1f),
                    )
                    DuoText("14:02", 10, c.fgMuted)
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                if (c.isDark) listOf(Color(0xFF2A2F3A), Color(0xFF171A20))
                                else listOf(Color(0xFFE3E0D8), Color(0xFFCFCBC0))
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    DuoIcon(DuoIcons.Camera, if (c.isDark) Color(0xFF4A4F5A) else Color(0xFF9A968A), 24.dp)
                }
                DuoText("30 минут грамматики — добил Past Perfect.", 14, c.fg, lineHeight = 1.45)
            }

            // Исходящее сообщение
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Box(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxWidth(0.78f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 4.dp, bottomStart = 16.dp))
                        .background(c.accent)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DuoText("Сильно! Я сяду вечером после работы", 14, c.onAccent, lineHeight = 1.4)
                        DuoText("14:05", 10, c.onAccent.copy(alpha = 0.7f))
                    }
                }
            }

            // Входящее сообщение
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth(0.78f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp))
                        .background(c.surfaceInput)
                        .border(
                            1.dp, c.border,
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp),
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DuoText("Договорились, жду твой отчёт до 21:00", 14, c.fg, lineHeight = 1.4)
                        DuoText("14:06", 10, c.fgMuted)
                    }
                }
            }
        }

        // Нижняя панель
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.divider))
        Column(
            Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.accentSoft),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DuoIcon(DuoIcons.Camera, c.accent, 16.dp)
                DuoText("Отправить отчёт за сегодня", 14, c.accent, FontWeight.SemiBold)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(c.surfaceInput)
                        .border(1.dp, c.border, RoundedCornerShape(999.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DuoText("Сообщение", 15, c.fgSubtle)
                }
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(c.accent),
                    contentAlignment = Alignment.Center,
                ) {
                    DuoIcon(DuoIcons.Send, c.onAccent, 19.dp)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF07080A, heightDp = 852, widthDp = 392)
@Composable
private fun ChatPreviewDark() {
    DuoGoalTheme(darkTheme = true) { ChatScreen() }
}
