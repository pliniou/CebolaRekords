package com.cebola.rekords.ui.artists

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cebola.rekords.ui.components.AnimatedListItem
import com.cebola.rekords.ui.theme.Dimens
import com.cebola.rekords.ui.theme.PoppinsFamily
import kotlinx.coroutines.delay

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun ArtistsScreen(
    viewModel: ArtistsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val artists = uiState.artists
    val listState = rememberLazyListState()
    var isContentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(120)
        isContentVisible = true
    }
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(all = Dimens.PaddingLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge),
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        item {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(700, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(700))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.PaddingSmall),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)
                ) {
                    Text(
                        text = "Nossos Artistas",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Talentos da Cebola Rekords",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = PoppinsFamily,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                    )
                }
            }
        }
        itemsIndexed(
            items = artists,
            key = { _, artist -> artist.id }
        ) { index, artist ->
            val parallaxOffset = listState.firstVisibleItemScrollOffset * 0.15f
            AnimatedListItem(delay = (index * 60L)) {
                ArtistCard(
                    artist = artist,
                    modifier = Modifier.graphicsLayer {
                        translationY = if (listState.firstVisibleItemIndex == index) parallaxOffset else 0f
                    }
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}