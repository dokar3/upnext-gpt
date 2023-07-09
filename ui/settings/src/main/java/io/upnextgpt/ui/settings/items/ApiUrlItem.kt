package io.upnextgpt.ui.settings.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun ApiBaseUrlItem(
    apiBaseUrl: String?,
    isTestingUrl: Boolean,
    isUrlWorkingProperly: Boolean?,
    onTestClick: () -> Unit,
    onSubmit: (value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEditing by remember { mutableStateOf(false) }

    var value by remember(apiBaseUrl) { mutableStateOf(apiBaseUrl ?: "") }

    Column(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            enabled = isEditing,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = {
                    if (isEditing) {
                        value = apiBaseUrl ?: ""
                        isEditing = false
                    } else {
                        onTestClick()
                    }
                },
                enabled = isEditing || !isTestingUrl,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
            ) {
                if (isTestingUrl) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                } else if (!isEditing && isUrlWorkingProperly == true) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                } else if (!isEditing && isUrlWorkingProperly == false) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = if (isEditing) "Cancel" else "Test",
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            TextButton(
                onClick = {
                    if (isEditing) {
                        onSubmit(value)
                    }
                    isEditing = !isEditing
                },
                enabled = !isEditing || value != apiBaseUrl,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
            ) {
                Text(
                    text = if (isEditing) "Save" else "Edit",
                )
            }
        }
    }
}