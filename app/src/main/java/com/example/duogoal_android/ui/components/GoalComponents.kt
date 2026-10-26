package com.example.duogoal_android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.theme.DuoTheme

@Composable
fun DuoCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = DuoTheme.colors
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier.fillMaxWidth().shadow(4.dp, shape, ambientColor = c.fg.copy(alpha = 0.03f), spotColor = c.fg.copy(alpha = 0.03f))
            .background(c.surface, shape).border(1.dp, c.border, shape).padding(padding),
        content = content,
    )
}

@Composable
fun PairedAvatars(size: Dp = 34.dp) {
    val c = DuoTheme.colors
    Box(Modifier.size(size * 1.7f, size).semantics { contentDescription = "Ты и Миша" }) {
        Avatar("АС", c.avatar1, size, 11, ringColor = c.bg, ringWidth = 2.dp)
        Avatar("МК", c.avatar3, size, 11, ringColor = c.bg, ringWidth = 2.dp, modifier = Modifier.offset(x = size * 0.7f))
    }
}

@Composable
fun GoalProgressRing(completed: Int, total: Int, modifier: Modifier = Modifier) {
    val c = DuoTheme.colors
    val progress = if (total > 0) (completed.toFloat() / total).coerceIn(0f, 1f) else 0f
    Box(
        modifier.size(112.dp).semantics {
            contentDescription = "Пройдено $completed из $total дней"
            progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f)
        },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            val diameter = size.minDimension - stroke
            val corner = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
            drawArc(c.accentSoft, -90f, 360f, false, corner, Size(diameter, diameter), style = Stroke(stroke))
            drawArc(c.accent, -90f, 360f * progress, false, corner, Size(diameter, diameter), style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DuoText(completed.toString(), 34, c.fg, FontWeight.SemiBold, letterSpacing = -0.04, lineHeight = 1.1)
            DuoText("из $total дней", 11, c.fgMuted)
        }
    }
}
