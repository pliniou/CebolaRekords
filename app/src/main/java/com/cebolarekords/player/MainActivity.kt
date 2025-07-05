// ARQUIVO ALTERADO: app/src/main/java/com/cebolarekords/player/MainActivity.kt
package com.cebolarekords.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cebolarekords.player.navigation.AppNavHost
import com.cebolarekords.player.navigation.AppNavigation
import com.cebolarekords.player.player.PlayerViewModel
import com.cebolarekords.player.ui.components.AppBottomNavigation
import com.cebolarekords.player.ui.components.MiniPlayer
import com.cebolarekords.player.ui.music.MusicViewModel
import com.cebolarekords.player.ui.player.FullPlayerScreen
import com.cebolarekords.player.ui.theme.CebolaRekordsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CebolaRekordsTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    musicViewModel: MusicViewModel = hiltViewModel() // Mantido para o Snackbar de erro
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val playerState by playerViewModel.uiState.collectAsState()
    val musicState by musicViewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val fullPlayerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFullPlayerSheet by remember { mutableStateOf(false) }

    val bottomBarRoutes = remember { AppNavigation.bottomNavItems.map { it.route }.toSet() }
    val showBottomBar = currentRoute in bottomBarRoutes
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(musicState.error) {
        musicState.error?.let { errorMsg ->
            snackbarHostState.showSnackbar(message = errorMsg)
            musicViewModel.errorShown()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar && !fullPlayerSheetState.isVisible,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                Column {
                    AnimatedVisibility(
                        visible = playerState.currentTrack != null,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        MiniPlayer(
                            track = playerState.currentTrack,
                            isPlaying = playerState.isPlaying,
                            onPlayPauseClick = playerViewModel::onPlayPauseClick,
                            onPlayerClick = {
                                scope.launch {
                                    showFullPlayerSheet = true
                                }
                            }
                        )
                    }
                    AppBottomNavigation(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // ALTERADO: A passagem do PlayerViewModel foi removida, pois não é mais necessária no AppNavHost
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showFullPlayerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFullPlayerSheet = false },
            sheetState = fullPlayerSheetState,
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            FullPlayerScreen(
                playerState = playerState,
                onNavigateUp = { scope.launch { fullPlayerSheetState.hide() }.invokeOnCompletion { if (!fullPlayerSheetState.isVisible) showFullPlayerSheet = false } },
                onPlayPauseClick = playerViewModel::onPlayPauseClick,
                onSkipNextClick = playerViewModel::onSkipNextClick,
                onSkipPreviousClick = playerViewModel::onSkipPreviousClick,
                onSeek = playerViewModel::onSeek
            )
        }
    }
}