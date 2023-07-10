package io.upnextgpt.ui.shared.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.upnextgpt.base.util.IntentUtil
import io.upnextgpt.ui.shared.R as SharedR

@Composable
fun ConnectToPlayersDialog(
    onDismissRequest: () -> Unit,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.heightIn(max = 600.dp),
        title = {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(
                    SharedR.drawable.baseline_link_off_24
                ),
                contentDescription = "Disconnected",
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally),
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onBackground
                ),
            )

            Text(
                text = "Players not connected",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
            )
        },
        actions = {
            TextButton(onClick = onDismissRequest) {
                Text("Maybe later")
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(onClick = onConnectClick) {
                Text("Connect")
            }
        }
    ) {
        val context = LocalContext.current

        var isShowDetails by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.verticalScroll(state = rememberScrollState()),
        ) {
            Text(
                text = "We need the access notification permission to sync/connect your players.",
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isShowDetails) "But wait," else "But wait...",
                modifier = Modifier.clickable {
                    isShowDetails = !isShowDetails
                },
                textDecoration = TextDecoration.Underline,
            )

            if (isShowDetails) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                    Spacer(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(
                                MaterialTheme.colorScheme
                                    .onBackground.copy(alpha = 0.5f)
                            ),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Why should I give you permission to access my notifications?",
                        fontStyle = FontStyle.Italic,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = """    
                            This is how this App works, we only read necessary data from your media notifications.
                            And the App is open-sourced, both Android code and server code, we only use your playback history to recommend the next track.
                        """.trimIndent(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Check our open-sourced repo:")

                val url = "https://github.com/dokar3/upnext-gpt"
                Text(
                    text = url,
                    modifier = Modifier.clickable {
                        IntentUtil.openUrl(context, url)
                    },
                    textDecoration = TextDecoration.Underline,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Tap the Connect button to continue.")
            }
        }
    }
}