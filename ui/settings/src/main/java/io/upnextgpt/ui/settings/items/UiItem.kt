package io.upnextgpt.ui.settings.items

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun UiItem(
    isDynamicColorEnabled: Boolean,
    onUpdateServiceEnabledState: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(
                enabled = dynamicColorAvailable,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = {
                    onUpdateServiceEnabledState(!isDynamicColorEnabled)
                },
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Dynamic Color")
            Text(text = "Android 12+", fontSize = 14.sp)
        }

        Switch(
            checked = isDynamicColorEnabled,
            onCheckedChange = onUpdateServiceEnabledState,
            enabled = dynamicColorAvailable,
        )
    }
}