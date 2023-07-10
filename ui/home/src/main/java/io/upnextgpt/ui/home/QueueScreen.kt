package io.upnextgpt.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import io.upnextgpt.base.ImmutableHolder
import io.upnextgpt.base.image.DiskImageStore
import io.upnextgpt.data.model.Track
import io.upnextgpt.ui.home.viewmodel.HomeViewModel
import io.upnextgpt.ui.shared.dialog.BasicDialog
import io.upnextgpt.ui.shared.widget.ShimmerBorderSnackbar
import io.upnextgpt.ui.shared.widget.TitleBar
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import org.koin.compose.koinInject
import io.upnextgpt.ui.shared.R as SharedR

@Composable
fun QueueScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    val queue by viewModel.playerQueue.collectAsState()

    val removedTrack by viewModel.removedTrack.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var isShowClearQueueDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel, removedTrack) {
        val removed = removedTrack
        if (removed != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Removed '${removed.data.title}'",
                actionLabel = "Undo",
                duration = SnackbarDuration.Long,
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.insertTrackToQueue(
                    track = removed.data,
                    index = removed.index,
                )
            }
            viewModel.clearRemovedTrack()
        }
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.clearRemovedTrack() }
    }

    Box {
        Column(modifier = modifier.fillMaxSize()) {
            TitleBar(
                title = "Queue",
                showBack = true,
                onBack = onBack,
                endButton = {
                    IconButton(onClick = { isShowClearQueueDialog = true }) {
                        Icon(
                            painter = painterResource(
                                SharedR.drawable.baseline_clear_all_24
                            ),
                            contentDescription = "Clear all",
                        )
                    }
                }
            )

            Divider()

            QueueList(
                nextTrack = uiState.nextTrack,
                items = ImmutableHolder(queue),
                onItemClick = viewModel::playTrack,
                onDelete = viewModel::removeTrackFromQueue,
                currTrackId = uiState.currTrack?.id,
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            ShimmerBorderSnackbar(
                snackbarData = it,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }

    if (isShowClearQueueDialog) {
        BasicDialog(
            onDismissRequest = { isShowClearQueueDialog = false },
            title = { Text("Clear queue") },
            actions = {
                TextButton(onClick = {
                    viewModel.clearQueue()
                    isShowClearQueueDialog = false
                }) {
                    Text("Clear")
                }
            },
        ) {
            Text("Are you sure to clear the queue?")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QueueList(
    nextTrack: Track?,
    items: ImmutableHolder<List<Track>>,
    onItemClick: (track: Track) -> Unit,
    onDelete: (track: Track) -> Unit,
    currTrackId: Long?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        if (nextTrack != null) {
            item {
                SectionHeader(title = "Up Next")

                TrackItem(
                    item = nextTrack,
                    onClick = { onItemClick(nextTrack) },
                    onDelete = {},
                    isCurrent = false,
                    swipeable = false,
                    icon = {
                        Icon(
                            painter = painterResource(
                                SharedR.drawable.fill_sparkles
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(4.dp),
                        )
                    },
                )
            }
        }

        item {
            SectionHeader(title = "History")
        }

        items(
            items = items.value,
            key = { it.id },
        ) {
            TrackItem(
                item = it,
                onClick = { onItemClick(it) },
                onDelete = { onDelete(it) },
                isCurrent = it.id == currTrackId,
                modifier = Modifier.animateItemPlacement(),
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp,
        ),
    )
}

@Composable
private fun TrackItem(
    item: Track,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    isCurrent: Boolean,
    modifier: Modifier = Modifier,
    swipeable: Boolean = !isCurrent,
    icon: @Composable ((Track) -> Unit)? = { TrackCover(track = it) },
) {
    val deleteAction = SwipeAction(
        onSwipe = onDelete,
        icon = {
            Row(modifier = Modifier.padding(horizontal = 32.dp)) {
                Icon(
                    painter = painterResource(SharedR.drawable.outline_delete),
                    contentDescription = "Delete",
                )
            }
        },
        background = MaterialTheme.colorScheme.errorContainer,
    )

    SwipeableActionsBox(
        endActions = if (swipeable) listOf(deleteAction) else emptyList(),
        swipeThreshold = 96.dp,
    ) {
        Box(modifier = Modifier.height(IntrinsicSize.Max)) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    icon(item)

                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column {
                    Text(text = item.title)

                    Text(text = item.artist, fontSize = 14.sp)
                }
            }

            if (isCurrent) {
                Spacer(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}

@Composable
private fun TrackCover(
    track: Track,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = koinInject(),
) {
    AsyncImage(
        model = DiskImageStore.url(track.id.toString()),
        contentDescription = null,
        modifier = modifier
            .size(48.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        imageLoader = imageLoader,
    )
}