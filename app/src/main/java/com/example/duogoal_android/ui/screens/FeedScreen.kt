package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.duogoal_android.ui.components.DuoBrand
import com.example.duogoal_android.ui.components.DuoCard
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.PairedAvatars
import com.example.duogoal_android.ui.components.StatusChip
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

@Composable
fun FeedScreen(onOpenNotifications: () -> Unit = {}) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            DuoBrand(Modifier.weight(1f))
            BellWithDot(onClick = onOpenNotifications)
        }
        Column(Modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
            DuoText("МАЛЕНЬКИЕ ШАГИ. БОЛЬШИЕ ПЕРЕМЕНЫ.", 10, c.accentFg, FontWeight.SemiBold, letterSpacing = 0.07)
            DuoText("Вместе\nполучается больше.", 32, c.fg, FontWeight.SemiBold, letterSpacing = -0.04, lineHeight = 1.12, modifier = Modifier.padding(top = 12.dp))
        }
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Story("+", c.accentSoft, "Ты", add = true)
            Story("МК", c.avatar3, "Миша")
            Story("ЛГ", c.avatar5, "Лена")
            Story("ПО", c.avatar2, "Павел")
            Story("ДК", c.avatar4, "Дарья")
        }
        Column(Modifier.padding(horizontal = 24.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            DuoText("ИСТОРИИ ВАШИХ ПОБЕД", 11, c.fgMuted, FontWeight.Medium, letterSpacing = 0.08)
            DuoCard(padding = PaddingValues(0.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PairedAvatars(30.dp)
                    Column(Modifier.weight(1f)) {
                        DuoText("Аня и Миша", 14, c.fg, FontWeight.SemiBold)
                        DuoText("Английский · сегодня", 12, c.fgMuted)
                    }
                    DuoIcon(DuoIcons.MoreHorizontal, c.fgMuted, 20.dp)
                }
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 10.dp).clip(RoundedCornerShape(20.dp)).background(c.accentSoft).padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    StatusChip("МАЛЕНЬКАЯ БОЛЬШАЯ ПОБЕДА", c.surface, c.accentFg)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        DuoText("14", 80, c.accentFg, FontWeight.SemiBold, letterSpacing = -0.07, lineHeight = 1.0, modifier = Modifier.weight(1f))
                        Box(Modifier.size(70.dp).clip(CircleShape).background(c.surface), contentAlignment = Alignment.Center) {
                            DuoIcon(DuoIcons.Sparkles, c.accentFg, 32.dp)
                        }
                    }
                    DuoText("дней выбираем свою цель", 17, c.accentFg, FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 8.dp)) {
                        repeat(14) { Box(Modifier.weight(1f).height(5.dp).clip(CircleShape).background(c.accent)) }
                    }
                }
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DuoText("Новый рубеж — вместе.", 19, c.fg, FontWeight.SemiBold, letterSpacing = -0.02)
                    DuoText("Ни одного пропуска. Спасибо, что рядом.", 13, c.fgMuted, lineHeight = 1.6)
                    Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        DuoIcon(DuoIcons.Heart, c.accentFg, 20.dp)
                        DuoText("128", 13, c.fg, FontWeight.Medium)
                        Spacer(Modifier.width(10.dp))
                        DuoIcon(DuoIcons.Comment, c.fgMuted, 20.dp)
                        DuoText("16", 13, c.fgMuted)
                        Spacer(Modifier.weight(1f))
                        DuoIcon(DuoIcons.Bookmark, c.fgMuted, 20.dp)
                    }
                }
            }
            DuoCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Avatar("ЛГ", c.avatar5, 38.dp, 13)
                    Column {
                        DuoText("Лена Гурьева", 14, c.fg, FontWeight.SemiBold)
                        DuoText("Испанский с нуля · вчера", 12, c.fgMuted)
                    }
                }
                DuoText("30 дней, одно обещание себе.", 18, c.fg, FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp))
                DuoText("С напарником оказалось легче не откладывать. Продолжаем!", 13, c.fgMuted, lineHeight = 1.6, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun Story(initials: String, color: androidx.compose.ui.graphics.Color, name: String, add: Boolean = false) {
    val c = DuoTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(58.dp).border(1.5.dp, if (add) c.borderStrong else c.accentFg, CircleShape), contentAlignment = Alignment.Center) {
            if (add) Box(Modifier.size(48.dp).clip(CircleShape).background(c.surface), contentAlignment = Alignment.Center) { DuoIcon(DuoIcons.Plus, c.accentFg, 22.dp) }
            else Avatar(initials, color, 48.dp, 15)
        }
        DuoText(name, 12, c.fgMuted)
    }
}

@Preview(name = "Лента · светлая", widthDp = 392, heightDp = 852)
@Preview(name = "Лента · тёмная", widthDp = 392, heightDp = 852, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FeedPreview() { DuoGoalTheme { FeedScreen() } }
