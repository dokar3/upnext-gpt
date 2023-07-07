package io.upnextgpt.ui.home.control

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.upnextgpt.base.ImmutableHolder
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CoverView(
    key: Any,
    bitmap: ImmutableHolder<Bitmap?>,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    val imageBitmap = remember(bitmap.value) { bitmap.value?.asImageBitmap() }

    var currBitmap by remember { mutableStateOf(imageBitmap) }

    var showNextBitmap by remember { mutableStateOf(false) }

    LaunchedEffect(key) {
        showNextBitmap = true
    }

    LaunchedEffect(showNextBitmap, imageBitmap) {
        if (!showNextBitmap) {
            currBitmap = imageBitmap
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1.0f)
            .fillMaxWidth()
            .clip(shape)
            .shadow(elevation = 6.dp),
    ) {
        val curr = currBitmap
        AnimatedVisibility(
            visible = !showNextBitmap,
            enter = EnterTransition.None,
            exit = scaleOut(targetScale = 0.5f) + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                if (curr != null) {
                    Image(
                        bitmap = curr,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showNextBitmap,
            enter = slideInHorizontally { it } +
                    scaleIn(initialScale = 1.1f) +
                    fadeIn(),
            exit = ExitTransition.None,
        ) {
            LaunchedEffect(transition.currentState) {
                if (transition.currentState == EnterExitState.Visible) {
                    currBitmap = imageBitmap
                    showNextBitmap = false
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}