package io.upnextgpt.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sheets.BottomSheetState
import com.dokar.sheets.m3.BottomSheet
import io.upnextgpt.base.ImmutableHolder
import io.upnextgpt.ui.home.viewmodel.MusicApp
import io.upnextgpt.ui.shared.widget.CardButton

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PlayerSelectorSheet(
    state: BottomSheetState,
    items: ImmutableHolder<List<MusicApp>>,
    onSelect: (item: MusicApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomSheet(
        state = state,
        modifier = modifier,
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
        skipPeeked = true,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            stickyHeader {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Select Player",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            items(
                items = items.value,
                key = { it.info.packageName },
            ) {
                PlayerItem(
                    item = it,
                    onSelect = { onSelect(it) },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun PlayerItem(
    item: MusicApp,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val enabled = item.isInstalled && !item.isActive

    val backgroundBrush =
        remember(item.info.themeColor, item.isActive, item.isInstalled) {
            if (!item.isInstalled) {
                Brush.linearGradient(
                    colors = listOf(
                        item.info.themeColor.copy(alpha = 0.3f),
                        Color.Transparent,
                    ),
                )
            } else if (item.isActive) {
                SolidColor(item.info.themeColor)
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        item.info.themeColor,
                        Color.Transparent,
                    ),
                )
            }
        }

    CardButton(
        padding = PaddingValues(0.dp),
        onClick = onSelect,
        clickRequiresUnConsumed = false,
        enabled = enabled,
        backgroundColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(backgroundBrush)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(item.info.iconRes),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(item.info.appName)
            }

            RadioButton(
                selected = item.isActive,
                onClick = onSelect,
                modifier = Modifier.drawWithContent {
                    if (item.isInstalled) {
                        drawContent()
                    }
                },
                enabled = enabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.White.copy(alpha = 0.9f),
                    disabledSelectedColor = Color.White.copy(alpha = 0.9f),
                    disabledUnselectedColor = Color.White.copy(alpha = 0.9f),
                )
            )
        }
    }
}