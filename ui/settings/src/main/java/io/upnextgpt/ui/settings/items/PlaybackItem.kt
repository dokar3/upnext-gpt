package io.upnextgpt.ui.settings.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.upnextgpt.data.settings.TrackFinishedAction
import io.upnextgpt.ui.shared.modifier.borderStart

private fun trackFinishedActionDescription(
    action: TrackFinishedAction
): String {
    return when (action) {
        TrackFinishedAction.None -> "None"
        TrackFinishedAction.Pause -> "Pause"
        TrackFinishedAction.PauseAndOpenApp -> "Pause and open APP"
        TrackFinishedAction.OpenPlayerToPlayNext -> "Open player to play next"
    }
}

@Composable
internal fun PlaybackItem(
    trackFinishedAction: TrackFinishedAction?,
    onUpdateTrackFinishedAction: (TrackFinishedAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val action = trackFinishedAction ?: TrackFinishedAction.None

    var isShowActionSelector by remember { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(text = "Track finished action")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(
                    MaterialTheme.colorScheme
                        .onBackground.copy(alpha = 0.1f)
                )
                .clickable { isShowActionSelector = true }
                .padding(8.dp),
        ) {
            Text(
                text = trackFinishedActionDescription(action),
                modifier = Modifier.weight(weight = 1f),
            )

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
            )

            if (isShowActionSelector) {
                TrackFinishedActionSelector(
                    onDismissRequest = { isShowActionSelector = false },
                    action = action,
                    onUpdateTrackFinishedAction = onUpdateTrackFinishedAction,
                )
            }
        }
    }
}

@Composable
private fun TrackFinishedActionSelector(
    onDismissRequest: () -> Unit,
    action: TrackFinishedAction,
    onUpdateTrackFinishedAction: (TrackFinishedAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = remember {
        TrackFinishedAction.values().map {
            it to trackFinishedActionDescription(it)
        }
    }

    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        items.forEach { item ->
            val isCurrent = item.first == action
            DropdownMenuItem(
                text = {
                    Text(
                        text = item.second,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .borderStart(
                                width = 4.dp,
                                color = if (isCurrent) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    Color.Transparent
                                },
                            )
                            .padding(horizontal = 8.dp),
                    )
                },
                onClick = {
                    if (!isCurrent) {
                        onUpdateTrackFinishedAction(item.first)
                    }
                    onDismissRequest()
                },
                enabled = !isCurrent,
            )
        }
    }
}