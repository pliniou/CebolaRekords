package com.cebolarekords.player.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.annotation.RawRes
import androidx.core.net.toUri
import com.cebolarekords.player.R
import com.cebolarekords.player.data.database.TrackDao
import com.cebolarekords.player.data.database.TrackEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CebolaRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackDao: TrackDao
) {
    // Cache para arte da capa, permanece útil para a primeira busca.
    private val artworkCache = mutableMapOf<Int, ByteArray?>()

    fun getArtists(): List<Artist> {
        return listOf(
            Artist(1, "Pliniou", "Produtor e DJ de Brasília, conhecido por suas texturas profundas e batidas envolventes que exploram as vertentes do House e Techno. Com uma carreira consolidada na cena local, Pliniou é um dos pilares da Cebola Rekords, trazendo inovação e energia para a pista de dança.", R.drawable.foto_plinio),
            Artist(2, "Renato Uhm", "Com uma sonoridade que viaja pelo Deep House e Minimal, Renato Uhm cria atmosferas hipnóticas e paisagens sonoras imersivas. Seu trabalho é caracterizado pela atenção meticulosa aos detalhes e pela construção de narrativas sonoras que cativam o ouvinte do início ao fim.", R.drawable.foto_renato)
        )
    }

    suspend fun getAllTracks(): List<Track> = withContext(Dispatchers.IO) {
        val trackCountInDb = trackDao.getTrackCount()

        if (trackCountInDb == 0) {
            Log.d("CebolaRepository", "Banco de dados vazio. Populando com dados locais...")
            val localTracks = fetchTracksFromLocalResources()
            trackDao.insertAll(localTracks)
        }

        val entitiesFromDb = trackDao.getAllTracks()
        return@withContext entitiesFromDb.map { entity ->
            val uri = "android.resource://${context.packageName}/${entity.audioFileResId}".toUri()
            entity.toDomainModel(uri)
        }
    }

    private fun fetchTracksFromLocalResources(): List<TrackEntity> {
        Log.d("CebolaRepository", "Buscando e processando faixas dos recursos raw.")
        val defaultAlbumArt = getResourceAsByteArray(R.drawable.ic_cebolarekords_album_art)

        val trackDefinitions = getTrackDefinitions()
        return trackDefinitions.mapIndexed { index, (title, artist, resourceKey) ->
            val resId = trackResourceMap[resourceKey] ?: R.raw.aufmerksamkeit // Fallback seguro, mas não deve acontecer
            val trackId = index + 1
            val artworkData = artworkCache.getOrPut(trackId) { extractArtwork(resId) }

            TrackEntity(
                id = trackId,
                title = title,
                artistName = artist,
                albumName = if (title == "CEBOLA Live at Home") "Cebola Live" else "Cebola Classics #01",
                audioFileResId = resId,
                artworkData = artworkData ?: defaultAlbumArt
            )
        }
    }

    // =========================================================================
    // FUNÇÕES AUXILIARES QUE FALTAVAM (A CAUSA DOS ERROS)
    // =========================================================================

    private fun extractArtwork(@RawRes audioRes: Int): ByteArray? {
        return try {
            MediaMetadataRetriever().use { retriever ->
                val uri = "android.resource://${context.packageName}/$audioRes".toUri()
                retriever.setDataSource(context, uri)
                retriever.embeddedPicture
            }
        } catch (e: Exception) {
            Log.e("CebolaRepository", "Falha ao extrair arte da faixa: $audioRes", e)
            null
        }
    }

    private fun getResourceAsByteArray(resourceId: Int): ByteArray {
        return context.resources.openRawResource(resourceId).use { it.readBytes() }
    }

    private val trackResourceMap = mapOf(
        "aufmerksamkeit" to R.raw.aufmerksamkeit,
        "capivara_walk" to R.raw.capivara_walk,
        "cerrado_sounds" to R.raw.cerrado_sounds,
        "compass" to R.raw.compass,
        "deep_space" to R.raw.deep_space,
        "endless_nightmare" to R.raw.endless_nightmare,
        "epicurean_club_mix" to R.raw.epicurean_club_mix,
        "epicurean_extended_mix" to R.raw.epicurean_extended_mix,
        "euphorically" to R.raw.euphorically,
        "exordio" to R.raw.exordio,
        "interference_club_mix" to R.raw.interference_club_mix,
        "interference_extended_mix" to R.raw.interference_extended_mix,
        "lets_get_it_dark" to R.raw.lets_get_it_dark,
        "lets_get_it_on" to R.raw.lets_get_it_on,
        "mand_woolf" to R.raw.mand_woolf,
        "movelement" to R.raw.movelement,
        "my_house" to R.raw.my_house,
        "preludio_club_mix" to R.raw.preludio_club_mix,
        "preludio_extended_mix" to R.raw.preludio_extended_mix,
        "sucuarana_mode" to R.raw.sucuarana_mode,
        "wheres_the_place" to R.raw.wheres_the_place,
        "your_house" to R.raw.your_house,
        "grooves" to R.raw.grooves,
        "cebola_live_at_home" to R.raw.cebola_live_at_home,
        "masterpiece_in_128" to R.raw.masterpiece_in_128
    )

    // CORRIGIDO: Função agora retorna uma lista tipada corretamente
    private fun getTrackDefinitions(): List<Triple<String, String, String>> {
        return listOf(
            Triple("Aufmerksamkeit", "Pliniou", "aufmerksamkeit"),
            Triple("Capivara Walk", "Pliniou", "capivara_walk"),
            Triple("Cerrado Sounds", "Renato Uhm", "cerrado_sounds"),
            Triple("Compass", "Pliniou", "compass"),
            Triple("Deep Space", "Pliniou", "deep_space"),
            Triple("Endless Nightmare", "Pliniou", "endless_nightmare"),
            Triple("Epicurean (Club Mix)", "Pliniou", "epicurean_club_mix"),
            Triple("Epicurean (Extended Mix)", "Pliniou", "epicurean_extended_mix"),
            Triple("Euphorically", "Pliniou", "euphorically"),
            Triple("Exordio", "Pliniou", "exordio"),
            Triple("Interference (Club Mix)", "Pliniou", "interference_club_mix"),
            Triple("Interference (Extended Mix)", "Pliniou", "interference_extended_mix"),
            Triple("Let's Get It Dark", "Pliniou", "lets_get_it_dark"),
            Triple("Marvin Gaye - Let's Get It On (Remix)", "Pliniou", "lets_get_it_on"),
            Triple("Mand Woolf", "Pliniou", "mand_woolf"),
            Triple("Movelement", "Pliniou", "movelement"),
            Triple("My House - Rhythm Control (Remix)", "Pliniou", "my_house"),
            Triple("Preludio (Club Mix)", "Pliniou, Renato Uhm", "preludio_club_mix"),
            Triple("Preludio (Extended Mix)", "Pliniou, Renato Uhm", "preludio_extended_mix"),
            Triple("Sucuarana Mode", "Pliniou", "sucuarana_mode"),
            Triple("Where's the Place", "Pliniou", "wheres_the_place"),
            Triple("Your House", "Pliniou", "your_house"),
            Triple("122 Grooves", "Pliniou", "grooves"),
            Triple("CEBOLA Live at Home", "Pliniou", "cebola_live_at_home"),
            Triple("Masterpiece in 128", "Pliniou", "masterpiece_in_128")
        )
    }
}