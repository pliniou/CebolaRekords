package com.cebolarekords.player.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppNavigation(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : AppNavigation("home", "Início", Icons.Filled.Home)
    data object Artists : AppNavigation("artists", "Artistas", Icons.Filled.Person)
    data object Music : AppNavigation("music", "Músicas", Icons.Filled.MusicNote)
    data object About : AppNavigation("about", "Sobre", Icons.Filled.Info)
    companion object {
        val bottomNavItems = listOf(Home, Artists, Music, About)
    }
}