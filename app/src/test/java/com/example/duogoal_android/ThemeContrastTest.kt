package com.example.duogoal_android

import androidx.compose.ui.graphics.Color
import com.example.duogoal_android.ui.theme.DuoDarkColors
import com.example.duogoal_android.ui.theme.DuoLightColors
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/** Контраст обычного текста: WCAG AA, минимум 4.5:1 в обеих темах. */
class ThemeContrastTest {
    @Test fun textAndStatusColorsRemainReadableInBothThemes() {
        listOf(DuoLightColors, DuoDarkColors).forEach { c ->
            val combinations = listOf(
                Triple("Основной текст", c.fg, c.bg),
                Triple("Подпись", c.fgMuted, c.bg),
                Triple("Подпись карточки", c.fgMuted, c.surface),
                Triple("Placeholder", c.fgSubtle, c.surfaceInput),
                Triple("Акцент", c.accentFg, c.bg),
                Triple("Акцент на плашке", c.accentFg, c.accentSoft),
                Triple("Успешный статус", c.success, c.successSoft),
                Triple("Кнопка", c.onAccent, c.accent),
                Triple("Аватар", c.avatarInk, c.avatar1),
            )
            combinations.forEach { (label, foreground, background) ->
                val contrast = contrast(foreground, background)
                assertTrue("$label, dark=${c.isDark}: $contrast < 4.5", contrast >= 4.5)
            }
        }
    }

    private fun contrast(a: Color, b: Color): Double {
        fun luminance(color: Color): Double {
            fun linear(channel: Float): Double = channel.toDouble().let { if (it <= 0.04045) it / 12.92 else ((it + 0.055) / 1.055).pow(2.4) }
            return 0.2126 * linear(color.red) + 0.7152 * linear(color.green) + 0.0722 * linear(color.blue)
        }
        val first = luminance(a)
        val second = luminance(b)
        return (max(first, second) + 0.05) / (min(first, second) + 0.05)
    }
}
