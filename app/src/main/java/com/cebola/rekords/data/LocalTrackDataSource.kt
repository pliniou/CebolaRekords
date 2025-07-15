package com.cebolarekords.player.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.net.toUri
import com.cebolarekords.player.R
import com.cebolarekords.player.data.database.TrackEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados local para artistas e faixas.
 *
 * Esta classe lê os metadados diretamente dos arquivos de áudio
 * e mapeia os artistas para as faixas, servindo como a fonte inicial
 * para popular o banco de dados.
 */
@Singleton
class LocalTrackDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val artists = listOf(
        Artist(1, "Pliniou", "DJ e produtor de Brasília, fundador da Cebola Rekords. Sua sonoridade transita entre o House, Techno e ritmos brasileiros, criando uma identidade única na cena eletrônica nacional.", R.drawable.foto_plinio),
        Artist(2, "Renato Uhm", "Pianista e musicista experimental. Suas composições exploram texturas ambientais e melodias complexas, combinando elementos clássicos com sintetizadores modernos para criar paisagens sonoras imersivas.", R.drawable.foto_renato)
    )

    fun getArtists(): List<Artist> {
        return artists
    }

    /**
     * Extrai metadados de todos os arquivos de áudio na pasta res/raw.
     * O mapeamento de artistas para faixas é feito aqui.
     *
     * @return Uma lista de [TrackEntity] pronta para ser inserida no banco de dados.
     */
    suspend fun fetchTracksFromLocalResources(): List<TrackEntity> = withContext(Dispatchers.IO) {
        val rawResources = mapOf(
            R.raw.atencao_passageiros to artists[0],
            R.raw.aufmerksamkeit to artists[0],
            R.raw.deep_space to artists[0],
            R.raw.dont_look_any_further to artists[0],
            R.raw.endless_nightmare to artists[0],
            R.raw.epicurean_club_mix to artists[0],
            R.raw.epicurean_extended_mix to artists[0],
            R.raw.euphorically to artists[0],
            R.raw.exordio to artists[0],
            R.raw.grooves to artists[0],
            R.raw.interference_club_mix to artists[0],
            R.raw.interference_extended_mix to artists[0],
            R.raw.lala_lala_song to artists[0],
            R.raw.lets_get_it_dark to artists[0],
            R.raw.lets_get_it_on to artists[0],
            R.raw.live_on_radio_pulse to artists[0],
            R.raw.movelement to artists[0],
            R.raw.my_house to artists[0],
            R.raw.preludio_club_mix to artists[0],
            R.raw.preludio_extended_mix to artists[0],
            R.raw.wheres_the_place to artists[0],
            R.raw.your_house to artists[0],
            R.raw.capivara_walk to artists[1],
            R.raw.cerrado_sounds to artists[1],
            R.raw.compass to artists[1],
            R.raw.mand_wolf to artists[1],
            R.raw.sucuarana to artists[1]
        )

        val trackEntities = mutableListOf<TrackEntity>()
        var trackIdCounter = 1

        rawResources.forEach { (resId, artist) ->
            val retriever = MediaMetadataRetriever()
            try {
                val uri = "android.resource://${context.packageName}/$resId".toUri()
                retriever.setDataSource(context, uri)

                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Faixa $trackIdCounter"
                val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Cebola Singles"
                val artwork = retriever.embeddedPicture

                trackEntities.add(
                    TrackEntity(
                        id = trackIdCounter,
                        title = title,
                        artistName = artist.name,
                        albumName = album,
                        audioFileResId = resId,
                        artworkData = artwork
                    )
                )
                trackIdCounter++
            } catch (e: Exception) {
                // Em produção, use uma ferramenta de logging como Firebase Crashlytics.
                // Evite e.printStackTrace() pois apenas imprime no logcat de debug.
                Log.e("LocalTrackDataSource", "Falha ao processar o recurso de mídia: $resId", e)
            } finally {
                retriever.release()
            }
        }
        return@withContext trackEntities
    }
}