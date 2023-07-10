package io.upnextgpt.ui.home

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetDragHandle
import com.dokar.sheets.BottomSheetState
import io.upnextgpt.base.ImmutableHolder
import io.upnextgpt.data.model.Track
import kotlinx.coroutines.launch
import io.upnextgpt.ui.shared.R as SharedR

@Composable
fun TrackMenuSheet(
    track: Track,
    albumArt: ImmutableHolder<Bitmap?>,
    state: BottomSheetState,
    onLike: () -> Unit,
    onCancelLike: () -> Unit,
    onDislike: () -> Unit,
    onCancelDislike: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomSheet(
        state = state,
        modifier = modifier,
        skipPeeked = true,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        dragHandle = {
            BottomSheetDragHandle(color = MaterialTheme.colorScheme.onSurface)
        },
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.1f
                        )
                    ),
            ) {
                val bitmap = remember(albumArt.value) {
                    albumArt.value?.asImageBitmap()
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = track.title,
                    fontSize = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(text = track.artist)
            }

        }


        RateView(
            isLiked = track.liked == true,
            isDisliked = track.disliked == true,
            onLike = onLike,
            onCancelLike = onCancelLike,
            onDislike = onDislike,
            onCancelDislike = onCancelDislike,
        )
    }
}

@Composable
private fun RateView(
    isLiked: Boolean,
    isDisliked: Boolean,
    onLike: () -> Unit,
    onCancelLike: () -> Unit,
    onDislike: () -> Unit,
    onCancelDislike: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (isLiked) {
                        onCancelLike()
                    } else {
                        onLike()
                    }
                }
                .padding(
                    horizontal = 8.dp,
                    vertical = 16.dp,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(
                        if (isLiked) {
                            SharedR.drawable.baseline_thumb_up_alt_24
                        } else {
                            SharedR.drawable.baseline_thumb_up_off_alt_24
                        }
                    ),
                    contentDescription = if (isLiked) {
                        "Cancel like"
                    } else {
                        "Like"
                    },
                    modifier = Modifier.likedAnimation(run = isLiked),
                    tint = if (isLiked) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Like", fontSize = 14.sp)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (isDisliked) {
                        onCancelDislike()
                    } else {
                        onDislike()
                    }
                }
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(
                        if (isDisliked) {
                            SharedR.drawable.baseline_thumb_down_alt_24
                        } else {
                            SharedR.drawable.baseline_thumb_down_off_alt_24
                        }
                    ),
                    contentDescription = if (isDisliked) {
                        "Cancel dislike"
                    } else {
                        "Dislike"
                    },
                    modifier = Modifier.dislikedAnimation(run = isDisliked),
                    tint = if (isDisliked) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Dislike", fontSize = 14.sp)
            }
        }
    }
}

private val scaleKeyframes = keyframes {
    durationMillis = 700
    1f at 0
    1.1f at 150
    1.2f at 250
    1.36f at 350
    1.3f at 400
    1.32f at 500
    1.25f at 550
    1f at 700
}

private fun Modifier.likedAnimation(run: Boolean): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(run) {
        if (!run) {
            scale.snapTo(1f)
            rotation.snapTo(0f)
            return@LaunchedEffect
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = scaleKeyframes,
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 750
                    0f at 0
                    -15f at 200
                    -25f at 300
                    -25f at 500
                    10f at 600
                    0f at 750
                }
            )
        }
    }

    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        rotationZ = rotation.value
    }
}


private fun Modifier.dislikedAnimation(run: Boolean): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(run) {
        if (!run) {
            scale.snapTo(1f)
            rotation.snapTo(0f)
            return@LaunchedEffect
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = scaleKeyframes,
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 750
                    0f at 0
                    5f at 200
                    25f at 300
                    25f at 500
                    -10f at 600
                    0f at 750
                }
            )
        }
    }

    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        rotationZ = rotation.value
    }
}