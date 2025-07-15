package com.cebolarekords.player.ui.streaming

import androidx.lifecycle.ViewModel
import com.cebolarekords.player.data.StreamContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class StreamingUiState(
    val selectedTab: Int = 0, // 0 para SoundCloud, 1 para YouTube
    val soundCloudPlaylists: List<StreamContent> = emptyList(),
    val youtubeVideos: List<StreamContent> = emptyList()
)

@HiltViewModel
class StreamingViewModel @Inject constructor(
    // REFINAMENTO: Injeta o DataSource para desacoplar a fonte de dados da ViewModel,
    // melhorando a testabilidade e aderindo aos princípios de Injeção de Dependência.
    private val streamDataSource: LocalStreamDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreamingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadContent()
    }

    private fun loadContent() {
        // REFINAMENTO: Carrega os dados a partir do DataSource injetado, em vez de
        // instanciá-los diretamente.
        val soundCloudContent = streamDataSource.getSoundCloudPlaylists()
        val youtubeContent = streamDataSource.getYoutubeVideos()
        _uiState.update {
            it.copy(
                soundCloudPlaylists = soundCloudContent,
                youtubeVideos = youtubeContent
            )
        }
    }

    fun onTabSelected(index: Int) {
        if (index != _uiState.value.selectedTab) {
            _uiState.update { it.copy(selectedTab = index) }
        }
    }
}