package com.cebola.rekords.playback

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import com.cebola.rekords.data.datastore.UserPreferencesRepository
import com.cebola.rekords.di.IoDispatcher
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val hasPreviousTrack: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: Int = Player.REPEAT_MODE_OFF
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val controllerFuture: ListenableFuture<MediaController>,
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private var mediaController: MediaController? = null
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
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
                    hasError = playbackState == Player.STATE_IDLE && uiState.value.currentTrack == null
                )
            }
            if (playbackState == Player.STATE_ENDED) {
                _uiState.update { it.copy(currentPosition = 0L) }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("PlayerViewModel", "Player Error", error)
            _uiState.update {
                it.copy(
                    hasError = true,
                    isBuffering = false
                )
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _uiState.update { it.copy(isShuffleEnabled = shuffleModeEnabled) }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _uiState.update { it.copy(repeatMode = repeatMode) }
        }

        override fun onPlaybackParametersChanged(playbackParameters: androidx.media3.common.PlaybackParameters) {
            _uiState.update { it.copy(playbackSpeed = playbackParameters.speed) }
        }
    }

    init {
        initializeMediaController()
    }

    private fun initializeMediaController() {
        controllerFuture.addListener({
            try {
                mediaController = controllerFuture.get().apply {
                    addListener(playerListener)
                    updateAllState()
                    if (isPlaying) startPositionTracker()
                }
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Failed to get MediaController", e)
                _uiState.update {
                    it.copy(
                        hasError = true,
                        isBuffering = false
                    )
                }
            }
        }, MoreExecutors.directExecutor())
    }

    private fun startPositionTracker() {
        stopPositionTracker()
        positionTrackerJob = viewModelScope.launch {
            while (isActive && mediaController?.isPlaying == true) {
                val currentPosition = mediaController?.currentPosition?.coerceAtLeast(0L) ?: 0L
                _uiState.update { it.copy(currentPosition = currentPosition) }
                delay(250)
            }
        }
    }

    private fun stopPositionTracker() {
        positionTrackerJob?.cancel()
        positionTrackerJob = null
        val lastPosition = mediaController?.currentPosition
        if (lastPosition != null && lastPosition > 0) {
            viewModelScope.launch(ioDispatcher) {
                try {
                    userPreferencesRepository.saveLastPlayedPosition(lastPosition)
                } catch (e: Exception) {
                    Log.e("PlayerViewModel", "Failed to save last played position.", e)
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
                    currentPosition = controller.currentPosition.coerceAtLeast(0L),
                    isShuffleEnabled = controller.shuffleModeEnabled,
                    repeatMode = controller.repeatMode,
                    playbackSpeed = controller.playbackParameters.speed
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
        mediaController?.let { controller ->
            controller.currentMediaItem?.let {
                if (controller.isPlaying) {
                    controller.pause()
                } else {
                    controller.play()
                }
            }
        }
    }

    fun onSkipNextClick() {
        if (mediaController?.hasNextMediaItem() == true) {
            mediaController?.seekToNextMediaItem()
        }
    }

    fun onSkipPreviousClick() {
        if (mediaController?.hasPreviousMediaItem() == true) {
            mediaController?.seekToPreviousMediaItem()
        }
    }

    fun onSeek(position: Long) {
        mediaController?.seekTo(position.coerceAtLeast(0L))
    }

    fun onShuffleToggle() {
        mediaController?.let { controller ->
            controller.shuffleModeEnabled = !controller.shuffleModeEnabled
            viewModelScope.launch { userPreferencesRepository.saveShuffleMode(controller.shuffleModeEnabled) }
        }
    }

    fun onRepeatModeToggle() {
        mediaController?.let { controller ->
            val nextMode = when (controller.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                else -> Player.REPEAT_MODE_OFF
            }
            controller.repeatMode = nextMode
            viewModelScope.launch { userPreferencesRepository.saveRepeatMode(nextMode) }
        }
    }

    override fun onCleared() {
        stopPositionTracker()
        mediaController?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFuture)
        super.onCleared()
    }
}