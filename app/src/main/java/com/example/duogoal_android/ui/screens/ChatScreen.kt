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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 6g — Чат с напарником. */
@Composable
fun ChatScreen(onBack: () -> Unit = {}) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        // Шапка чата
        Row(
            Modifier.fillMaxWidth().heightIn(min = 76.dp).padding(horizontal = 12.dp, vertical = 10.dp),
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
                DuoText("Английский каждый день", 12, c.fgMuted, maxLines = 1)
                DuoText("6 дней вместе", 11, c.accentFg, modifier = Modifier.padding(top = 3.dp))
            }
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                DuoIcon(DuoIcons.MoreHorizontal, c.fgMuted, 20.dp)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.divider))

        // Сообщения
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 20.dp),
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
                        "ОТЧЁТ · ДЕНЬ 6", 11, c.accentFg, FontWeight.SemiBold,
                        letterSpacing = 0.04, modifier = Modifier.weight(1f),
                    )
                    DuoText("14:02", 10, c.fgMuted)
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(c.accentSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    DuoIcon(DuoIcons.Camera, c.accentFg, 24.dp)
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
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        DuoText("Сильно! Я сяду вечером после работы", 14, c.onAccent, lineHeight = 1.4)
                        DuoText("14:05", 11, c.onAccent, modifier = Modifier.align(Alignment.End))
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
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        DuoText("Договорились, жду твой отчёт до 21:00", 14, c.fg, lineHeight = 1.4)
                        DuoText("14:06", 11, c.fgMuted, modifier = Modifier.align(Alignment.End))
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
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.accentSoft),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DuoIcon(DuoIcons.Camera, c.accentFg, 16.dp)
                DuoText("Отправить отчёт за сегодня", 14, c.accentFg, FontWeight.SemiBold, modifier = Modifier.padding(vertical = 10.dp))
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

@Preview(name = "Чат · светлая", heightDp = 852, widthDp = 392)
@Preview(name = "Чат · тёмная", heightDp = 852, widthDp = 392, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatPreviewDark() {
    DuoGoalTheme { ChatScreen() }
}
