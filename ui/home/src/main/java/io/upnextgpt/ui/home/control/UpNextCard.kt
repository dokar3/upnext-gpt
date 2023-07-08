package io.upnextgpt.ui.home.control

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.upnextgpt.data.model.TrackInfo
import io.upnextgpt.ui.shared.widget.CardButton
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun UpNextCard(
    isRolling: Boolean,
    nextTrack: TrackInfo?,
    playEnabled: Boolean,
    rollEnabled: Boolean,
    onPlayClick: () -> Unit,
    onRollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CardButton(
        modifier = modifier
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.secondary,
        padding = PaddingValues(0.dp),
    ) {
        Column(
            modifier = Modifier
                .loadingEffects(
                    animating = isRolling,
                    shape = MaterialTheme.shapes.medium,
                )
                .padding(16.dp),
        ) {
            Text("Up Next")

            Text(
                text = "${nextTrack?.title ?: ""} - ${nextTrack?.artist ?: ""}",
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = onPlayClick,
                    shape = CircleShape,
                    enabled = playEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text("Play")
                }

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(
                    onClick = onRollClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContentColor = MaterialTheme.colorScheme
                            .onPrimary.copy(alpha = 0.5f),
                    ),
                    enabled = rollEnabled,
                ) {
                    Text("Roll!")
                }
            }
        }
    }
}

fun Modifier.loadingEffects(
    animating: Boolean,
    shape: Shape,
): Modifier = composed {
    val animation = remember { Animatable(0f) }

    LaunchedEffect(animation, animating) {
        if (!animating) {
            animation.snapTo(0f)
            return@LaunchedEffect
        }
        animation.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000,
                    easing = LinearEasing,
                ),
            )
        )
    }

    graphicsLayer { alpha = 0.99f }.drawWithCache {
        val animValue = animation.value

        val outline = shape.createOutline(size, layoutDirection, this)

        val points = calcLinearGradientPoints(
            cx = size.width / 2,
            cy = size.height / 2,
            degrees = animValue * 360,
            width = size.width,
            height = size.height,
        )
        val borderBrush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent, Color.White),
            start = Offset(points.startX, points.startY),
            end = Offset(points.endX, points.endY),
        )
        val borderStroke = Stroke(
            width = 4.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

        onDrawWithContent {
            drawContent()
            if (animating) {
                // Border
                drawOutline(
                    outline = outline,
                    brush = borderBrush,
                    style = borderStroke,
                )
            }
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