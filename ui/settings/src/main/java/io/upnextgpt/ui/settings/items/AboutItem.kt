package io.upnextgpt.ui.settings.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.upnextgpt.base.ImmutableHolder
import io.upnextgpt.base.util.IntentUtil
import io.upnextgpt.ui.settings.Contributor
import io.upnextgpt.ui.settings.GH_CONTRIBUTORS
import io.upnextgpt.ui.shared.R as SharedR

private data class SocialItem(
    val name: String,
    val url: String,
    val iconRes: Int,
    val color: Color?,
)

private val SocialItems = listOf(
    SocialItem(
        name = "Github",
        url = "https://github.com/dokar3/upnext-gpt",
        iconRes = SharedR.drawable.github,
        color = null,
    ),
    SocialItem(
        name = "Twitter",
        url = "https://twitter.com/EnDeepFour",
        iconRes = SharedR.drawable.twitter,
        color = Color(0xFF1D9BF0),
    )
)

@Composable
internal fun AboutItem(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Text("Open-sourced by dokar")

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(8.dp))

        SocialItemList(items = ImmutableHolder(SocialItems))

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.24f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Contributors")

        ContributorList(items = ImmutableHolder(GH_CONTRIBUTORS))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SocialItemList(
    items: ImmutableHolder<List<SocialItem>>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items.value.forEach { item ->
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        IntentUtil.openUrl(context, item.url)
                    }
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(item.iconRes),
                    contentDescription = item.name,
                    modifier = Modifier.size(36.dp),
                    colorFilter = ColorFilter.tint(
                        item.color
                            ?: MaterialTheme.colorScheme.onBackground,
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.name,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContributorList(
    items: ImmutableHolder<List<Contributor>>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items.value.forEach { item ->
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        IntentUtil.openUrl(context, item.url)
                    }
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = item.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme
                                .onBackground.copy(alpha = 0.1f)
                        ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}