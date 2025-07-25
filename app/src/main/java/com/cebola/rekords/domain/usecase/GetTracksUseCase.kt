package com.cebola.rekords.domain.usecase

import com.cebola.rekords.data.CebolaRepository
import com.cebola.rekords.data.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val repository: CebolaRepository
) {
    /**
     * * REFINAMENTO: A função invoke agora retorna um Flow<List<Track>> diretamente do repositório,
     * mantendo a cadeia de reatividade.
     * Isso permite que a UI observe as atualizações
     * de dados de forma contínua e eficiente, desde o banco de dados até a tela.
     */
    operator fun invoke(): Flow<List<Track>> {
        return repository.getAllTracks()
    }
}