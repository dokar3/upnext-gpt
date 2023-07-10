package io.upnextgpt.ui.home.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.upnextgpt.base.AppLauncher
import io.upnextgpt.base.SealedResult
import io.upnextgpt.base.image.DiskImageStore
import io.upnextgpt.base.util.remove
import io.upnextgpt.data.fetcher.NextTrackFetcher
import io.upnextgpt.data.model.Track
import io.upnextgpt.data.repository.TrackRepository
import io.upnextgpt.data.settings.Settings
import io.upnextgpt.data.settings.TrackFinishedAction
import io.upnextgpt.remote.palyer.NotificationBasedPlayer
import io.upnextgpt.remote.palyer.PlayState
import io.upnextgpt.remote.palyer.RemotePlayer
import io.upnextgpt.remote.palyer.toTrack
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val player: NotificationBasedPlayer,
    private val settings: Settings,
    private val appLauncher: AppLauncher,
    private val nextTrackFetcher: NextTrackFetcher,
    private val trackRepo: TrackRepository,
    private val diskImageStore: DiskImageStore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private var fetchNextTrackJob: Job? = null

    private var playerList = SupportedPlayers

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _playerQueue = MutableStateFlow<List<Track>>(emptyList())
    val playerQueue: StateFlow<List<Track>> = _playerQueue

    private val _removedTrack = MutableStateFlow<RemovedTrack?>(null)
    val removedTrack: StateFlow<RemovedTrack?> = _removedTrack

    init {
        loadQueue()
        updatePlayerList()
        updatePlayerConnectionStatus()
        listenCurrPlayer()
        listenPlaybackStates()
        listenPlaybackEvents()
        listenCurrTrack()
        listenNextTrack()
    }

    private fun listenCurrPlayer() = viewModelScope.launch(dispatcher) {
        settings.currentPlayerFlow.collect { currPlayer ->
            player.updateTargetPlayer(packageName = currPlayer)
            val players = playerList.map {
                it.copy(isActive = it.packageName == currPlayer)
            }
            _uiState.update { it.copy(players = players) }
        }
    }

    private fun listenPlaybackStates() = viewModelScope.launch(dispatcher) {
        player.prepare()
        player.playbackInfoFlow().collect { info ->
            val packageName = info?.packageName
                ?: uiState.value.activePlayer?.packageName
            val players = playerList.map {
                it.copy(isActive = packageName == it.packageName)
            }
            val currTrack = info?.toTrack()?.let {
                val saved = trackRepo.get(id = it.id)
                it.copy(
                    liked = saved?.liked,
                    disliked = saved?.disliked,
                    queueId = saved?.queueId,
                    addedAt = saved?.addedAt ?: -1,
                    updatedAt = saved?.updatedAt ?: -1,
                )
            }
            _uiState.update {
                it.copy(
                    players = players,
                    currTrack = currTrack,
                    isPlaying = info?.playState == PlayState.Playing,
                    position = info?.position ?: 0L,
                    duration = info?.duration ?: 0L,
                    albumArt = info?.albumArt,
                )
            }

            if (currTrack != null) {
                updateDiskAlbumArt(
                    track = currTrack,
                    bitmap = info.albumArt,
                    alwaysUpdate = false,
                )
            }
        }
    }

    private fun listenPlaybackEvents() = viewModelScope.launch(dispatcher) {
        player.playbackEventFlow().collect {
            if (it == RemotePlayer.PlaybackEvent.TrackFinished) {
                handleTrackFinished()
            }
        }
    }

    private suspend fun handleTrackFinished() {
        when (settings.trackFinishedActionFlow.firstOrNull()) {
            null,
            TrackFinishedAction.None -> {
            }

            TrackFinishedAction.Pause -> {
                player.pause()
            }

            TrackFinishedAction.PauseAndOpenApp -> {
                player.pause()
                appLauncher.launchSelf()
            }

            TrackFinishedAction.OpenPlayerToPlayNext -> {
                player.pause()
                val nextTrack = awaitNextTrackOrNull() ?: return
                // Play next track on finish
                playTrack(nextTrack)
            }
        }
    }

    private suspend fun awaitNextTrackOrNull(): Track? {
        val state = uiState.value
        if (state.nextTrack != null) return state.nextTrack
        if (!state.isLoadingNextTrack) return null
        return uiState.filter { !it.isLoadingNextTrack }
            .first()
            .nextTrack
    }

    private fun listenCurrTrack() = viewModelScope.launch(dispatcher) {
        uiState.map { it.currTrack }
            .filterNotNull()
            .distinctUntilChangedBy { it.title + it.artist }
            .collect { currTrack ->
                addTrackToQueue(currTrack)
                trackRepo.save(currTrack)
                fetchNextTrack()
                updateDiskAlbumArt(currTrack, uiState.value.albumArt)
            }
    }

    private fun addTrackToQueue(track: Track) {
        val list = playerQueue.value.toMutableList()
        list.remove { it.id == track.id }
        list.add(0, track)
        _playerQueue.update { list }
    }

    private fun listenNextTrack() = viewModelScope.launch(dispatcher) {
        uiState.map { it.nextTrack?.id }
            .filterNotNull()
            .collect { nextTrackId ->
                settings.updateNextTrackId(nextTrackId)
            }
    }

    private fun loadQueue() = viewModelScope.launch(dispatcher) {
        val list = trackRepo.getQueueTracks(queueId = null)
        _playerQueue.update { list }

        val nextTrackId = settings.nextTrackIdFlow.firstOrNull()
        val nextTrack = list.find { it.id == nextTrackId }
        _uiState.update { it.copy(nextTrack = nextTrack) }
    }

    private fun updateDiskAlbumArt(
        track: Track,
        bitmap: Bitmap?,
        alwaysUpdate: Boolean = true,
    ) = viewModelScope.launch(dispatcher) {
        val key = track.id.toString()
        if (bitmap != null) {
            if (alwaysUpdate || !diskImageStore.exists(key)) {
                diskImageStore.save(
                    bitmap = bitmap,
                    key = track.id.toString(),
                )
            }
        } else {
            diskImageStore.delete(key)
        }
    }

    fun updatePlayerConnectionStatus() = viewModelScope.launch(dispatcher) {
        val isConnected = player.isConnected()
        _uiState.update { it.copy(isConnectedToPlayers = isConnected) }
        if (isConnected) {
            if (!player.isPrepared()) {
                player.prepare()
            }
        } else {
            player.sync()
            _uiState.update {
                it.copy(
                    currTrack = null,
                    albumArt = null,
                    isPlaying = false,
                    position = -1,
                    duration = -1,
                    nextTrack = null,
                )
            }
        }
    }

    fun connectToPlayers() = viewModelScope.launch(dispatcher) {
        player.connect()
    }

    fun pause() {
        controlOrLaunchPlayer { player.pause() }
    }

    fun play() {
        controlOrLaunchPlayer { player.play() }
    }

    fun seek(position: Long) {
        controlOrLaunchPlayer { player.seek(position) }
    }

    fun playTrack(track: Track) {
        val currPlayer = uiState.value.activePlayer ?: return
        appLauncher.playTrack(
            packageName = currPlayer.packageName,
            title = track.title,
            artist = track.artist,
            album = track.album,
        )
    }

    fun likeTrack(track: Track) = viewModelScope.launch(dispatcher) {
        val updated = track.copy(liked = true, disliked = null)
        updateTrack(updated)
    }

    fun cancelLikeTrack(track: Track) = viewModelScope.launch(dispatcher) {
        val updated = track.copy(liked = null, disliked = null)
        updateTrack(updated)
    }

    fun dislikeTrack(track: Track) = viewModelScope.launch(dispatcher) {
        val updated = track.copy(liked = null, disliked = true)
        updateTrack(updated)
    }

    fun cancelDislikeTrack(track: Track) = viewModelScope.launch(dispatcher) {
        val updated = track.copy(liked = null, disliked = null)
        updateTrack(updated)
    }

    private suspend fun updateTrack(track: Track) {
        // Current track
        val currTrack = uiState.value.currTrack
        if (currTrack?.id == track.id) {
            _uiState.update { it.copy(currTrack = track) }
        }
        // Queue track
        val queueIndex = playerQueue.value.indexOfFirst { it.id == track.id }
        if (queueIndex != -1) {
            val list = playerQueue.value.toMutableList()
            list[queueIndex] = track
            _playerQueue.update { list }
        }
        // Database
        if (trackRepo.exists(track.id)) {
            trackRepo.save(track)
        }
    }

    fun removeTrackFromQueue(track: Track) = viewModelScope.launch(dispatcher) {
        trackRepo.delete(track.id)
        diskImageStore.delete(track.id.toString())
        val list = playerQueue.value.toMutableList()
        val index = list.remove { it.id == track.id }
        if (index != null) {
            _playerQueue.update { list }
            _removedTrack.update { RemovedTrack(index = index, data = track) }
        }
    }

    fun insertTrackToQueue(track: Track, index: Int) = viewModelScope.launch(
        dispatcher
    ) {
        trackRepo.save(track)
        val list = playerQueue.value.toMutableList()
        list.add(index.coerceIn(0, list.size), track)
        _playerQueue.update { list }
    }

    fun clearRemovedTrack() {
        _removedTrack.update { null }
    }

    fun selectPlayer(meta: PlayerMeta) = viewModelScope.launch(dispatcher) {
        val currPlayer = uiState.value.activePlayer
        if (currPlayer == meta) {
            return@launch
        }
        settings.updateCurrentPlayer(meta.packageName)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearQueue(queueId: String? = null) = viewModelScope.launch(
        dispatcher
    ) {
        val currTrack = uiState.value.currTrack
        // Clear album arts
        trackRepo.getQueueTracks(queueId).forEach {
            if (currTrack == null || it.id == currTrack.id) {
                diskImageStore.delete(it.id.toString())
            }
        }
        // Clear db queue tracks
        trackRepo.clearQueue(queueId)
        if (currTrack != null) {
            trackRepo.save(currTrack)
        }
        // Update ui state
        val newQueue = if (currTrack != null) {
            listOf(currTrack)
        } else {
            emptyList()
        }
        _playerQueue.update { newQueue }
    }

    fun fetchNextTrack() {
        fetchNextTrackJob?.cancel()
        val queue = playerQueue.value
        if (queue.isEmpty()) {
            return
        }
        fetchNextTrackJob = viewModelScope.launch(dispatcher) {
            _uiState.update {
                it.copy(
                    isLoadingNextTrack = true,
                    error = null,
                )
            }
            when (val ret = nextTrackFetcher.fetch(queue)) {
                is SealedResult.Err -> _uiState.update {
                    it.copy(
                        isLoadingNextTrack = false,
                        error = ret.error.message,
                    )
                }

                is SealedResult.Ok -> _uiState.update {
                    it.copy(
                        isLoadingNextTrack = false,
                        nextTrack = ret.data,
                    )
                }
            }
        }
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

    private inline fun controlOrLaunchPlayer(action: () -> Unit) {
        if (player.isControllable()) {
            action()
        } else {
            val activePlayer = uiState.value.activePlayer
            if (activePlayer != null) {
                appLauncher.launchPackage(activePlayer.packageName)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.destroy()
    }
}