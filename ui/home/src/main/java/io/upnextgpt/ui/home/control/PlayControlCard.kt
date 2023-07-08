package io.upnextgpt.ui.home.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.upnextgpt.ui.shared.R
import io.upnextgpt.ui.shared.widget.CardButton

@Composable
fun PlayControlCard(
    isPlaying: Boolean,
    prevEnabled: Boolean,
    nextEnabled: Boolean,
    onPrevClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardButton(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onPrevClick, enabled = prevEnabled) {
                Icon(
                    painter = painterResource(R.drawable.outline_skip_previous_24),
                    contentDescription = "",
                )
            }
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    painter = painterResource(
                        if (isPlaying) {
                            R.drawable.outline_pause_circle_outline_24
                        } else {
                            R.drawable.outline_play_arrow_24
                        }
                    ),
                    contentDescription = "",
                    modifier = Modifier.size(28.dp),
                )
            }
            IconButton(onClick = onNextClick, enabled = nextEnabled) {
                Icon(
                    painter = painterResource(R.drawable.outline_skip_next_24),
                    contentDescription = "",
                )
            }
        }
    }
}