package io.upnextgpt.ui.shared.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private class SpringDragScrollConnection(
    private val onNestedScroll: (dy: Float) -> Unit,
    private val onStopNestedScroll: () -> Unit,
) : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return if (source == NestedScrollSource.Drag) {
            onNestedScroll(available.y)
            available
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        onStopNestedScroll()
        return super.onPreFling(available)
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        onStopNestedScroll()
        return super.onPostFling(consumed, available)
    }
}

@Composable
fun SpringDragBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()

    val offsetY = remember { Animatable(0f) }

    val maxOffsetDistance = with(LocalDensity.current) { 160.dp.toPx() }

    val connection = remember(scope, offsetY) {
        SpringDragScrollConnection(
            onNestedScroll = {
                scope.launch {
                    val value = offsetY.value
                    val factor = 1f + (abs(value) / maxOffsetDistance) * 5
                    val newY = if (value >= 0) {
                        min(maxOffsetDistance, value + it / factor)
                    } else {
                        max(-maxOffsetDistance, value + it / factor)
                    }
                    offsetY.snapTo(newY)
                }
            },
            onStopNestedScroll = {
                scope.launch {
                    offsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                    )
                }
            },
        )
    }

    Box(
        modifier = modifier
            .nestedScroll(connection = connection)
            .graphicsLayer { translationY = offsetY.value },
        content = content,
    )
}