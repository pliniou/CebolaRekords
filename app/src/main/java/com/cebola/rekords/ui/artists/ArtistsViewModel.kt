package com.cebola.rekords.ui.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebola.rekords.data.Artist
import com.cebola.rekords.domain.usecase.GetArtistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            _uiState.value = ArtistsUiState(artists = getArtistsUseCase())
        }
    }
}