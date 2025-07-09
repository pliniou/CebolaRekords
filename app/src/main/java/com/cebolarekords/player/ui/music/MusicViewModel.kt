package com.cebolarekords.player.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.cebolarekords.player.data.Track
import com.cebolarekords.player.domain.usecase.GetTracksUseCase
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MusicScreenState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase,
    private val controllerFuture: ListenableFuture<MediaController>
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicScreenState())
    val uiState = _uiState.asStateFlow()

    private val trackToMediaItemCache = mutableMapOf<Int, MediaItem>()
    private var mediaController: MediaController? = null
    private var isPlaylistSet = false

    init {
        loadTracks()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
        }, MoreExecutors.directExecutor())
    }

    private fun loadTracks() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val allTracks = getTracksUseCase()
                _uiState.update {
                    it.copy(
                        tracks = allTracks,
                        isLoading = false,
                        error = if (allTracks.isEmpty()) "Nenhuma música encontrada." else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Falha ao carregar as músicas."
                    )
                }
            }
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }

    fun onTrackClick(clickedTrack: Track) {
        val controller = mediaController ?: return
        val currentState = _uiState.value
        if (currentState.isLoading) return

        val clickedTrackId = clickedTrack.id.toString()

        // Se a faixa clicada já está tocando, apenas pausa/retoma.
        if (controller.currentMediaItem?.mediaId == clickedTrackId) {
            if (controller.isPlaying) controller.pause() else controller.play()
            return
        }

        val trackIndex = currentState.tracks.indexOfFirst { it.id == clickedTrack.id }
        if (trackIndex == -1) return

        // OTIMIZADO: Define a playlist no controller apenas uma vez.
        if (!isPlaylistSet) {
            val playlistAsMediaItems = currentState.tracks.map { track ->
                trackToMediaItemCache.getOrPut(track.id) { buildMediaItem(track) }
            }
            controller.setMediaItems(playlistAsMediaItems, trackIndex, 0L)
            isPlaylistSet = true
        } else {
            // Se a playlist já está definida, apenas navega para a faixa.
            controller.seekTo(trackIndex, 0L)
        }

        controller.prepare()
        controller.play()
    }

    private fun buildMediaItem(track: Track): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artistName)
            .setAlbumTitle(track.albumName)
            .apply {
                track.artworkData?.let {
                    setArtworkData(it, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                }
            }
            .build()

        return MediaItem.Builder()
            .setMediaId(track.id.toString())
            .setUri(track.audioUri)
            .setMediaMetadata(metadata)
            .build()
    }
}