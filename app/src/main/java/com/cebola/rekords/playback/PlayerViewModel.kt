package com.cebolarekords.player.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
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
    val hasError: Boolean = false,
    val hasNextTrack: Boolean = false,
    val hasPreviousTrack: Boolean = false
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val controllerFuture: ListenableFuture<MediaController>,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private var mediaController: MediaController? = null
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()
    private var positionTrackerJob: Job? = null
    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateTrackInfo()
            _uiState.update { it.copy(currentPosition = 0L) }
        }
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            updateTrackInfo()
        }
        override fun onIsPlayingChanged(playing: Boolean) {
            _uiState.update { it.copy(isPlaying = playing) }
            if (playing) startPositionTracker() else stopPositionTracker()
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            _uiState.update {
                it.copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING,
                    hasError = false
                )
            }
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
        controllerFuture.addListener({
            mediaController = try {
                controllerFuture.get().apply {
                    addListener(playerListener)
                    updateAllState()
                    if (isPlaying) startPositionTracker()
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(hasError = true) }
                null
            }
        }, MoreExecutors.directExecutor())
    }
    
    private fun startPositionTracker() {
        stopPositionTracker()
        positionTrackerJob = viewModelScope.launch {
            while (isActive) {
                val currentPosition = mediaController?.currentPosition?.coerceAtLeast(0L) ?: 0L
                _uiState.update { it.copy(currentPosition = currentPosition) }
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
    
    private fun updateAllState() {
        updateTrackInfo()
        mediaController?.let { controller ->
            _uiState.update {
                it.copy(
                    isPlaying = controller.isPlaying,
                    isBuffering = controller.playbackState == Player.STATE_BUFFERING,
                    currentPosition = controller.currentPosition.coerceAtLeast(0L)
                )
            }
        }
    }
    
    private fun updateTrackInfo() {
        mediaController?.let { controller ->
            _uiState.update {
                it.copy(
                    currentTrack = controller.currentMediaItem,
                    duration = controller.duration.coerceAtLeast(0L),
                    hasNextTrack = controller.hasNextMediaItem(),
                    hasPreviousTrack = controller.hasPreviousMediaItem()
                )
            }
        }
    }
    
    fun onPlayPauseClick() {
        mediaController?.currentMediaItem?.let {
            if (mediaController?.isPlaying == true) mediaController?.pause() else mediaController?.play()
        }
    }
    
    fun onSkipNextClick() {
        if (mediaController?.hasNextMediaItem() == true) mediaController?.seekToNextMediaItem()
    }
    
    fun onSkipPreviousClick() {
        if (mediaController?.hasPreviousMediaItem() == true) mediaController?.seekToPreviousMediaItem()
    }
    
    fun onSeek(position: Long) {
        mediaController?.seekTo(position.coerceAtLeast(0L))
    }
    
    override fun onCleared() {
        stopPositionTracker()
        mediaController?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFuture)
        super.onCleared()
    }
}