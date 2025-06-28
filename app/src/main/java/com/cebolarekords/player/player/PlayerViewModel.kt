package com.cebolarekords.player.player

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var mediaController: MediaController? = null
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()
    private var positionTrackerJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateState()
        }

        override fun onIsPlayingChanged(playing: Boolean) {
            updateState()
            if (playing) startPositionTracker() else stopPositionTracker()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateState()
            // Se o player terminar, reseta a posição para o início visualmente.
            if (playbackState == Player.STATE_ENDED) {
                _uiState.update { it.copy(currentPosition = 0L) }
            }
        }

        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
            _uiState.update { it.copy(hasError = true, isBuffering = false) }
        }
    }

    init {
        initializeMediaController()
    }

    private fun initializeMediaController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture: ListenableFuture<MediaController> =
            MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            try {
                mediaController = controllerFuture.get()
                mediaController?.addListener(playerListener)
                updateState() // Atualização inicial do estado
                if (mediaController?.isPlaying == true) {
                    startPositionTracker()
                }
            } catch (e: Exception) {
                // Lidar com falha na conexão, ex: logar o erro
                _uiState.update { it.copy(hasError = true) }
            }
        }, MoreExecutors.directExecutor())
    }

    private fun startPositionTracker() {
        stopPositionTracker()
        positionTrackerJob = viewModelScope.launch {
            while (true) {
                val controller = mediaController
                if (controller?.isPlaying == true && controller.duration > 0) { // Adicionado check de duração para evitar loops em faixas sem duração ou não carregadas
                    val position = controller.currentPosition.coerceAtLeast(0L)
                    _uiState.update { currentState ->
                        // Atualiza apenas se a posição mudou para evitar recomposições desnecessárias
                        if (currentState.currentPosition != position) {
                            currentState.copy(currentPosition = position)
                        } else {
                            currentState
                        }
                    }
                    delay(100) // ALTERADO: Intervalo de atualização da posição para 100ms
                } else {
                    // Se não estiver tocando ou a duração for 0, para de rastrear e reseta a posição
                    stopPositionTracker()
                    _uiState.update { it.copy(currentPosition = 0L) }
                }
            }
        }
    }

    private fun stopPositionTracker() {
        positionTrackerJob?.cancel()
        positionTrackerJob = null
    }

    private fun updateState() {
        val controller = mediaController ?: return
        _uiState.update {
            it.copy(
                currentTrack = controller.currentMediaItem,
                isPlaying = controller.isPlaying,
                duration = controller.duration.coerceAtLeast(0L),
                isBuffering = controller.playbackState == Player.STATE_BUFFERING,
                hasError = false // Reseta o erro em uma atualização bem-sucedida
            )
        }
    }

    fun onPlayPauseClick() {
        val controller = mediaController ?: return
        // Adiciona checagem para garantir que há um item de mídia para tocar/pausar
        if (controller.currentMediaItem != null) {
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
        }
    }

    fun onSkipNextClick() {
        mediaController?.seekToNextMediaItem()
    }

    fun onSkipPreviousClick() {
        mediaController?.seekToPreviousMediaItem()
    }

    fun onSeek(position: Long) {
        mediaController?.seekTo(position.coerceAtLeast(0L))
        // Após o seek, force uma atualização para a UI imediatamente refletir a nova posição
        updateState()
    }

    fun getMediaController(): MediaController? = mediaController

    override fun onCleared() {
        stopPositionTracker()
        mediaController?.removeListener(playerListener)
        super.onCleared()
    }
}