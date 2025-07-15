package com.cebolarekords.player.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cebolarekords.player.ui.about.SobreScreen
import com.cebolarekords.player.ui.artists.ArtistsScreen
import com.cebolarekords.player.ui.home.HomeScreen
import com.cebolarekords.player.ui.music.MusicScreen
import com.cebolarekords.player.ui.streaming.StreamingScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = AppNavigation.Home.route,
        modifier = modifier.fillMaxSize(),
    ) {
        composable(AppNavigation.Home.route) {
            HomeScreen()
        }
        composable(AppNavigation.Artists.route) {
            ArtistsScreen(viewModel = hiltViewModel())
        }
        composable(AppNavigation.Music.route) {
            MusicScreen(
                viewModel = hiltViewModel()
            )
        }
        composable(AppNavigation.Streaming.route) {
            StreamingScreen(viewModel = hiltViewModel())
        }
        composable(AppNavigation.About.route) {
            SobreScreen()
        }
    }
}