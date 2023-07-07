package io.upnextgpt.ui.home.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.upnextgpt.base.util.IntentUtil
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
    private val application: Application,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val context: Context get() = application.applicationContext

    private var player = NotificationBasedPlayer(context)

    private var playerListenJob: Job? = null

    private val _uiState = MutableStateFlow(HomeUiState())

    private var playerList = SupportedPlayers

    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        updatePlayerList()
        updatePlayerConnectionStatus()
        listenPlaybackUpdates()
    }

    private fun listenPlaybackUpdates() {
        playerListenJob?.cancel()
        playerListenJob = viewModelScope.launch(dispatcher) {
            player.prepare()
            player.playbackInfoFlow()
                .collect { info ->
                    val packageName = info?.packageName
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
        player.pause()
    }

    fun play() {
        player.play()
    }

    fun seek(position: Long) {
        player.seek(position)
    }

    fun selectPlayer(meta: PlayerMeta) = viewModelScope.launch(dispatcher) {
        val currPlayer = uiState.value.activeOrFirstPlayer
        if (currPlayer == meta) {
            return@launch
        }

        val players = playerList.map {
            it.copy(isActive = it.packageName == meta.packageName)
        }
        _uiState.update {
            it.copy(players = players)
        }
        player.pause()
        player.destroy()
        player = NotificationBasedPlayer(
            context = context,
            specifiedPackageName = meta.packageName,
        )
        listenPlaybackUpdates()
    }

    private fun updatePlayerList() = viewModelScope.launch(dispatcher) {
        val ctx = context
        val newList = SupportedPlayers.map {
            val installed = IntentUtil.isPackageInstalled(ctx, it.packageName)
            it.copy(isInstalled = installed)
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

    override fun onCleared() {
        super.onCleared()
        player.destroy()
    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(
                application = application,
            ) as T
        }
    }
}