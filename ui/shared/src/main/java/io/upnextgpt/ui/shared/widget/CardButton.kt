package io.upnextgpt.ui.shared.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = MaterialTheme.shapes.medium,
    padding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    clickRequiresUnConsumed: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    var isTouching by remember { mutableStateOf(false) }

    val scale = animateFloatAsState(
        targetValue = if (isTouching) 0.95f else 1f,
        label = "CardButtonScale",
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .pointerInput(enabled) {
                if (!enabled) {
                    return@pointerInput
                }
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isTouching = true
                    if (awaitLongPressOrCancellation(down.id) != null) {
                        val click = !clickRequiresUnConsumed ||
                                currentEvent.changes.all { !it.isConsumed }
                        if (click) {
                            onLongClick?.invoke()
                            hapticFeedback.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )
                        }
                    } else {
                        val click = !clickRequiresUnConsumed ||
                                currentEvent.changes.all { !it.isConsumed }
                        if (click) {
                            val touchSlop = viewConfiguration.touchSlop
                            val downPos = down.position
                            val currPos = currentEvent.changes.first().position
                            if (abs(downPos.x - currPos.x) <= touchSlop &&
                                abs(downPos.y - currPos.y) <= touchSlop
                            ) {
                                onClick?.invoke()
                            }
                        }
                    }
                    isTouching = false
                }
            }
            .clip(shape)
            .background(backgroundColor)
            .padding(padding)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
        ) {
            content()
        }
    }
}