package io.upnextgpt.ui.home.control

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.upnextgpt.ui.shared.widget.CardButton
import io.upnextgpt.ui.shared.R as SharedR

@Composable
internal fun PlayerCard(
    playerName: String,
    @DrawableRes
    iconRes: Int,
    themeColor: Color,
    onClick: () -> Unit,
    onLaunchPlayerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardButton(
        modifier = modifier,
        backgroundColor = themeColor,
        contentColor = Color.White,
        onClick = onClick,
        padding = PaddingValues(0.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (iconRes != 0) {
                PlayerIcon(iconRes = iconRes)
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .height(48.dp),
            ) {
                Text(playerName)

                Icon(
                    painter = painterResource(
                        SharedR.drawable.baseline_arrow_outward_24
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = rememberRipple(radius = 12.dp),
                            onClick = onLaunchPlayerClick,
                        )
                )
            }
        }
    }
}

@Composable
private fun BoxScope.PlayerIcon(
    iconRes: Int,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        colorFilter = ColorFilter.tint(Color.White),
        modifier = modifier
            .align(Alignment.CenterEnd)
            .size(48.dp)
            .graphicsLayer {
                scaleX = 1.5f
                scaleY = 1.5f
                alpha = 0.99f
                rotationZ = -25f
            }
            .drawWithCache {
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.8f),
                    ),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height),
                )
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = gradientBrush,
                        blendMode = BlendMode.DstIn
                    )
                }
            },
    )
}