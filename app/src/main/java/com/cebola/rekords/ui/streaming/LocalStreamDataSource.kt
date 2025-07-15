package com.cebolarekords.player.ui.streaming

import com.cebolarekords.player.data.StreamContent
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

    fun getYoutubeVideos(): List<StreamContent> {
        return listOf(
            StreamContent("v9pjhzwLscY", "Don't Look Any Further (Remix)", "https://www.youtube.com/embed/v9pjhzwLscY"),
            StreamContent("kX03w9BkWI4", "Preludio (Extended Mix)", "https://www.youtube.com/embed/kX03w9BkWI4"),
            StreamContent("sRXmSWsX-9U", "Live on Radio Pulse", "https://www.youtube.com/embed/sRXmSWsX-9U"),
            StreamContent("Ee_H6K0SO9g", "Live At Home", "https://www.youtube.com/embed/Ee_H6K0SO9g"),
            StreamContent("puRkGVxJ_4g", "Aufmerksamkeit", "https://www.youtube.com/embed/puRkGVxJ_4g"),
            StreamContent("aOkUTyJUgEQ", "Atenção Passageiros", "https://www.youtube.com/embed/aOkUTyJUgEQ"),
            StreamContent("tXlPFS2B-p0", "Masterpiece in 128", "https://www.youtube.com/embed/tXlPFS2B-p0"),
            StreamContent("p3Qhuq69a4M", "Let's Get It On (Remix)", "https://www.youtube.com/embed/p3Qhuq69a4M"),
            StreamContent("7zfxM5A758E", "My House (Remix)", "https://www.youtube.com/embed/7zfxM5A758E"),
            StreamContent("L5oXhW6Xm_w", "Fever Night (Original Mix)", "https://www.youtube.com/embed/L5oXhW6Xm_w"),
            StreamContent("3h3_1cQ2f8I", "122 Grooves", "https://www.youtube.com/embed/3h3_1cQ2f8I"),
        )
    }
}