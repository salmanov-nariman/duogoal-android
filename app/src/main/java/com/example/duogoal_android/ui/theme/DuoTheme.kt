package com.example.duogoal_android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Согласованная дизайн-система: тёплая светлая и графитовая тёмная темы.
 * Цвет заливки кнопок отделён от цвета акцентного текста для контраста.
 */
data class DuoColors(
    val isDark: Boolean,
    val bg: Color,
    val surface: Color,          // карточки
    val surfaceInput: Color,     // поля ввода, вторичные кнопки
    val border: Color,           // границы карточек
    val borderStrong: Color,     // границы полей ввода
    val divider: Color,
    val fg: Color,
    val fgMuted: Color,
    val fgSubtle: Color,
    val accent: Color,
    val accentFg: Color,
    val onAccent: Color,
    val accentSoft: Color,       // 16% акцента
    val success: Color,
    val successSoft: Color,
    val warning: Color,
    val warningSoft: Color,
    val danger: Color,
    val navBg: Color,
    val navBorder: Color,
    val avatar1: Color,
    val avatar2: Color,
    val avatar3: Color,
    val avatar4: Color,
    val avatar5: Color,
    val avatar6: Color,
    val avatarInk: Color,
)

val Accent = Color(0xFF6753DB)

val DuoDarkColors = DuoColors(
    isDark = true,
    bg = Color(0xFF14151B),
    surface = Color(0xFF1E2029),
    surfaceInput = Color(0xFF1E2029),
    border = Color(0xFF30313E),
    borderStrong = Color(0xFF484858),
    divider = Color(0xFF30313E),
    fg = Color(0xFFF3F1F9),
    fgMuted = Color(0xFFAEA9BD),
    fgSubtle = Color(0xFFAAA5B8),
    accent = Accent,
    accentFg = Color(0xFFB9AAFF),
    onAccent = Color.White,
    accentSoft = Color(0xFF2B2544),
    success = Color(0xFF92D4B6),
    successSoft = Color(0xFF233A32),
    warning = Color(0xFFE8C089),
    warningSoft = Color(0x2EE0A85A),
    danger = Color(0xFFE5837A),
    navBg = Color(0xFF1E2029),
    navBorder = Color(0xFF30313E),
    avatar1 = Color(0xFFD7BCA0),
    avatar2 = Color(0xFFB8CDBD),
    avatar3 = Color(0xFFC9C6E9),
    avatar4 = Color(0xFFE0B9B5),
    avatar5 = Color(0xFFCDC0DE),
    avatar6 = Color(0xFFE0CEA7),
    avatarInk = Color(0xFF3F374A),
)

val DuoLightColors = DuoColors(
    isDark = false,
    bg = Color(0xFFF7F6F2),
    surface = Color(0xFFFFFFFF),
    surfaceInput = Color(0xFFFFFFFF),
    border = Color(0xFFE7E4ED),
    borderStrong = Color(0xFFD0CBDC),
    divider = Color(0xFFE7E4ED),
    fg = Color(0xFF242334),
    fgMuted = Color(0xFF6B687A),
    fgSubtle = Color(0xFF716B80),
    accent = Accent,
    accentFg = Color(0xFF5E48C8),
    onAccent = Color.White,
    accentSoft = Color(0xFFEEEBFB),
    success = Color(0xFF2C795E),
    successSoft = Color(0xFFE5F1EB),
    warning = Color(0xFF9A6B25),
    warningSoft = Color(0x2EE0A85A),
    danger = Color(0xFFB8514A),
    navBg = Color.White,
    navBorder = Color(0xFFE7E4ED),
    avatar1 = Color(0xFFD7BCA0),
    avatar2 = Color(0xFFB8CDBD),
    avatar3 = Color(0xFFC9C6E9),
    avatar4 = Color(0xFFE0B9B5),
    avatar5 = Color(0xFFCDC0DE),
    avatar6 = Color(0xFFE0CEA7),
    avatarInk = Color(0xFF3F374A),
)

val LocalDuoColors = staticCompositionLocalOf { DuoDarkColors }

object DuoTheme {
    val colors: DuoColors
        @Composable @ReadOnlyComposable get() = LocalDuoColors.current
}

@Composable
fun DuoGoalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DuoDarkColors else DuoLightColors
    CompositionLocalProvider(LocalDuoColors provides colors, content = content)
}
