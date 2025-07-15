package com.cebola.rekords.domain.usecase

import com.cebola.rekords.data.Artist
import com.cebola.rekords.data.CebolaRepository
import javax.inject.Inject

class GetArtistsUseCase @Inject constructor(
    private val repository: CebolaRepository
) {
    suspend operator fun invoke(): List<Artist> {
        return repository.getArtists()
    }
}