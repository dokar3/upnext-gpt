package io.upnextgpt.ui.shared.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class TypedSnackbarVisuals(
    val type: SnackbarType,
    override val message: String,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short
        else SnackbarDuration.Indefinite,
    override val withDismissAction: Boolean = false,
) : SnackbarVisuals

enum class SnackbarType {
    Message,
    Success,
    Warn,
    Error,
}

fun SnackbarVisuals.typedBorderColorOrNull(): Color? {
    val typedSnackbarVisuals = this as? TypedSnackbarVisuals
        ?: return null
    return when (typedSnackbarVisuals.type) {
        SnackbarType.Message -> Color.White
        SnackbarType.Success -> Color(0xFF0FF58A)
        SnackbarType.Warn -> Color(0xFFFFD500)
        SnackbarType.Error -> Color(0xFFFF5858)
    }
}

fun Modifier.snackbarShimmerBorder(
    color: Color,
    shape: Shape,
): Modifier {
    return drawWithCache {
        val borderBrush = Brush.linearGradient(
            0f to color,
            0.8f to Color.Transparent,
            start = Offset(size.width * 0.5f, 0f),
            end = Offset(size.width * 0.55f, size.height),
        )
        val outline = shape.createOutline(
            size, layoutDirection, density = this,
        )
        val borderStroke = Stroke(width = 0.7.dp.toPx())
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

@Composable
fun ShimmerBorderSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val contentColor = MaterialTheme.colorScheme.onBackground
    val borderShape = SnackbarDefaults.shape
    SwipeableSnackbar(
        snackbarData = snackbarData,
        modifier = modifier
            .padding(vertical = 16.dp)
            .snackbarShimmerBorder(
                color = snackbarData.visuals.typedBorderColorOrNull()
                    ?: contentColor,
                shape = borderShape,
            ),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = contentColor,
        actionColor = contentColor,
        actionContentColor = contentColor,
        dismissActionContentColor = contentColor,
    )
}

@Composable
fun SwipeableSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = SnackbarDefaults.color,
    contentColor: Color = SnackbarDefaults.contentColor,
    actionColor: Color = SnackbarDefaults.actionColor,
    actionContentColor: Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
) {
    val density = LocalDensity.current

    val scope = rememberCoroutineScope()

    val dismissThreshold = with(density) { 48.dp.toPx() }

    val offsetY = remember { Animatable(0f) }

    val draggableState = rememberDraggableState(
        onDelta = {
            scope.launch {
                val y = (offsetY.value + it).coerceAtLeast(0f)
                offsetY.snapTo(y)
            }
        }
    )

    fun canDismiss(velocity: Float): Boolean {
        val offY = offsetY.value
        return velocity >= 3000f ||
                (velocity >= 1000 && offY >= dismissThreshold / 2) ||
                (offY >= dismissThreshold)
    }

    SnackbarWithoutPaddings(
        snackbarData = snackbarData,
        modifier = Modifier
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    if (canDismiss(velocity)) {
                        snackbarData.dismiss()
                    } else {
                        offsetY.animateTo(0f)
                    }
                },
            )
            .then(modifier),
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        actionColor = actionColor,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor,
    )
}

@Composable
fun SnackbarWithoutPaddings(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = SnackbarDefaults.color,
    contentColor: Color = SnackbarDefaults.contentColor,
    actionColor: Color = SnackbarDefaults.actionColor,
    actionContentColor: Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
) {
    val actionLabel = snackbarData.visuals.actionLabel
    val actionComposable: (@Composable () -> Unit)? = if (actionLabel != null) {
        @Composable {
            TextButton(
                colors = ButtonDefaults.textButtonColors(contentColor = actionColor),
                onClick = { snackbarData.performAction() },
                content = { Text(actionLabel) }
            )
        }
    } else {
        null
    }
    val dismissActionComposable: (@Composable () -> Unit)? =
        if (snackbarData.visuals.withDismissAction) {
            @Composable {
                IconButton(
                    onClick = { snackbarData.dismiss() },
                    content = {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Dismiss",
                        )
                    }
                )
            }
        } else {
            null
        }
    androidx.compose.material3.Snackbar(
        modifier = modifier,
        action = actionComposable,
        dismissAction = dismissActionComposable,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor,
        content = { Text(snackbarData.visuals.message) }
    )
}