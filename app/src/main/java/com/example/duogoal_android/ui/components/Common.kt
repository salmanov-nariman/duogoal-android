package com.example.duogoal_android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.duogoal_android.ui.theme.DuoTheme
import androidx.compose.foundation.text.BasicText

@Composable
fun DuoIcon(
    icon: ImageVector,
    tint: Color,
    size: Dp = 21.dp,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = rememberVectorPainter(icon),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(size),
    )
}

@Composable
fun DuoText(
    text: String,
    size: Int,
    color: Color,
    weight: FontWeight = FontWeight.Normal,
    letterSpacing: Double = 0.0,
    lineHeight: Double? = null,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
) {
    BasicText(
        text = text,
        style = TextStyle(
            fontSize = size.sp,
            color = color,
            fontWeight = weight,
            letterSpacing = letterSpacing.em,
            lineHeight = lineHeight?.em ?: TextStyle.Default.lineHeight,
        ),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

/** Круглый аватар с инициалами. */
@Composable
fun Avatar(
    initials: String,
    color: Color,
    size: Dp = 44.dp,
    fontSize: Int = 16,
    ringColor: Color? = null,
    ringWidth: Dp = 2.5.dp,
    modifier: Modifier = Modifier,
) {
    var m = modifier.size(size).clip(CircleShape).background(color)
    if (ringColor != null) m = m.border(ringWidth, ringColor, CircleShape)
    Box(m, contentAlignment = Alignment.Center) {
        DuoText(initials, fontSize, Color.White, FontWeight.SemiBold)
    }
}

/** Пилюля-статус («активна», «ожидание ответа» и т.п.). */
@Composable
fun StatusChip(text: String, bg: Color, fg: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 11.dp, vertical = 4.dp)
    ) {
        DuoText(text, 12, fg, FontWeight.Medium)
    }
}

/** Заголовок секции — капс с разрядкой. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    DuoText(
        text.uppercase(),
        11,
        DuoTheme.colors.fgMuted,
        FontWeight.SemiBold,
        letterSpacing = 0.08,
        modifier = modifier,
    )
}

/** Главная фиолетовая кнопка. */
@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    height: Dp = 52.dp,
    icon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(c.accent)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            DuoIcon(icon, c.onAccent, 18.dp)
            Box(Modifier.size(8.dp))
        }
        DuoText(text, 16, c.onAccent, FontWeight.SemiBold)
    }
}

/** Вторичная кнопка на полупрозрачной подложке. */
@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    icon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    val c = DuoTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(c.surfaceInput)
            .border(1.dp, c.border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            DuoIcon(icon, c.fg, 16.dp)
            Box(Modifier.size(8.dp))
        }
        DuoText(text, 15, c.fg, FontWeight.Medium)
    }
}

/** Верхняя панель экрана: назад + заголовок + необязательная правая иконка. */
@Composable
fun ScreenTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val c = DuoTheme.colors
    Row(
        Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                DuoIcon(DuoIcons.ArrowLeft, c.fg, 22.dp)
            }
        }
        DuoText(
            title, 17, c.fg, FontWeight.SemiBold,
            letterSpacing = -0.01,
            modifier = Modifier.weight(1f).padding(start = if (onBack == null) 12.dp else 0.dp),
        )
        trailing?.invoke()
    }
}

/** Иконка-колокольчик с точкой непрочитанного. */
@Composable
fun BellWithDot(onClick: () -> Unit = {}) {
    val c = DuoTheme.colors
    Box(
        Modifier.size(44.dp).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DuoIcon(DuoIcons.Bell, c.fg, 21.dp)
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-11).dp, y = 10.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(c.accent)
        )
    }
}

enum class DuoTab { Feed, Chats, Profile }

/** Плавающая нижняя навигация. */
@Composable
fun DuoBottomNav(
    selected: DuoTab,
    onSelect: (DuoTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = DuoTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(c.navBg)
            .border(1.dp, c.navBorder, RoundedCornerShape(22.dp)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavItem(DuoIcons.Home, "Лента", selected == DuoTab.Feed) { onSelect(DuoTab.Feed) }
        NavItem(DuoIcons.Chat, "Чаты", selected == DuoTab.Chats) { onSelect(DuoTab.Chats) }
        NavItem(DuoIcons.User, "Профиль", selected == DuoTab.Profile) { onSelect(DuoTab.Profile) }
    }
}

@Composable
private fun NavItem(icon: ImageVector, label: String, active: Boolean, onClick: () -> Unit) {
    val c = DuoTheme.colors
    val tint = if (active) c.fg else c.fgSubtle
    Column(
        Modifier.size(width = 96.dp, height = 60.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        DuoIcon(icon, tint, 21.dp)
        Box(Modifier.height(4.dp))
        DuoText(label, 11, tint, if (active) FontWeight.SemiBold else FontWeight.Medium)
    }
}
