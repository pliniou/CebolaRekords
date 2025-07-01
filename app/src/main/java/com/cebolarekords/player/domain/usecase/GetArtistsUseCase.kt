package com.cebolarekords.player.domain.usecase

import com.cebolarekords.player.data.Artist
import com.cebolarekords.player.data.CebolaRepository
import javax.inject.Inject

class GetArtistsUseCase @Inject constructor(
    private val repository: CebolaRepository
) {
    // O operador invoke permite chamar a classe como se fosse uma função
    // Ex: getArtistsUseCase()
    operator fun invoke(): List<Artist> {
        return repository.getArtists()
    }
}