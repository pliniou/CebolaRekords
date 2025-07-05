package com.cebolarekords.player.ui.music

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebolarekords.player.R
import com.cebolarekords.player.data.Track
import com.cebolarekords.player.ui.components.AnimatedListItem
import kotlinx.coroutines.delay

@Composable
fun MusicScreen(
    // ALTERADO: O mediaController foi removido da assinatura. O viewModel é obtido via Hilt.
    viewModel: MusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // Este estado agora é apenas para UI e não tem o controller real
    var currentMediaId by remember { mutableStateOf<String?>(null) }
    var isTitleVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isTitleVisible = true
    }

    // Este DisposableEffect ainda é útil para ouvir mudanças, mas depende de como o estado é exposto.
    // Para simplificar, vamos nos basear no estado do ViewModel.
    // A lógica de ouvir o player real agora está encapsulada no PlayerViewModel.

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        if (uiState.isLoading) {
            LoadingContent()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CatalogHeader(visible = isTitleVisible)
                }
                itemsIndexed(
                    items = uiState.tracks,
                    key = { _, track -> track.id }
                ) { index, track ->
                    AnimatedListItem(delay = (index * 40L) + 250L) {
                        TrackItem(
                            track = track,
                            // A lógica de 'isPlaying' será simplificada aqui. O estado real vem do PlayerViewModel.
                            // Para o destaque visual, precisaremos de uma abordagem diferente ou aceitar uma pequena latência.
                            // Por ora, a lógica de clique está correta.
                            isPlaying = false, // O destaque visual correto virá do PlayerViewModel globalmente
                            onTrackClick = {
                                // ALTERADO: A chamada agora passa apenas a track, como esperado pelo ViewModel
                                viewModel.onTrackClick(it)
                            }
                        )
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}


@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "Carregando música...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun CatalogHeader(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it / 3 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(animationSpec = tween(600))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Catálogo Musical",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Descubra nossa coleção",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    isPlaying: Boolean,
    onTrackClick: (Track) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    val elevation by animateDpAsState(
        targetValue = when {
            isPlaying -> 8.dp
            isPressed -> 6.dp
            else -> 2.dp
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "elevation"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isPlaying) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = tween(250),
        label = "containerColor"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onTrackClick(track) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column {
            TrackArtwork(
                track = track,
                isPlaying = isPlaying
            )
            TrackInfo(
                track = track,
                isPlaying = isPlaying
            )
        }
    }
}

@Composable
private fun TrackArtwork(
    track: Track,
    isPlaying: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(track.artworkData)
                .crossfade(300)
                .build(),
            contentDescription = "Capa do álbum ${track.title}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_cebolarekords_album_art),
            error = painterResource(R.drawable.ic_cebolarekords_album_art)
        )
        if (isPlaying) {
            PlayingIndicator(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun PlayingIndicator(
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.size(28.dp),
        shadowElevation = 2.dp
    ) {
        Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = "Reproduzindo",
            tint = Color.White,
            modifier = Modifier.padding(6.dp)
        )
    }
}

@Composable
private fun TrackInfo(
    track: Track,
    isPlaying: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = track.title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = if (isPlaying) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
        Text(
            text = track.artistName,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}