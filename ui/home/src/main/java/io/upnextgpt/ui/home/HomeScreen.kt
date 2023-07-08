package io.upnextgpt.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import com.dokar.sheets.rememberBottomSheetState
import io.upnextgpt.base.ImmutableHolder
import io.upnextgpt.base.SealedResult
import io.upnextgpt.base.util.IntentUtil
import io.upnextgpt.data.model.Track
import io.upnextgpt.ui.home.control.CoverView
import io.upnextgpt.ui.home.control.PlayControlCard
import io.upnextgpt.ui.home.control.PlayerCard
import io.upnextgpt.ui.home.control.PlayerProgressBar
import io.upnextgpt.ui.home.control.UpNextCard
import io.upnextgpt.ui.home.viewmodel.HomeUiState
import io.upnextgpt.ui.home.viewmodel.HomeViewModel
import io.upnextgpt.ui.home.viewmodel.PlayerMeta
import io.upnextgpt.ui.shared.compose.rememberLifecycleEvent
import io.upnextgpt.ui.shared.widget.SnackbarType
import io.upnextgpt.ui.shared.widget.SpringDragBox
import io.upnextgpt.ui.shared.widget.SwipeableSnackbar
import io.upnextgpt.ui.shared.widget.TypedSnackbarVisuals
import io.upnextgpt.ui.shared.widget.snackbarShimmerBorder
import io.upnextgpt.ui.shared.widget.typedBorderColorOrNull
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleEvent = rememberLifecycleEvent()

    LaunchedEffect(viewModel, lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
            viewModel.updatePlayerConnectionStatus()
        }
    }

    Column(modifier = modifier) {
        AnimatedVisibility(
            visible = !uiState.isConnectedToPlayers,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            PlayersNotConnectedBar(
                onConnectClick = { viewModel.connectToPlayer() },
            )
        }

        TitleBar(modifier = Modifier.padding(horizontal = 32.dp))

        Player(
            uiState = uiState,
            onPlay = viewModel::play,
            onPause = viewModel::pause,
            onSeek = viewModel::seek,
            onSelectPlayer = viewModel::selectPlayer,
            onPlayTrack = viewModel::playTrack,
            onClearError = viewModel::clearError,
            onFetchNextTrackClick = viewModel::fetchNextTrack,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}

@Composable
private fun Player(
    uiState: HomeUiState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSeek: (position: Long) -> Unit,
    onSelectPlayer: (meta: PlayerMeta) -> Unit,
    onPlayTrack: (track: Track) -> Unit,
    onFetchNextTrackClick: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val currPlayer = uiState.activePlayer

    val track = uiState.currTrack

    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    val scrollState = rememberScrollState()

    val playerSelectorSheetState = rememberBottomSheetState()

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackBarHostState.showSnackbar(
                visuals = TypedSnackbarVisuals(
                    type = SnackbarType.Error,
                    message = uiState.error,
                    withDismissAction = true,
                ),
            )
            onClearError()
        }
    }

    DisposableEffect(Unit) {
        onDispose { onClearError() }
    }

    SpringDragBox(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {
            CoverView(
                key = track?.title + track?.artist,
                bitmap = ImmutableHolder(uiState.albumArt)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlayerProgressBar(
                isPlaying = uiState.isPlaying,
                position = uiState.position,
                duration = uiState.duration,
                onSeek = onSeek,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = track?.title ?: "Not Playing",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(text = track?.artist ?: "-")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PlayerCard(
                    playerName = currPlayer?.name ?: "-",
                    iconRes = currPlayer?.iconRes ?: 0,
                    themeColor = currPlayer?.themeColor
                        ?: MaterialTheme.colorScheme.secondary,
                    onClick = {
                        scope.launch { playerSelectorSheetState.expand() }
                    },
                    onLaunchPlayerClick = {
                        val packageName = currPlayer?.packageName
                            ?: return@PlayerCard
                        when (val ret =
                            IntentUtil.launchApp(context, packageName)) {
                            is SealedResult.Err -> scope.launch {
                                snackBarHostState
                                    .showSnackbar(
                                        message = ret.error,
                                        withDismissAction = true,
                                    )
                            }

                            is SealedResult.Ok -> {}
                        }
                    },
                    modifier = Modifier.weight(1f),
                )

                PlayControlCard(
                    isPlaying = uiState.isPlaying,
                    prevEnabled = true,
                    nextEnabled = uiState.nextTrack != null,
                    onPrevClick = {},
                    onPlayPauseClick = {
                        if (uiState.isPlaying) {
                            onPause()
                        } else {
                            onPlay()
                        }
                    },
                    onNextClick = {
                        if (uiState.nextTrack != null) {
                            onPlayTrack(uiState.nextTrack)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            UpNextCard(
                isRolling = uiState.isLoadingNextTrack,
                nextTrack = uiState.nextTrack,
                playEnabled = uiState.nextTrack != null,
                rollEnabled = !uiState.isLoadingNextTrack,
                onPlayClick = { uiState.nextTrack?.let { onPlayTrack(it) } },
                onRollClick = onFetchNextTrackClick,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            val contentColor = MaterialTheme.colorScheme.onBackground
            val borderShape = SnackbarDefaults.shape
            SwipeableSnackbar(
                snackbarData = it,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .snackbarShimmerBorder(
                        color = it.visuals.typedBorderColorOrNull()
                            ?: contentColor,
                        shape = borderShape,
                    ),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = contentColor,
                dismissActionContentColor = contentColor,
            )
        }
    }

    PlayerSelectorSheet(
        state = playerSelectorSheetState,
        items = ImmutableHolder(uiState.players),
        onSelect = {
            scope.launch { playerSelectorSheetState.collapse() }
            onSelectPlayer(it)
        },
    )
}

@Composable
private fun PlayersNotConnectedBar(
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFF9800))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Players are not connected.")

        TextButton(
            onClick = onConnectClick,
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
        ) {
            Text("Connect")
        }
    }
}