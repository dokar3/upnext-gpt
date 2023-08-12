package io.upnextgpt.ui.settings.items

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.upnextgpt.ui.shared.dialog.ConnectToPlayersDialog
import io.upnextgpt.ui.shared.theme.pass
import io.upnextgpt.ui.shared.theme.warn

@Composable
internal fun ServiceItem(
    onConnectClick: () -> Unit,
    onUpdateServiceEnabledState: (Boolean) -> Unit,
    isConnectedToPlayers: Boolean,
    isServiceEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var isShowConnectToPlayersDialog by remember(isConnectedToPlayers) {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .let {
                if (!isConnectedToPlayers) {
                    it.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.warn,
                        shape = MaterialTheme.shapes.medium,
                    )
                } else if (isServiceEnabled) {
                    it.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.pass,
                        shape = MaterialTheme.shapes.medium,
                    )
                } else {
                    it
                }
            }
            .then(modifier)
            .fillMaxWidth(),
    ) {
        Text(
            text = if (isConnectedToPlayers) {
                "Connected to your players."
            } else {
                "Players are not connected."
            },
            modifier = Modifier.padding(8.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = {
                    if (isConnectedToPlayers) {
                        onConnectClick()
                    } else {
                        isShowConnectToPlayersDialog = true
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isConnectedToPlayers) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                )
            ) {
                Text(
                    text = if (isConnectedToPlayers) {
                        "System Settings"
                    } else {
                        "Connect"
                    }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isServiceEnabled) {
                    "Service enabled."
                } else {
                    "Service disabled."
                },
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isServiceEnabled,
                onCheckedChange = onUpdateServiceEnabledState,
            )
        }
    }

    if (isShowConnectToPlayersDialog) {
        ConnectToPlayersDialog(
            onDismissRequest = { isShowConnectToPlayersDialog = false },
            onConnectClick = onConnectClick
        )
    }
}