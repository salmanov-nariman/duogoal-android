package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import com.example.duogoal_android.ui.components.BellWithDot
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 3 — Лента (immersive). */
@Composable
fun FeedScreen(onOpenNotifications: () -> Unit = {}) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        // Шапка
        Box(Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 12.dp)) {
            Box(
                Modifier.size(44.dp).align(Alignment.CenterStart),
                contentAlignment = Alignment.Center,
            ) {
                DuoIcon(DuoIcons.Filter, c.fg, 21.dp)
            }
            DuoText(
                "Лента", 18, c.fg, FontWeight.SemiBold, letterSpacing = -0.01,
                modifier = Modifier.align(Alignment.Center),
            )
            Box(Modifier.align(Alignment.CenterEnd)) {
                BellWithDot(onClick = onOpenNotifications)
            }
        }

        // Истории
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StoryAdd()
            Story("МК", c.avatar3, "Миша")
            Story("ЛГ", c.avatar5, "Лена")
            Story("ПО", c.avatar2, "Павел")
            Story("ДК", c.avatar4, "Дарья")
            Story("ИЛ", c.avatar6, "Игорь")
        }

        // Hero-карточка поста
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(c.surface)
                .border(1.dp, if (c.isDark) Color(0x52FFFFFF) else Color(0x24000000), RoundedCornerShape(28.dp)),
        ) {
            // Заглушка фото прогресса
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            if (c.isDark)
                                listOf(Color(0xFF3A4152), Color(0xFF212530), Color(0xFF12151B))
                            else
                                listOf(Color(0xFFD8D4C8), Color(0xFFC9C4B6), Color(0xFFB4AF9F))
                        )
                    )
            )
            // Затемнение сверху и снизу
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color(0x8C07080A),
                            0.26f to Color.Transparent,
                            0.44f to Color.Transparent,
                            0.74f to Color(0x9907080A),
                            1f to Color(0xF007080A),
                        )
                    )
            )
            // Верхний ряд: категория + меню
            Row(
                Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0x1FFFFFFF))
                        .border(1.dp, Color(0x47FFFFFF), RoundedCornerShape(999.dp))
                        .padding(horizontal = 15.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    DuoText("ЯЗЫКИ", 11, Color(0xFFF7F6F3), FontWeight.SemiBold, letterSpacing = 0.09)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.size(34.dp).clip(CircleShape).background(Color(0x1AFFFFFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    DuoIcon(DuoIcons.MoreHorizontal, Color(0xFFF7F6F3), 18.dp)
                }
            }
            // Нижний блок поста
            Column(
                Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    Row {
                        Avatar("АС", c.avatar1, 44.dp, 15, ringColor = Color(0xD9FFFFFF))
                        Box(Modifier.padding(start = 28.dp)) {
                            Avatar("МК", c.avatar3, 44.dp, 15, ringColor = Color(0xD9FFFFFF))
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        DuoText("14", 46, Color.White, FontWeight.Bold, letterSpacing = -0.02)
                        DuoText(
                            "ДНЕЙ ПОДРЯД", 10, Color(0xB3FFFFFF), FontWeight.Medium,
                            letterSpacing = 0.12, modifier = Modifier.padding(top = 5.dp),
                        )
                    }
                }
                Column {
                    DuoText("Новый рубеж — 14 дней вместе.", 20, Color.White, FontWeight.Bold, letterSpacing = -0.01)
                    DuoText(
                        "Ни одного пропуска. Спасибо, что рядом.", 13, Color(0xB8FFFFFF),
                        modifier = Modifier.padding(top = 5.dp),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DuoIcon(DuoIcons.Heart, Color.White, 20.dp)
                    DuoText("128", 14, Color.White, FontWeight.Medium, modifier = Modifier.padding(start = 7.dp))
                    Box(Modifier.width(22.dp))
                    DuoIcon(DuoIcons.Comment, Color.White, 20.dp)
                    DuoText("16", 14, Color.White, FontWeight.Medium, modifier = Modifier.padding(start = 7.dp))
                    Spacer(Modifier.weight(1f))
                    DuoIcon(DuoIcons.Bookmark, Color(0xD9FFFFFF), 20.dp)
                }
            }
        }

        // Точки-пейджер
        Row(
            Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(width = 20.dp, height = 6.dp).clip(RoundedCornerShape(99.dp)).background(c.fg))
            repeat(3) {
                Box(
                    Modifier.size(6.dp).clip(CircleShape)
                        .background(if (c.isDark) Color(0x47FFFFFF) else Color(0x33000000))
                )
            }
        }
    }
}

@Composable
private fun StoryAdd() {
    val c = DuoTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Box(
            Modifier
                .size(60.dp)
                .clip(CircleShape)
                .padding(4.dp)
                .clip(CircleShape)
                .background(c.surface)
                .border(1.5.dp, c.fgMuted, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            DuoIcon(DuoIcons.Plus, c.fgMuted, 20.dp)
        }
        DuoText("Ваша история", 11, c.fgMuted, maxLines = 1, modifier = Modifier.width(58.dp))
    }
}

@Composable
private fun Story(initials: String, color: Color, name: String) {
    val c = DuoTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Box(
            Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (c.isDark) Color(0x4DFFFFFF) else Color(0x33000000)),
            contentAlignment = Alignment.Center,
        ) {
            Avatar(initials, color, 52.dp, 16)
        }
        DuoText(name, 11, c.fgMuted, maxLines = 1)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF07080A, heightDp = 800, widthDp = 392)
@Composable
private fun FeedPreviewDark() {
    DuoGoalTheme(darkTheme = true) { FeedScreen() }
}
