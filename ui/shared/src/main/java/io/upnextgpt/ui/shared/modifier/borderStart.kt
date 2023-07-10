package io.upnextgpt.ui.shared.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.unit.Dp

fun Modifier.borderStart(
    width: Dp,
    color: Color,
    shape: Shape = RectangleShape,
): Modifier {
    return drawWithCache {
        val outline = shape.createOutline(
            size.copy(width = width.toPx()),
            layoutDirection,
            density = this,
        )
        onDrawWithContent {
            drawContent()
            drawOutline(
                outline = outline,
                color = color,
            )
        }
    }
}