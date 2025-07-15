package com.cebola.rekords.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.cebola.rekords.data.Track
import com.cebola.rekords.di.IoDispatcher
import com.cebola.rekords.domain.usecase.GetTracksUseCase
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ViewType { GRID, LIST }

data class MusicScreenState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val viewType: ViewType = ViewType.GRID,
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.TITLE_ASC
)

enum class SortOrder {
    TITLE_ASC, TITLE_DESC, ARTIST_ASC, ARTIST_DESC, RECENT
}

@HiltViewModel
class MusicViewModel @Inject constructor(
    getTracksUseCase: GetTracksUseCase,
    private val controllerFuture: ListenableFuture<MediaController>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicScreenState())
    val uiState: StateFlow<MusicScreenState> = _uiState.asStateFlow()

    private var mediaController: MediaController? = null
    private val mediaItemsCache = mutableMapOf<String, MediaItem>()
    private var pendingTrack: Track? = null

    val filteredTracks: StateFlow<List<Track>> = combine(
        getTracksUseCase(),
        _uiState
    ) { allTracks, state ->
        _uiState.update {
            it.copy(
                isLoading = false,
                error = if (allTracks.isEmpty() && !it.isLoading) "Nenhuma música encontrada." else null
            )
        }
        buildMediaItemsCache(allTracks)

        val sorted = when (state.sortOrder) {
            SortOrder.TITLE_ASC -> allTracks.sortedBy { it.title }
            SortOrder.TITLE_DESC -> allTracks.sortedByDescending { it.title }
            SortOrder.ARTIST_ASC -> allTracks.sortedBy { it.artistName }
            SortOrder.ARTIST_DESC -> allTracks.sortedByDescending { it.artistName }
            SortOrder.RECENT -> allTracks.sortedByDescending { it.id }
        }
        if (state.searchQuery.isBlank()) {
            sorted
        } else {
            sorted.filter { track ->
                track.title.contains(state.searchQuery, ignoreCase = true) ||
                        track.artistName.contains(state.searchQuery, ignoreCase = true) ||
                        track.albumName.contains(state.searchQuery, ignoreCase = true)
            }
        }
    }.catch { e ->
        _uiState.update {
            it.copy(isLoading = false, error = "Falha ao carregar músicas: ${e.localizedMessage}")
        }
        emit(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        initializeMediaController()
    }

    private fun initializeMediaController() {
        controllerFuture.addListener({
            try {
                mediaController = controllerFuture.get()
                pendingTrack?.let { track ->
                    handleTrackSelection(track)
                    pendingTrack = null
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erro ao inicializar player: ${e.localizedMessage}") }
            }
        }, MoreExecutors.directExecutor())
    }

    fun onToggleViewType() {
        _uiState.update { it.copy(viewType = if (it.viewType == ViewType.GRID) ViewType.LIST else ViewType.GRID) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
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
        if (_uiState.value.isLoading) return

        val isSameTrack = controller.currentMediaItem?.mediaId == clickedTrack.id.toString()
        if (isSameTrack) {
            if (controller.isPlaying) controller.pause() else controller.play()
        } else {
            initializePlaylistAndPlay(controller, clickedTrack)
        }
    }

    private fun initializePlaylistAndPlay(controller: MediaController, track: Track) {
        val currentPlaylist = filteredTracks.value
        val trackIndex = currentPlaylist.indexOfFirst { it.id == track.id }
        if (trackIndex == -1) {
            _uiState.update { it.copy(error = "Faixa não encontrada na lista atual.") }
            return
        }
        val mediaItems = currentPlaylist.map { tr ->
            mediaItemsCache[tr.id.toString()] ?: buildMediaItem(tr)
        }
        viewModelScope.launch {
            try {
                controller.setMediaItems(mediaItems, trackIndex, 0L)
                controller.prepare()
                controller.play()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erro ao reproduzir música: ${e.localizedMessage}") }
            }
        }
    }

    private fun buildMediaItemsCache(tracks: List<Track>) {
        viewModelScope.launch(ioDispatcher) {
            tracks.forEach { track ->
                val trackId = track.id.toString()
                if (!mediaItemsCache.containsKey(trackId)) {
                    mediaItemsCache[trackId] = buildMediaItem(track)
                }
            }
        }
    }

    private fun buildMediaItem(track: Track): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artistName)
            .setAlbumTitle(track.albumName)
            .apply { track.artworkData?.let { setArtworkData(it, MediaMetadata.PICTURE_TYPE_FRONT_COVER) } }
            .build()
        return MediaItem.Builder()
            .setMediaId(track.id.toString())
            .setUri(track.audioUri)
            .setMediaMetadata(metadata)
            .build()
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null) }
    }
}