package io.upnextgpt.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.upnextgpt.base.ImmutableHolder
import io.upnextgpt.data.model.Track
import io.upnextgpt.ui.home.viewmodel.HomeViewModel

@Composable
fun QueueScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    val queue by viewModel.playerQueue.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TitleBar(
            title = "Queue",
            showBack = true,
            onBack = onBack,
        )

        QueueList(
            items = ImmutableHolder(queue),
            onItemClick = viewModel::playTrack,
            playingTrackId = uiState.currTrack?.id,
        )
    }
}

@Composable
private fun QueueList(
    items: ImmutableHolder<List<Track>>,
    onItemClick: (track: Track) -> Unit,
    playingTrackId: Long?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = items.value) {
            TrackItem(
                item = it,
                onClick = { onItemClick(it) },
                isPlaying = it.id == playingTrackId,
            )
        }
    }
}

@Composable
private fun TrackItem(
    item: Track,
    onClick: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.height(IntrinsicSize.Max)) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(text = item.title)

            Text(text = item.artist, fontSize = 14.sp)
        }

        if (isPlaying) {
            Spacer(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}