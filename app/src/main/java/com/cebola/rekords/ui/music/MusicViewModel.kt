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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ViewType { GRID, LIST }

data class MusicScreenState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val viewType: ViewType = ViewType.GRID
)

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase,
    private val controllerFuture: ListenableFuture<MediaController>
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicScreenState())
    val uiState = _uiState.asStateFlow()

    private var mediaController: MediaController? = null
    private val mediaItemsCache = mutableMapOf<String, MediaItem>()
    private var pendingTrack: Track? = null

    init {
        initializeMediaController()
        loadTracks()
    }

    private fun initializeMediaController() {
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            pendingTrack?.let {
                handleTrackSelection(it)
                pendingTrack = null
            }
        }, MoreExecutors.directExecutor())
    }

    private fun loadTracks() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getTracksUseCase()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Falha ao carregar as músicas: ${e.localizedMessage ?: "Erro desconhecido"}"
                        )
                    }
                }
                .collect { allTracks ->
                    _uiState.update {
                        it.copy(
                            tracks = allTracks,
                            isLoading = false,
                            error = if (allTracks.isEmpty()) "Nenhuma música encontrada." else null
                        )
                    }
                    buildMediaItemsCache(allTracks)
                }
        }
    }

    fun onToggleViewType() {
        val newViewType = when (_uiState.value.viewType) {
            ViewType.GRID -> ViewType.LIST
            ViewType.LIST -> ViewType.GRID
        }
        _uiState.update { it.copy(viewType = newViewType) }
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }

    fun onTrackClick(clickedTrack: Track) {
        handleTrackSelection(clickedTrack)
    }

    private fun handleTrackSelection(clickedTrack: Track) {
        val controller = mediaController
        if (controller == null) {
            pendingTrack = clickedTrack
            return
        }

        if (_uiState.value.isLoading) {
            _uiState.update { it.copy(error = "Aguarde o carregamento das músicas.") }
            return
        }

        val clickedTrackId = clickedTrack.id.toString()
        val isSameTrack = controller.currentMediaItem?.mediaId == clickedTrackId

        if (isSameTrack) {
            togglePlayPause(controller)
        } else {
            initializePlaylistAndPlay(controller, clickedTrack)
        }
    }

    private fun togglePlayPause(controller: MediaController) {
        if (controller.isPlaying) controller.pause() else controller.play()
    }

    private fun initializePlaylistAndPlay(controller: MediaController, track: Track) {
        val trackIndex = _uiState.value.tracks.indexOfFirst { it.id == track.id }
        if (trackIndex == -1) {
            _uiState.update { it.copy(error = "Faixa não encontrada na lista.") }
            return
        }

        val mediaItems = _uiState.value.tracks.map { tr ->
            mediaItemsCache[tr.id.toString()] ?: buildMediaItem(tr)
        }

        controller.setMediaItems(mediaItems, trackIndex, 0L)
        controller.prepare()
        controller.play()
    }

    private fun buildMediaItemsCache(tracks: List<Track>) {
        tracks.forEach { track ->
            mediaItemsCache.getOrPut(track.id.toString()) {
                buildMediaItem(track)
            }
        }
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