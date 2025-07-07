package com.cebolarekords.player.ui.artists

import androidx.lifecycle.ViewModel
import com.cebolarekords.player.data.Artist
import com.cebolarekords.player.domain.usecase.GetArtistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ArtistsUiState(
    val artists: List<Artist> = emptyList()
)

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val getArtistsUseCase: GetArtistsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArtistsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadArtists()
    }

    private fun loadArtists() {
        _uiState.value = ArtistsUiState(artists = getArtistsUseCase())
    }
}