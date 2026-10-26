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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.Avatar
import com.example.duogoal_android.ui.components.DuoIcon
import com.example.duogoal_android.ui.components.DuoIcons
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.components.ScreenTopBar
import com.example.duogoal_android.ui.components.SectionLabel
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Экран 6h — Уведомления. */
@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {},
    onOpenInvitation: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Column(Modifier.fillMaxSize().background(c.bg)) {
        ScreenTopBar("Уведомления", onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            SectionLabel("Сегодня", Modifier.padding(horizontal = 20.dp, vertical = 6.dp))

            NotificationRow(
                iconBg = c.accentSoft,
                icon = { DuoIcon(DuoIcons.Bell, c.accentFg, 18.dp) },
                title = "Не забудь отчитаться",
                subtitle = "Английский каждый день · до 21:00",
                time = "18:00",
                unread = true,
            )
            NotificationRow(
                iconBg = c.successSoft,
                icon = { DuoIcon(DuoIcons.Check, c.success, 18.dp) },
                title = "Миша отчитался",
                subtitle = "День 6 — посмотри отчёт в чате",
                time = "14:02",
                unread = true,
            )

            // Приглашение с кнопками
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenInvitation)
                    .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Avatar("КМ", c.avatar4, 40.dp, 14)
                Column(Modifier.weight(1f)) {
                    DuoText("Катя зовёт тебя в напарники", 14, c.fg, FontWeight.Medium)
                    DuoText(
                        "Утренняя растяжка · 21 день", 12, c.fgMuted,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    Row(
                        Modifier.padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            Modifier
                                .height(34.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(c.accent)
                                .clickable(onClick = onOpenInvitation)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            DuoText("Принять", 13, c.onAccent, FontWeight.Medium)
                        }
                        Box(
                            Modifier
                                .height(34.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(c.surfaceInput)
                                .border(1.dp, c.border, RoundedCornerShape(9.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            DuoText("Отклонить", 13, c.fg, FontWeight.Medium)
                        }
                    }
                }
                DuoText("11:40", 11, c.fgMuted)
            }

            SectionLabel(
                "Вчера",
                Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 6.dp),
            )

            NotificationRow(
                iconBg = c.warningSoft,
                icon = { DuoIcon(DuoIcons.Clock, c.warning, 18.dp) },
                title = "Стрик под угрозой",
                subtitle = "Оставался час до 21:00",
                time = "20:00",
            )
            NotificationRow(
                iconBg = c.surfaceInput,
                icon = { DuoText("МК", 14, c.fg, FontWeight.SemiBold) },
                title = "Миша принял твой запрос",
                subtitle = "Английский каждый день — цель началась",
                time = "9:15",
            )
        }
    }
}

@Composable
private fun NotificationRow(
    iconBg: Color,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    time: String,
    unread: Boolean = false,
) {
    val c = DuoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
        Column(Modifier.weight(1f)) {
            DuoText(title, 14, c.fg, FontWeight.Medium)
            DuoText(subtitle, 12, c.fgMuted, modifier = Modifier.padding(top = 2.dp))
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            DuoText(time, 11, c.fgMuted)
            if (unread) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(c.accent))
            }
        }
    }
}

@Preview(name = "Уведомления · светлая", heightDp = 852, widthDp = 392)
@Preview(name = "Уведомления · тёмная", heightDp = 852, widthDp = 392, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NotificationsPreviewDark() {
    DuoGoalTheme { NotificationsScreen() }
}
