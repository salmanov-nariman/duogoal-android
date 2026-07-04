package com.example.duogoal_android.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Контурные иконки из дизайна (Lucide). Path-данные перенесены из SVG как есть,
 * цвет задаётся через tint при отрисовке.
 */
private fun strokeIcon(
    name: String,
    vararg paths: String,
    strokeWidth: Float = 1.75f,
): ImageVector {
    val builder = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    for (d in paths) {
        builder.addPath(
            pathData = addPathNodes(d),
            stroke = SolidColor(Color.White),
            strokeLineWidth = strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }
    return builder.build()
}

private fun fillIcon(name: String, vararg paths: String): ImageVector {
    val builder = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    for (d in paths) {
        builder.addPath(pathData = addPathNodes(d), fill = SolidColor(Color.White))
    }
    return builder.build()
}

private fun circlePath(cx: Float, cy: Float, r: Float): String =
    "M ${cx - r},$cy a $r,$r 0 1,0 ${2 * r},0 a $r,$r 0 1,0 ${-2 * r},0"

object DuoIcons {
    val ArrowLeft = strokeIcon("arrow-left", "m12 19-7-7 7-7", "M19 12H5")

    val Bell = strokeIcon(
        "bell",
        "M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9",
        "M10.3 21a1.94 1.94 0 0 0 3.4 0",
    )

    val Home = strokeIcon(
        "home",
        "M3 10.5 12 3l9 7.5",
        "M5 9.5V20a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V9.5",
    )

    val Chat = strokeIcon("chat", "M7.9 20A9 9 0 1 0 4 16.1L2 22Z")

    val User = strokeIcon("user", circlePath(12f, 8f, 5f), "M20 21a8 8 0 0 0-16 0")

    val Settings = strokeIcon(
        "settings",
        circlePath(12f, 12f, 3f),
        "M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1Z",
    )

    val MoreHorizontal = fillIcon(
        "more-horizontal",
        circlePath(5f, 12f, 1.6f),
        circlePath(12f, 12f, 1.6f),
        circlePath(19f, 12f, 1.6f),
    )

    val Plus = strokeIcon("plus", "M5 12h14", "M12 5v14", strokeWidth = 2.2f)

    val Check = strokeIcon("check", "M20 6 9 17l-5-5", strokeWidth = 2.5f)

    val Clock = strokeIcon("clock", circlePath(12f, 12f, 10f), "M12 6v6l4 2")

    val Camera = strokeIcon(
        "camera",
        "M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z",
        circlePath(12f, 13f, 3f),
    )

    val Share = strokeIcon(
        "share",
        circlePath(18f, 5f, 3f),
        circlePath(6f, 12f, 3f),
        circlePath(18f, 19f, 3f),
        "m8.6 13.5 6.8 4",
        "m15.4 6.5-6.8 4",
    )

    val Search = strokeIcon("search", circlePath(11f, 11f, 8f), "m21 21-4.3-4.3")

    val Send = strokeIcon("send", "m22 2-7 20-4-9-9-4Z", "M22 2 11 13")

    val Heart = strokeIcon(
        "heart",
        "M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.51 4.04 3 5.5l7 7Z",
    )

    val Comment = strokeIcon(
        "comment",
        "M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z",
    )

    val Bookmark = strokeIcon("bookmark", "M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z")

    val Mail = strokeIcon(
        "mail",
        "M4 4h16a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2Z",
        "m22 7-10 5L2 7",
    )

    val Lock = strokeIcon(
        "lock",
        "M5 11h14a2 2 0 0 1 2 2v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2Z",
        "M7 11V7a5 5 0 0 1 10 0v4",
    )

    val Eye = strokeIcon(
        "eye",
        "M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z",
        circlePath(12f, 12f, 3f),
    )

    val Close = strokeIcon("close", "M18 6 6 18", "m6 6 12 12", strokeWidth = 2f)

    val Filter = strokeIcon(
        "filter",
        "M4 6h16",
        "M4 12h16",
        "M4 18h16",
        circlePath(9f, 6f, 2f),
        circlePath(15f, 12f, 2f),
        circlePath(8f, 18f, 2f),
    )

    /** Логотип DuoGoal: две дуги над общей линией (viewport 64). */
    val Logo: ImageVector = run {
        val builder = ImageVector.Builder(
            name = "logo",
            defaultWidth = 64.dp,
            defaultHeight = 64.dp,
            viewportWidth = 64f,
            viewportHeight = 64f,
        )
        listOf(
            Triple("M8 44 A15 15 0 0 1 38 44", 1f, 5f),
            Triple("M26 44 A15 15 0 0 1 56 44", 0.45f, 5f),
            Triple("M6 44 L58 44", 1f, 5f),
        ).forEach { (d, alpha, w) ->
            builder.addPath(
                pathData = addPathNodes(d),
                stroke = SolidColor(Color.White),
                strokeAlpha = alpha,
                strokeLineWidth = w,
                strokeLineCap = StrokeCap.Round,
            )
        }
        builder.build()
    }
}
