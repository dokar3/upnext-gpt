package io.upnextgpt.ui.home.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

private fun millisToDisplayTime(millis: Long): String {
    if (millis <= 0) {
        return "-"
    }
    val minutes = millis / 60000
    val seconds = (millis % 60000) / 1000
    fun padWithZero(number: Long): String {
        return String.format("%02d", number)
    }
    return "${padWithZero(minutes)}:${padWithZero(seconds)}"
}

@Composable
internal fun PlayerProgressBar(
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    onSeek: (position: Long) -> Unit,
    modifier: Modifier = Modifier,
    tickMillis: Long = 500L,
) {
    var currProgress by remember { mutableFloatStateOf(0f) }

    var currTime by remember(position) {
        mutableStateOf(millisToDisplayTime(position))
    }

    val maxTime by remember(duration) {
        derivedStateOf {
            millisToDisplayTime(duration)
        }
    }

    LaunchedEffect(isPlaying, position, duration) {
        if (position <= 0 || duration <= 0) {
            currProgress = 0f
            return@LaunchedEffect
        }
        var pos = position
        while (pos <= duration) {
            currProgress = pos.toFloat() / duration
            currTime = millisToDisplayTime(pos)
            if (!isPlaying) {
                break
            }
            val elapsed = measureTimeMillis {
                delay(tickMillis)
            }
            pos += elapsed
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(currTime, fontSize = 14.sp)

            Text(maxTime, fontSize = 14.sp)
        }

        Slider(
            value = currProgress,
            onValueChange = { onSeek((it * duration).toLong()) },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
            )
        )
    }
}