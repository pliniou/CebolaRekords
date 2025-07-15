package com.cebola.rekords

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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cebola.rekords.navigation.AppNavHost
import com.cebola.rekords.navigation.AppNavigation
import com.cebola.rekords.playback.PlayerViewModel
import com.cebola.rekords.ui.components.AppBottomNavigation
import com.cebola.rekords.ui.components.MiniPlayer
import com.cebola.rekords.ui.music.MusicViewModel
import com.cebola.rekords.ui.player.FullPlayerScreen
import com.cebola.rekords.ui.theme.CebolaRekordsTheme
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

private val bottomBarRoutes = AppNavigation.bottomNavItems.map { it.route }.toSet()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    musicViewModel: MusicViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()
    val musicState by musicViewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val fullPlayerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFullPlayerSheet by rememberSaveable { mutableStateOf(false) }
    val showBottomBar = currentRoute in bottomBarRoutes
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(currentRoute) {
        if (currentRoute == AppNavigation.Streaming.route && playerState.isPlaying) {
            playerViewModel.onPlayPauseClick()
        }
    }

    LaunchedEffect(fullPlayerSheetState.isVisible) {
        if (!fullPlayerSheetState.isVisible) {
            showFullPlayerSheet = false
        }
    }

    LaunchedEffect(musicState.error) {
        musicState.error?.let { errorMsg ->
            snackbarHostState.showSnackbar(
                message = errorMsg,
                withDismissAction = true
            )
            musicViewModel.errorShown()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                contentDescription = context.getString(R.string.app_name)
            },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar && !fullPlayerSheetState.isVisible,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                Column {
                    AnimatedVisibility(
                        visible = playerState.currentTrack != null &&
                                currentRoute != AppNavigation.Streaming.route,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        MiniPlayer(
                            track = playerState.currentTrack,
                            isPlaying = playerState.isPlaying,
                            progress = if (playerState.duration > 0) playerState.currentPosition.toFloat() / playerState.duration else 0f,
                            onPlayPauseClick = playerViewModel::onPlayPauseClick,
                            onPlayerClick = { showFullPlayerSheet = true }
                        )
                    }
                    AppBottomNavigation(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
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
                onNavigateUp = {
                    scope.launch {
                        fullPlayerSheetState.hide()
                    }
                },
                onPlayPauseClick = playerViewModel::onPlayPauseClick,
                onSkipNextClick = playerViewModel::onSkipNextClick,
                onSkipPreviousClick = playerViewModel::onSkipPreviousClick,
                onSeek = playerViewModel::onSeek,
                onShuffleToggle = playerViewModel::onShuffleToggle,
                onRepeatToggle = playerViewModel::onRepeatModeToggle
            )
        }
    }
}