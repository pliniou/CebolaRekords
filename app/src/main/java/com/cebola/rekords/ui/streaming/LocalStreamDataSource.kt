package com.cebola.rekords.ui.streaming

import com.cebola.rekords.data.StreamContent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStreamDataSource @Inject constructor() {

    fun getSoundCloudPlaylists(): List<StreamContent> {
        return listOf(
            StreamContent(
                id = "1989023692",
                title = "Cebola Classics / Tape B",
                embedUrl = "https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/playlists/1989023692&color=%23ff5500&auto_play=false&hide_related=true&show_comments=false&show_user=true&show_reposts=false&show_teaser=true"
            ),
            StreamContent(
                id = "1989015652",
                title = "Cebola Classics / Tape A",
                embedUrl = "https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/playlists/1989015652&color=%23ff5500&auto_play=false&hide_related=true&show_comments=false&show_user=true&show_reposts=false&show_teaser=true"
            )
        )
    }

    // CORREÇÃO: Lista de vídeos do YouTube completamente atualizada com os novos links e títulos.
    fun getYoutubeVideos(): List<StreamContent> {
        return listOf(
            // EMBEDDED SETS
            StreamContent("f7IPODbQ5K4", "CEBOLA Live on Radio Pulse (NY)", "https://www.youtube.com/embed/f7IPODbQ5K4"),
            StreamContent("l5gN35O7BkM", "CEBOLA Live at Home", "https://www.youtube.com/embed/l5gN35O7BkM"),
            StreamContent("Y9kQ6dILUW0", "Masterpiece in 128", "https://www.youtube.com/embed/Y9kQ6dILUW0"),
            StreamContent("Ee_H6K0SO9g", "Atenção Passageiros - Alex LPunkt | Pliniou (Remix)", "https://www.youtube.com/embed/Ee_H6K0SO9g"),
            StreamContent("LIslItn0RAQ", "A LITTLE MORE THAN AN HOUR OF GOOD ELECTRONIC MUSIC", "https://www.youtube.com/embed/LIslItn0RAQ"),
            StreamContent("zDd0cw72qSY", "122 Grooves", "https://www.youtube.com/embed/zDd0cw72qSY"),

            // SINGLES
            StreamContent("kX03w9BkWI4", "Pliniou - Preludio (Extended Mix)", "https://www.youtube.com/embed/kX03w9BkWI4"),
            StreamContent("sRXmSWsX-9U", "Pliniou - Aufmerksamkeit", "https://www.youtube.com/embed/sRXmSWsX-9U"),
            StreamContent("puRkGVxJ_4g", "The Cemetery Girls - Lala Lala Song (2018) / Pliniou (Re-Edit)", "https://www.youtube.com/embed/puRkGVxJ_4g"),
            StreamContent("aOkUTyJUgEQ", "Pliniou - Compass", "https://www.youtube.com/embed/aOkUTyJUgEQ"),
            StreamContent("mhB3hwHSgFk", "Pliniou - Fever Night (Original Mix)", "https://www.youtube.com/embed/mhB3hwHSgFk"),
            StreamContent("emSuWzjsXQ4", "Pliniou - Movelement (Original Mix)", "https://www.youtube.com/embed/emSuWzjsXQ4"),
            StreamContent("L5MNstDRWDw", "Pliniou - Where's the Place?", "https://www.youtube.com/embed/L5MNstDRWDw"),
            StreamContent("XY_C0SBuDoo", "Pliniou - Sounds of Cerrado (Original Mix)", "https://www.youtube.com/embed/XY_C0SBuDoo"),
            StreamContent("E3NcmO4OrKY", "Pliniou - Euphoric", "https://www.youtube.com/embed/E3NcmO4OrKY"),
            StreamContent("UBgokDJ2jtI", "Pliniou - Deep Space (Re-Edit)", "https://www.youtube.com/embed/UBgokDJ2jtI"),
            StreamContent("oWxlOFRzgPU", "Pliniou & Renato Uhm - Epic Journey (Re-Edit)", "https://www.youtube.com/embed/oWxlOFRzgPU"),

            // REMIXES
            StreamContent("v9pjhzwLscY", "Dennis Edwards - Don't Look Any Further (Remix)", "https://www.youtube.com/embed/v9pjhzwLscY"),
            StreamContent("8JIXC5ofiNo", "Marvin Gaye - Let's Get It On (1973) / Pliniou (Re-Edit)", "https://www.youtube.com/embed/8JIXC5ofiNo"),
            StreamContent("UMttrSs5u-I", "Rhythm Controll - My House (1987) / Pliniou (Re-Edit)", "https://www.youtube.com/embed/UMttrSs5u-I")
        )
    }
}