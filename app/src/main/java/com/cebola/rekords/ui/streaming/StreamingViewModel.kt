package com.cebola.rekords.ui.streaming

import androidx.lifecycle.ViewModel
import com.cebola.rekords.data.StreamContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class StreamingUiState(
    val selectedTab: Int = 0,
    val soundCloudPlaylists: List<StreamContent> = emptyList(),
    val youtubeVideos: List<StreamContent> = emptyList(),
    val selectedStreamUrl: String? = null
)

@HiltViewModel
class StreamingViewModel @Inject constructor(
    private val streamDataSource: LocalStreamDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow(StreamingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadContent()
    }

    private fun loadContent() {
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

    fun onStreamClick(streamContent: StreamContent) {
        _uiState.update { it.copy(selectedStreamUrl = streamContent.embedUrl) }
    }

    fun onPlayerDismiss() {
        _uiState.update { it.copy(selectedStreamUrl = null) }
    }
}