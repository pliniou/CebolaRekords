// ARQUIVO ALTERADO: app/src/main/java/com/cebolarekords/player/navigation/AppNavHost.kt
package com.cebolarekords.player.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
    // REMOVIDO: O PlayerViewModel não é mais necessário aqui
) {
    NavHost(
        navController = navController,
        startDestination = AppNavigation.Home.route,
        modifier = modifier.fillMaxSize(),
        enterTransition = { fadeIn(animationSpec = tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(400)) }
    ) {
        composable(AppNavigation.Home.route) {
            HomeScreen()
        }
        composable(
            route = AppNavigation.Artists.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) }
        ) {
            ArtistsScreen(viewModel = hiltViewModel())
        }
        composable(
            route = AppNavigation.Music.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) }
        ) {
            // ALTERADO: A MusicScreen agora obtém seu próprio ViewModel, que por sua vez obtém o MediaController.
            // O hiltViewModel() garante que a instância correta seja fornecida.
            MusicScreen(viewModel = hiltViewModel())
        }
        composable(
            route = AppNavigation.About.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) }
        ) {
            SobreScreen()
        }
    }
}