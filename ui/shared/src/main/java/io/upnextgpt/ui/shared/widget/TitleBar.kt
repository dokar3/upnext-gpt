package io.upnextgpt.ui.shared.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.upnextgpt.ui.shared.Jost
import io.upnextgpt.ui.shared.R as SharedR

@Composable
fun TitleBar(
    title: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    endButton: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (showBack) {
                IconButton(onClick = { onBack?.invoke() }) {
                    Icon(
                        painter = painterResource(
                            SharedR.drawable.baseline_keyboard_backspace_24
                        ),
                        contentDescription = "Back",
                    )
                }
            }
        }

        Text(
            text = title,
            fontFamily = FontFamily.Jost,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )

        Box(modifier = Modifier.width(56.dp)) {
            if (endButton != null) {
                endButton()
            }
        }
    }
}