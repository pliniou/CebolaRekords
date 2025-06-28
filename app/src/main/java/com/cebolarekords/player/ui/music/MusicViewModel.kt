package com.cebolarekords.player.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.cebolarekords.player.data.CebolaRepository
import com.cebolarekords.player.data.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MusicScreenState(
    val tracks: List<Track> = emptyList(), // ALTERADO: Agora é uma lista plana de músicas
    val isLoading: Boolean = true
)

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: CebolaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            val allTracks = repository.getAllTracks() // O repositório já retorna ordenado
            _uiState.update {
                it.copy(
                    tracks = allTracks, // ALTERADO: Atribui a lista plana
                    isLoading = false
                )
            }
        }
    }

    fun onTrackClick(clickedTrack: Track, mediaController: MediaController?) {
        mediaController?.let { controller ->
            val currentState = _uiState.value
            if (currentState.isLoading) return

            val playlist = currentState.tracks // ALTERADO: Usa a lista plana de músicas como playlist
            val playlistAsMediaItems = playlist.map { it.toMediaItem() }
            val trackIndex = playlist.indexOfFirst { it.id == clickedTrack.id }

            if (trackIndex != -1) {
                if (controller.currentMediaItem?.mediaId == clickedTrack.id.toString()) {
                    if (controller.isPlaying) controller.pause() else controller.play()
                } else {
                    controller.setMediaItems(playlistAsMediaItems, trackIndex, 0L)
                    controller.prepare()
                    controller.play()
                }
            }
        }
    }

    // ALTERADO: Agora passa a arte de capa como ByteArray para o MediaItem.
    private fun Track.toMediaItem(): MediaItem {
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artistName)
            .setAlbumTitle(albumName)
            .setArtworkUri(this.artworkUri) // O Coil usa esta URI para a lista de músicas.

        // O player (MediaSession) usa estes dados para a notificação e tela de bloqueio.
        this.artworkData?.let {
            metadataBuilder.setArtworkData(it, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
        }

        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(this.audioUri)
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }
}