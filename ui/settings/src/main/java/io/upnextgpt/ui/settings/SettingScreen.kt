package io.upnextgpt.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.upnextgpt.ui.settings.items.ApiBaseUrlItem
import io.upnextgpt.ui.settings.viewmodel.SettingsViewModel
import io.upnextgpt.ui.shared.widget.TitleBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val itemModifier = Modifier
        .clip(MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.24f),
            shape = MaterialTheme.shapes.medium,
        )
        .padding(8.dp)

    Column(modifier = modifier.fillMaxSize()) {
        TitleBar(
            title = "Settings",
            showBack = true,
            onBack = onBack,
        )

        Divider()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            itemWithTitle("API URL") {
                ApiBaseUrlItem(
                    apiBaseUrl = uiState.apiBaseUrl,
                    isTestingUrl = uiState.isTestingApiBaseUrl,
                    isUrlWorkingProperly = uiState.isApiBaseUrlWorkingProperly,
                    onTestClick = viewModel::testApiBaseUrl,
                    onSubmit = viewModel::updateApiBaseUrl,
                    modifier = itemModifier,
                )
            }
        }
    }
}

fun LazyListScope.itemWithTitle(
    title: String,
    content: @Composable () -> Unit
) {
    item {
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        content()
    }
}