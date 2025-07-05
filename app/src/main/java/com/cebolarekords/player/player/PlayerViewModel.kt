package com.cebolarekords.player.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.session.MediaController
import com.cebolarekords.player.data.datastore.UserPreferencesRepository
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val currentTrack: MediaItem? = null,
    val isPlaying: Boolean = false,
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val isBuffering: Boolean = false,
    val hasError: Boolean = false
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val controllerFuture: ListenableFuture<MediaController>, // ALTERADO: Injetado via Hilt
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private var mediaController: MediaController? = null
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()
    private var positionTrackerJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { updateState() }
        override fun onIsPlayingChanged(playing: Boolean) {
            updateState()
            if (playing) startPositionTracker() else stopPositionTracker()
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            updateState()
            if (playbackState == Player.STATE_ENDED) {
                _uiState.update { it.copy(currentPosition = 0L) }
            }
        }
        override fun onPlayerError(error: PlaybackException) {
            _uiState.update { it.copy(hasError = true, isBuffering = false) }
        }
    }

    init {
        initializeMediaController()
    }

    private fun initializeMediaController() {
        // REMOVIDO: A criação do controller future agora é feita pelo Hilt
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            mediaController?.addListener(playerListener)
            updateState()
            if (mediaController?.isPlaying == true) {
                startPositionTracker()
            }
        }, MoreExecutors.directExecutor())
    }

    private fun startPositionTracker() {
        stopPositionTracker()
        positionTrackerJob = viewModelScope.launch {
            while (isActive) {
                mediaController?.currentPosition?.let { pos ->
                    _uiState.update { it.copy(currentPosition = pos.coerceAtLeast(0L)) }
                }
                delay(1000)
            }
        }
    }

    private fun stopPositionTracker() {
        positionTrackerJob?.cancel()
        positionTrackerJob = null
        mediaController?.currentPosition?.let { position ->
            if (position > 0) {
                viewModelScope.launch {
                    userPreferencesRepository.saveLastPlayedPosition(position)
                }
            }
        }
    }

    private fun updateState() {
        mediaController?.let { controller ->
            _uiState.update {
                it.copy(
                    currentTrack = controller.currentMediaItem,
                    isPlaying = controller.isPlaying,
                    duration = controller.duration.coerceAtLeast(0L),
                    isBuffering = controller.playbackState == Player.STATE_BUFFERING,
                    hasError = false
                )
            }
        }
    }

    fun onPlayPauseClick() { mediaController?.currentMediaItem?.let { if (mediaController?.isPlaying == true) mediaController?.pause() else mediaController?.play() } }
    fun onSkipNextClick() { mediaController?.seekToNextMediaItem() }
    fun onSkipPreviousClick() { mediaController?.seekToPreviousMediaItem() }
    fun onSeek(position: Long) { mediaController?.seekTo(position.coerceAtLeast(0L)) }

    // REMOVIDO: Não é mais necessário expor o controller diretamente.
    // fun getMediaController(): MediaController? = mediaController

    override fun onCleared() {
        stopPositionTracker()
        mediaController?.removeListener(playerListener)
        // A liberação da future é gerenciada pelo Hilt/escopo do Singleton, mas é bom manter o release aqui.
        MediaController.releaseFuture(controllerFuture)
        super.onCleared()
    }
}