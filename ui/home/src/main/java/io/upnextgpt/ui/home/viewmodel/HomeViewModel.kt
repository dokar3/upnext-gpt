package io.upnextgpt.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.upnextgpt.base.AppLauncher
import io.upnextgpt.data.settings.Settings
import io.upnextgpt.remote.palyer.NotificationBasedPlayer
import io.upnextgpt.remote.palyer.PlayState
import io.upnextgpt.remote.palyer.toTrackInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val player: NotificationBasedPlayer,
    private val settings: Settings,
    private val appLauncher: AppLauncher,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private var playerListenJob: Job? = null

    private val _uiState = MutableStateFlow(HomeUiState())

    private var playerList = SupportedPlayers

    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        updatePlayerList()
        updatePlayerConnectionStatus()
        listenCurrPlayerChanges()
        listenPlaybackUpdates()
    }

    private fun listenCurrPlayerChanges() = viewModelScope.launch(dispatcher) {
        settings.currentPlayerFlow.collect { currPlayer ->
            player.updateTargetPlayer(packageName = currPlayer)
            val players = playerList.map {
                it.copy(isActive = it.packageName == currPlayer)
            }
            _uiState.update { it.copy(players = players) }
        }
    }

    private fun listenPlaybackUpdates() {
        playerListenJob?.cancel()
        playerListenJob = viewModelScope.launch(dispatcher) {
            player.prepare()
            player.playbackInfoFlow()
                .collect { info ->
                    val packageName = info?.packageName
                        ?: uiState.value.activePlayer?.packageName
                    val players = playerList.map {
                        it.copy(isActive = packageName == it.packageName)
                    }
                    _uiState.update {
                        it.copy(
                            players = players,
                            trackInfo = info?.toTrackInfo(),
                            isPlaying = info?.playState == PlayState.Playing,
                            position = info?.position ?: 0L,
                            duration = info?.duration ?: 0L,
                            albumArt = info?.albumArt,
                        )
                    }
                }
        }
    }

    fun updatePlayerConnectionStatus() = viewModelScope.launch(dispatcher) {
        _uiState.update {
            it.copy(isConnectedToPlayers = player.isConnected())
        }
    }

    fun connectToPlayer() = viewModelScope.launch(dispatcher) {
        player.connect()
    }

    fun pause() {
        controlOrLunchPlayer { player.pause() }
    }

    fun play() {
        controlOrLunchPlayer { player.play() }
    }

    fun seek(position: Long) {
        controlOrLunchPlayer { player.seek(position) }
    }

    fun selectPlayer(meta: PlayerMeta) = viewModelScope.launch(dispatcher) {
        val currPlayer = uiState.value.activePlayer
        if (currPlayer == meta) {
            return@launch
        }
        settings.updateCurrentPlayer(meta.packageName)
    }

    private fun updatePlayerList() = viewModelScope.launch(dispatcher) {
        val newList = SupportedPlayers.map {
            it.copy(isInstalled = appLauncher.isInstalled(it.packageName))
        }
        if (newList != playerList) {
            playerList = newList
            val isInstalledMap = newList.associate {
                it.packageName to it.isInstalled
            }
            val players = uiState.value.players.map {
                it.copy(
                    isInstalled = isInstalledMap[it.packageName]
                        ?: it.isInstalled,
                )
            }
            _uiState.update { it.copy(players = players) }
        }
    }

    private inline fun controlOrLunchPlayer(action: () -> Unit) {
        if (player.isControllable()) {
            action()
        } else {
            val activePlayer = uiState.value.activePlayer
            if (activePlayer != null) {
                appLauncher.lunchPackage(activePlayer.packageName)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.destroy()
    }
}