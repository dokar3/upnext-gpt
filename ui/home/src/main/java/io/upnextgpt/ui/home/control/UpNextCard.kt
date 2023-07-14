package io.upnextgpt.ui.home.control

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.upnextgpt.data.model.Track
import io.upnextgpt.ui.shared.modifier.gradientBorder
import io.upnextgpt.ui.shared.widget.CardButton
import io.upnextgpt.ui.shared.R as SharedR

@Composable
fun UpNextCard(
    isRolling: Boolean,
    nextTrack: Track?,
    playEnabled: Boolean,
    rollEnabled: Boolean,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    onRollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CardButton(
        modifier = modifier
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.secondary,
        padding = PaddingValues(0.dp),
        onClick = onClick,
    ) {
        Box {
            Column(
                modifier = Modifier
                    .loadingEffects(
                        animating = isRolling,
                        shape = MaterialTheme.shapes.medium,
                    )
                    .padding(16.dp),
            ) {
                Text("Up Next")

                val title = nextTrack?.title ?: ""
                val artist = nextTrack?.artist ?: ""
                Text(
                    text = "$artist - $title",
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = MaterialTheme.colorScheme
                                .onPrimary.copy(alpha = 0.5f),
                        ),
                        enabled = playEnabled,
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

            SparklesImage(modifier = Modifier.align(Alignment.TopEnd))
        }
    }
}

@Composable
private fun SparklesImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(SharedR.drawable.fill_sparkles),
        contentDescription = null,
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                alpha = 0.99f
                scaleX = 1.5f
                scaleY = 1.5f
            }
            .drawWithCache {
                val brush = Brush.linearGradient(
                    0f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.8f),
                    start = Offset.Zero,
                    end = Offset(size.width, 0f),
                )
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = brush,
                        blendMode = BlendMode.DstIn,
                    )
                }
            },
        colorFilter = ColorFilter.tint(Color.White),
    )
}

internal fun Modifier.loadingEffects(
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

    if (!animating) {
        return@composed this
    }

    gradientBorder(
        degrees = { animation.value * 360 },
        width = 4.dp,
        shape = shape,
        colorStops = arrayOf(
            0f to Color.Transparent,
            0.33f to Color.Transparent,
            1f to Color.White
        )
    )
}
