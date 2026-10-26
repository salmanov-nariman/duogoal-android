package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.DuoCard
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.GoalProgressRing
import com.example.duogoal_android.ui.components.PairedAvatars
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.components.ScreenTopBar
import com.example.duogoal_android.ui.components.SecondaryButton
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

@Composable
fun GoalActiveScreen(onBack: () -> Unit = {}, onOpenChat: () -> Unit = {}) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        ScreenTopBar("Ваша цель", onBack = onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            Column {
                DuoText("ЯЗЫКИ · 14 ДНЕЙ", 11, c.accentFg, FontWeight.SemiBold, letterSpacing = 0.1)
                DuoText("Английский.\nКаждый день.", 34, c.fg, FontWeight.SemiBold, letterSpacing = -0.04, lineHeight = 1.1, modifier = Modifier.padding(top = 12.dp))
                Row(Modifier.padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PairedAvatars()
                    DuoText("Ты и Миша · 14 дней вместе", 13, c.fgMuted, modifier = Modifier.weight(1f))
                }
            }
            DuoCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    GoalProgressRing(6, 14)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DuoText("Хороший ритм.\nПродолжайте!", 17, c.fg, FontWeight.SemiBold, lineHeight = 1.3)
                        DuoText("Шесть дней подряд вы выбираете свою цель.", 12, c.fgMuted, lineHeight = 1.6)
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DuoText("ВАША НЕДЕЛЯ", 11, c.fgMuted, FontWeight.Medium, letterSpacing = 0.08)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС").forEachIndexed { index, label ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DuoText(label, 10, c.fgMuted, FontWeight.Medium)
                            Box(
                                Modifier.fillMaxWidth().aspectRatio(1f).clip(CircleShape).background(if (index < 6) c.successSoft else c.accent),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (index < 6) DuoIcon(DuoIcons.Check, c.success, 14.dp)
                                else DuoText("7", 12, c.onAccent, FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
            DuoCard {
                DuoText("СЕГОДНЯ · ДЕНЬ 7", 11, c.fgMuted, FontWeight.Medium, letterSpacing = 0.06)
                Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DuoIcon(DuoIcons.Check, c.success, 14.dp)
                    DuoText("Миша уже поделился прогрессом", 12, c.success)
                }
                DuoText("30 минут для будущего себя", 17, c.fg, FontWeight.SemiBold, modifier = Modifier.padding(top = 14.dp))
                DuoText("Практика английского и короткий отчёт.\nНапарник ждёт твоего шага.", 13, c.fgMuted, lineHeight = 1.6, modifier = Modifier.padding(top = 8.dp))
                Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DuoIcon(DuoIcons.Clock, c.fgMuted, 14.dp)
                    DuoText("Отчёт до 21:00", 12, c.fgMuted)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryButton("Поделиться прогрессом", icon = DuoIcons.Plus)
                SecondaryButton("Открыть чат с напарником", icon = DuoIcons.Chat, onClick = onOpenChat)
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Preview(name = "Цель · светлая", widthDp = 392, heightDp = 852)
@Preview(name = "Цель · тёмная", widthDp = 392, heightDp = 852, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GoalActivePreview() { DuoGoalTheme { GoalActiveScreen() } }
