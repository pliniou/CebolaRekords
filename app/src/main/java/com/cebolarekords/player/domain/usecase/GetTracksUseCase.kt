package com.cebolarekords.player.domain.usecase

import com.cebolarekords.player.data.Track
import com.cebolarekords.player.data.CebolaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val repository: CebolaRepository
) {
    suspend operator fun invoke(): List<Track> {
        return repository.getAllTracks()
    }
}