package com.example.duogoal_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.Avatar
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.PrimaryButton
import com.example.duogoal_android.ui.components.ScreenTopBar
import com.example.duogoal_android.ui.components.SectionLabel
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 6d — Выбор напарника. */
@Composable
fun PartnerPickerScreen(
    onBack: () -> Unit = {},
    onSendRequest: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        ScreenTopBar("Выбор напарника", onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, top = 12.dp)) {
            DuoText("Найди своего человека.", 24, c.fg, androidx.compose.ui.text.font.FontWeight.SemiBold, letterSpacing = -0.03, modifier = Modifier.padding(bottom = 20.dp))
            // Поиск
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(c.surfaceInput)
                    .border(1.dp, c.borderStrong, RoundedCornerShape(13.dp))
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                DuoIcon(DuoIcons.Search, c.fgMuted, 18.dp)
                DuoText("Имя или ник друга", 15, c.fgSubtle)
            }

            SectionLabel(
                "С похожей целью",
                Modifier.padding(start = 4.dp, end = 4.dp, top = 20.dp, bottom = 12.dp),
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PartnerRow("МК", c.avatar3, "Миша Ковалёв", "Английский каждый день · 14 дней", selected = true)
                PartnerRow("ЛГ", c.avatar5, "Лена Гурьева", "Английский по 30 минут · 21 день", selected = false)
                PartnerRow("ПО", c.avatar2, "Павел Осипов", "Разговорный английский · 30 дней", selected = false)
            }

            Spacer(Modifier.weight(1f))
            Column(
                Modifier.padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PrimaryButton("Отправить запрос", onClick = onSendRequest)
                DuoText("Миша получит приглашение в эту цель", 12, c.fgMuted)
            }
        }
    }
}

@Composable
private fun PartnerRow(
    initials: String,
    avatarColor: Color,
    name: String,
    goal: String,
    selected: Boolean,
) {
    val c = DuoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(c.surface)
            .border(
                if (selected) 1.5.dp else 1.dp,
                if (selected) c.accentFg else c.border,
                RoundedCornerShape(24.dp),
            )
            .clickable {}
            .padding(horizontal = 15.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Avatar(initials, avatarColor, 44.dp, 16)
        Column(Modifier.weight(1f)) {
            DuoText(name, 15, c.fg, androidx.compose.ui.text.font.FontWeight.SemiBold)
            DuoText(goal, 12, c.fgMuted, modifier = Modifier.padding(top = 2.dp))
        }
        Box(
            Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) c.accentFg else c.borderStrong, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(c.accentFg))
            }
        }
    }
}

@Preview(name = "Напарник · светлая", heightDp = 852, widthDp = 392)
@Preview(name = "Напарник · тёмная", heightDp = 852, widthDp = 392, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PartnerPickerPreviewDark() {
    DuoGoalTheme { PartnerPickerScreen() }
}
