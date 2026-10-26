package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.components.ScreenTopBar
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 6a — Создание цели. */
@Composable
fun CreateGoalScreen(
    onBack: () -> Unit = {},
    onCreate: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        ScreenTopBar("Новая цель", onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            FormField("Название") {
                StaticInput(text = "Английский каждый день")
            }
            FormField("Категория") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip("Языки", selected = true)
                    CategoryChip("Спорт", selected = false)
                    CategoryChip("Учёба", selected = false)
                    CategoryChip("Здоровье", selected = false)
                }
            }
            FormField("Теги") {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(13.dp))
                        .background(c.surfaceInput)
                        .border(1.5.dp, c.accent, RoundedCornerShape(13.dp))
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TagChip("разговор")
                    TagChip("грамматика")
                    DuoText("слова…", 14, c.fgMuted)
                }
            }
            FormField("Длительность") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DurationChip("7 дней", selected = false, Modifier.weight(1f))
                    DurationChip("14 дней", selected = true, Modifier.weight(1f))
                    DurationChip("21 день", selected = false, Modifier.weight(1f))
                    DurationChip("30 дней", selected = false, Modifier.weight(1f))
                }
            }
            FormField("Дедлайн отчёта") {
                StaticInput(text = "21:00", icon = DuoIcons.Clock)
            }
            FormField("Описание") {
                StaticInput(text = "30 минут в день: приложение или разговор.")
            }
            PrimaryButton("Создать", Modifier.padding(top = 2.dp), onClick = onCreate)
        }
    }
}

@Composable
private fun FormField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        DuoText(
            label, 12, DuoTheme.colors.fgMuted, FontWeight.Medium,
            modifier = Modifier.padding(start = 2.dp),
        )
        content()
    }
}

@Composable
private fun StaticInput(text: String, icon: ImageVector? = null) {
    val c = DuoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 50.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(c.surfaceInput)
            .border(1.dp, c.borderStrong, RoundedCornerShape(13.dp))
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            DuoIcon(icon, c.fgMuted, 18.dp)
            Box(Modifier.size(10.dp))
        }
        DuoText(text, 15, c.fg)
    }
}

@Composable
private fun CategoryChip(text: String, selected: Boolean) {
    val c = DuoTheme.colors
    Box(
        Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) c.accentSoft else c.surfaceInput)
            .border(1.dp, if (selected) c.accent else c.borderStrong, RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        DuoText(text, 13, if (selected) c.accent else c.fgMuted, FontWeight.Medium)
    }
}

@Composable
private fun TagChip(text: String) {
    val c = DuoTheme.colors
    Row(
        Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(c.surfaceInput)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        DuoText(text, 13, c.fg)
        DuoIcon(DuoIcons.Close, c.fgMuted, 11.dp)
    }
}

@Composable
private fun DurationChip(text: String, selected: Boolean, modifier: Modifier = Modifier) {
    val c = DuoTheme.colors
    Box(
        modifier
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) c.accentSoft else c.surfaceInput)
            .border(1.dp, if (selected) c.accent else c.borderStrong, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        DuoText(
            text, 13,
            if (selected) c.accent else c.fgMuted,
            if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF07080A, heightDp = 852, widthDp = 392)
@Composable
private fun CreateGoalPreviewDark() {
    DuoGoalTheme(darkTheme = true) { CreateGoalScreen() }
}
