package com.cebola.rekords.data

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.cebola.rekords.R
import com.cebola.rekords.data.database.TrackEntity
import com.cebola.rekords.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTrackDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    companion object {
        private const val TAG = "LocalTrackDataSource"
    }

    private val artists = listOf(
        Artist(
            id = 1,
            name = "Pliniou",
            description = "DJ e produtor de Brasília, fundador da Cebola Rekords. Sua sonoridade visionária transita entre o House, Techno e ritmos brasileiros, conectando a vanguarda da cena eletrônica nacional com o público global.",
            coverImage = R.drawable.foto_plinio
        ),
        Artist(
            id = 2,
            name = "Renato Uhm",
            description = "Pianista e musicista experimental. Suas composições exploram texturas ambientais e melodias complexas, combinando a profundidade do clássico com a inovação de sintetizadores modernos para criar paisagens sonoras imersivas e inesquecíveis.",
            coverImage = R.drawable.foto_renato
        )
    )

    fun getArtists(): List<Artist> {
        return artists
    }

    suspend fun fetchTracksFromLocalResources(): List<TrackEntity> = withContext(ioDispatcher) {
        val rawResources = createRawResourcesMap()
        val trackEntities = mutableListOf<TrackEntity>()
        var trackIdCounter = 1

        // CORREÇÃO: Carregar a imagem padrão uma única vez para reutilização.
        val defaultArtwork = loadDefaultArtwork()

        rawResources.forEach { (resId, artist) ->
            val retriever = MediaMetadataRetriever()
            try {
                val uri = "android.resource://${context.packageName}/$resId".toUri()
                retriever.setDataSource(context, uri)
                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ?: generateTitleFromResourceId(resId)
                val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                    ?: "Cebola Singles"

                // CORREÇÃO: Se a extração da arte falhar, usar a imagem padrão.
                val artwork = retriever.embeddedPicture ?: defaultArtwork

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
                Log.e(TAG, "Failed to process media resource: $resId", e)
            } finally {
                try {
                    retriever.release()
                } catch (_: Exception) {
                    // Ignorar
                }
            }
        }
        return@withContext trackEntities
    }

    // CORREÇÃO: Nova função para carregar a imagem padrão e convertê-la para ByteArray.
    private fun loadDefaultArtwork(): ByteArray? {
        return try {
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_cebolarekords_album_art)
            val bitmap = drawable?.toBitmap()
            if (bitmap != null) {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load default artwork.", e)
            null
        }
    }

    private fun createRawResourcesMap(): Map<Int, Artist> {
        return mapOf(
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
    }

    private fun generateTitleFromResourceId(resId: Int): String {
        return when (resId) {
            R.raw.atencao_passageiros -> "Atenção Passageiros"
            R.raw.aufmerksamkeit -> "Aufmerksamkeit"
            R.raw.deep_space -> "Deep Space"
            R.raw.dont_look_any_further -> "Don't Look Any Further"
            R.raw.endless_nightmare -> "Endless Nightmare"
            R.raw.epicurean_club_mix -> "Epicurean (Club Mix)"
            R.raw.epicurean_extended_mix -> "Epicurean (Extended Mix)"
            R.raw.euphorically -> "Euphorically"
            R.raw.exordio -> "Exórdio"
            R.raw.grooves -> "Grooves"
            R.raw.interference_club_mix -> "Interference (Club Mix)"
            R.raw.interference_extended_mix -> "Interference (Extended Mix)"
            R.raw.lala_lala_song -> "Lala Lala Song"
            R.raw.lets_get_it_dark -> "Let's Get It Dark"
            R.raw.lets_get_it_on -> "Let's Get It On"
            R.raw.live_on_radio_pulse -> "Live on Radio Pulse"
            R.raw.movelement -> "Movelement"
            R.raw.my_house -> "My House"
            R.raw.preludio_club_mix -> "Preludio (Club Mix)"
            R.raw.preludio_extended_mix -> "Preludio (Extended Mix)"
            R.raw.wheres_the_place -> "Where's The Place"
            R.raw.your_house -> "Your House"
            R.raw.capivara_walk -> "Capivara Walk"
            R.raw.cerrado_sounds -> "Cerrado Sounds"
            R.raw.compass -> "Compass"
            R.raw.mand_wolf -> "Mand Wolf"
            R.raw.sucuarana -> "Sucuarana"
            else -> "Faixa Desconhecida"
        }
    }
}