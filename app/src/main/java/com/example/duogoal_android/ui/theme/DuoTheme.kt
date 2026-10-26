package com.example.duogoal_android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Цветовые токены дизайн-системы DuoGoal (из Claude Design, акцент #6355E0).
 * Светлая и тёмная палитры повторяют colors_and_type.css + экраны immersive.
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
)

val Accent = Color(0xFF6355E0)

val DuoDarkColors = DuoColors(
    isDark = true,
    bg = Color(0xFF07080A),
    surface = Color(0xFF0E1015),
    surfaceInput = Color(0x0DFFFFFF),
    border = Color(0x17FFFFFF),
    borderStrong = Color(0x1FFFFFFF),
    divider = Color(0x0FFFFFFF),
    fg = Color(0xFFF4F2EC),
    fgMuted = Color(0xFF8A8D93),
    fgSubtle = Color(0xFF6C7079),
    accent = Accent,
    onAccent = Color.White,
    accentSoft = Color(0x296355E0),
    success = Color(0xFF7FD9AC),
    successSoft = Color(0x293FBE86),
    warning = Color(0xFFE8C089),
    warningSoft = Color(0x2EE0A85A),
    danger = Color(0xFFE5837A),
    navBg = Color(0xB814161C),
    navBorder = Color(0xFF23262E),
    avatar1 = Color(0xFF8C7558),
    avatar2 = Color(0xFF677866),
    avatar3 = Color(0xFF6E7A8A),
    avatar4 = Color(0xFF8C6E6E),
    avatar5 = Color(0xFF7E7388),
    avatar6 = Color(0xFF997D3F),
)

val DuoLightColors = DuoColors(
    isDark = false,
    bg = Color(0xFFF4F3EF),
    surface = Color(0xFFFFFFFF),
    surfaceInput = Color(0xFFFFFFFF),
    border = Color(0x1A000000),
    borderStrong = Color(0x1F000000),
    divider = Color(0x0F000000),
    fg = Color(0xFF1A1B1E),
    fgMuted = Color(0xFF8A8D93),
    fgSubtle = Color(0xFF9A9DA3),
    accent = Accent,
    onAccent = Color.White,
    accentSoft = Color(0x296355E0),
    success = Color(0xFF1F7A50),
    successSoft = Color(0x293FBE86),
    warning = Color(0xFF9A6B25),
    warningSoft = Color(0x2EE0A85A),
    danger = Color(0xFFB8514A),
    navBg = Color(0xEBFFFFFF),
    navBorder = Color(0xFFEAE8E2),
    avatar1 = Color(0xFFB89B7E),
    avatar2 = Color(0xFF8FA38C),
    avatar3 = Color(0xFF94A3B8),
    avatar4 = Color(0xFFB89494),
    avatar5 = Color(0xFFA89BB8),
    avatar6 = Color(0xFFC9A85B),
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
