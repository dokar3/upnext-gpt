package io.upnextgpt.ui.shared.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun BasicDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    titlePadding: PaddingValues = PaddingValues(
        horizontal = 24.dp,
        vertical = 16.dp,
    ),
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    actionsPadding: PaddingValues = PaddingValues(
        horizontal = 24.dp,
        vertical = 16.dp,
    ),
    title: @Composable (ColumnScope.() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            if (title != null) {
                CompositionLocalProvider(
                    LocalTextStyle provides LocalTextStyle.current.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(titlePadding)
                    ) {
                        title()
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(weight = 1f, fill = false)
                    .padding(contentPadding)
            ) {
                content()
            }

            if (actions != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(actionsPadding),
                    horizontalArrangement = Arrangement.End,
                ) {
                    actions()
                }
            }
        }
    }
}