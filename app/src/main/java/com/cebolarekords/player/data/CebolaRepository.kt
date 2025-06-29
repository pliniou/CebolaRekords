package com.cebolarekords.player.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.net.toUri
import com.cebolarekords.player.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CebolaRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getArtists(): List<Artist> {
        return listOf(
            Artist(1, "Pliniou", "Produtor e DJ de Brasília, conhecido por suas texturas profundas e batidas envolventes que exploram as vertentes do House e Techno. Com uma carreira consolidada na cena local, Pliniou é um dos pilares da Cebola Rekords, trazendo inovação e energia para a pista de dança.", R.drawable.foto_plinio),
            Artist(2, "Renato Uhm", "Com uma sonoridade que viaja pelo Deep House e Minimal, Renato Uhm cria atmosferas hipnóticas e paisagens sonoras imersivas. Seu trabalho é caracterizado pela atenção meticulosa aos detalhes e pela construção de narrativas sonoras que cativam o ouvinte do início ao fim.", R.drawable.foto_renato)
        )
    }

    // CORRIGIDO: A função agora é 'suspend' para rodar em background e não bloquear a Main Thread.
    suspend fun getAllTracks(): List<Track> = withContext(Dispatchers.IO) {
        val albumCoverUri = "android.resource://${context.packageName}/${R.drawable.ic_cebolarekords_album_art}".toUri()
        val defaultAlbumArt = getResourceAsByteArray(R.drawable.ic_cebolarekords_album_art)

        val baseTrackList = listOf(
            Track(1, "Aufmerksamkeit", "Pliniou", "Cebola Classics #01", null, R.raw.aufmerksamkeit),
            Track(2, "Capivara Walk", "Pliniou", "Cebola Classics #01", null, R.raw.capivara_walk),
            Track(3, "Cerrado Sounds", "Renato Uhm", "Cebola Classics #01", null, R.raw.cerrado_sounds),
            Track(4, "Compass", "Pliniou", "Cebola Classics #01", null, R.raw.compass),
            Track(5, "Deep Space", "Pliniou", "Cebola Classics #01", null, R.raw.deep_space),
            Track(6, "Endless Nightmare", "Pliniou", "Cebola Classics #01", null, R.raw.endless_nightmare),
            Track(7, "Epicurean (Club Mix)", "Pliniou", "Cebola Classics #01", null, R.raw.epicurean_club_mix),
            Track(8, "Epicurean (Extended Mix)", "Pliniou", "Cebola Classics #01", null, R.raw.epicurean_extended_mix),
            Track(9, "Euphorically", "Pliniou", "Cebola Classics #01", null, R.raw.euphorically),
            Track(10, "Exordio", "Pliniou", "Cebola Classics #01", null, R.raw.exordio),
            Track(11, "Interference (Club Mix)", "Pliniou", "Cebola Classics #01", null, R.raw.interference_club_mix),
            Track(12, "Interference (Extended Mix)", "Pliniou", "Cebola Classics #01", null, R.raw.interference_extended_mix),
            Track(13, "Let's Get It Dark", "Pliniou", "Cebola Classics #01", null, R.raw.lets_get_it_dark),
            Track(14, "Marvin Gaye - Let's Get It On (Remix)", "Pliniou", "Cebola Classics #01", null, R.raw.lets_get_it_on),
            Track(15, "Mand Woolf", "Pliniou", "Cebola Classics #01", null, R.raw.mand_woolf),
            Track(16, "Movelement", "Pliniou", "Cebola Classics #01", null, R.raw.movelement),
            Track(17, "My House - Rhythm Control (Remix)", "Pliniou", "Cebola Classics #01", null, R.raw.my_house),
            Track(18, "Preludio (Club Mix)", "Pliniou, Renato Uhm", "Cebola Classics #01", null, R.raw.preludio_club_mix),
            Track(19, "Preludio (Extended Mix)", "Pliniou, Renato Uhm", "Cebola Classics #01", null, R.raw.preludio_extended_mix),
            Track(20, "Sucuarana Mode", "Pliniou", "Cebola Classics #01", null, R.raw.sucuarana_mode),
            Track(21, "Where's the Place", "Pliniou", "Cebola Classics #01", null, R.raw.wheres_the_place),
            Track(22, "Your House", "Pliniou", "Cebola Classics #01", null, R.raw.your_house),
            Track(23, "122 Grooves", "Pliniou", "Cebola Classics #01", null, R.raw.grooves),
            Track(24, "CEBOLA Live at Home", "Pliniou", "Cebola Live", null, R.raw.cebola_live_at_home),
            Track(25, "Masterpiece in 128", "Pliniou", "Cebola Classics #01", null, R.raw.masterpiece_in_128)
        )

        val retriever = MediaMetadataRetriever()
        try {
            val tracksWithMetadata = baseTrackList.map { track ->
                val audioUri = "android.resource://${context.packageName}/${track.audioFile}".toUri()
                var artworkData: ByteArray? = null
                try {
                    retriever.setDataSource(context, audioUri)
                    artworkData = retriever.embeddedPicture
                } catch (e: Exception) {
                    Log.e("CebolaRepository", "Error getting metadata for track ${track.title} (${track.id}): ${e.message}")
                }

                track.copy(
                    audioUri = audioUri,
                    artworkUri = albumCoverUri, // Mantém uma URI padrão para o Coil, mas usa os dados abaixo para o player.
                    artworkData = artworkData ?: defaultAlbumArt // Usa a arte extraída, ou a padrão se não houver.
                )
            }
            // Retorna a lista ordenada por título
            tracksWithMetadata.sortedBy { it.title }
        } finally {
            // Garante que o retriever seja liberado mesmo em caso de erro.
            retriever.release()
        }
    }

    private fun getResourceAsByteArray(resourceId: Int): ByteArray {
        return context.resources.openRawResource(resourceId).use { it.readBytes() }
    }
}