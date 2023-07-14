package io.upnextgpt.ui.shared.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import kotlin.math.cos
import kotlin.math.sin

fun Modifier.gradientBorder(
    degrees: () -> Float,
    width: Dp,
    shape: Shape,
    colorStops: Array<Pair<Float, Color>> = arrayOf(
        0f to Color.Transparent,
        1f to Color.White
    ),
): Modifier {
    return graphicsLayer { alpha = 0.99f }.drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)

        val points = calcLinearGradientPoints(
            cx = size.width / 2,
            cy = size.height / 2,
            degrees = degrees(),
            width = size.width,
            height = size.height,
        )
        val borderBrush = Brush.linearGradient(
            colorStops = colorStops,
            start = Offset(points.startX, points.startY),
            end = Offset(points.endX, points.endY),
        )
        val borderStroke = Stroke(
            width = width.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

        onDrawWithContent {
            drawContent()
            drawOutline(
                outline = outline,
                brush = borderBrush,
                style = borderStroke,
            )
        }
    }
}

private data class GradientPoints(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
)

private fun calcLinearGradientPoints(
    cx: Float,
    cy: Float,
    degrees: Float,
    width: Float,
    height: Float
): GradientPoints {
    val radians = Math.toRadians(degrees.toDouble())
    val halfWidth = width / 2f
    val halfHeight = height / 2f
    val cos = cos(radians).toFloat()
    val sin = sin(radians).toFloat()

    val startX = cx + (-halfWidth * cos + halfHeight * sin)
    val startY = cy + (-halfWidth * sin - halfHeight * cos)
    val endX = cx + (halfWidth * cos + halfHeight * sin)
    val endY = cy + (halfWidth * sin - halfHeight * cos)

    return GradientPoints(startX, startY, endX, endY)
}