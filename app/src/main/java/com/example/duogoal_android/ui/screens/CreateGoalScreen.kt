package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
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
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            DuoText("Начни с маленького шага.", 24, c.fg, FontWeight.SemiBold, letterSpacing = -0.03)
            FormField("Название") {
                StaticInput(text = "Английский каждый день")
            }
            FormField("Категория") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip("Языки", selected = true)
                    CategoryChip("Спорт", selected = false)
                    CategoryChip("Учёба", selected = false)
                    CategoryChip("Здоровье", selected = false)
                }
            }
            FormField("Теги") {
                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(13.dp))
                        .background(c.surfaceInput)
                        .border(1.dp, c.borderStrong, RoundedCornerShape(13.dp))
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TagChip("разговор")
                    TagChip("грамматика")
                    DuoText("слова…", 14, c.fgMuted)
                }
            }
            FormField("Длительность") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DurationChip("7 дней", selected = false)
                    DurationChip("14 дней", selected = true)
                    DurationChip("21 день", selected = false)
                    DurationChip("30 дней", selected = false)
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
            .heightIn(min = 40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) c.accentSoft else c.surfaceInput)
            .border(1.dp, if (selected) c.accentFg else c.borderStrong, RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        DuoText(text, 13, if (selected) c.accentFg else c.fgMuted, FontWeight.Medium)
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
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) c.accentSoft else c.surfaceInput)
            .border(1.dp, if (selected) c.accentFg else c.borderStrong, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        DuoText(
            text, 13,
            if (selected) c.accentFg else c.fgMuted,
            if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Preview(name = "Создание · светлая", heightDp = 852, widthDp = 392)
@Preview(name = "Создание · тёмная", heightDp = 852, widthDp = 392, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreateGoalPreviewDark() {
    DuoGoalTheme { CreateGoalScreen() }
}
