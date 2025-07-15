package com.cebola.rekords.ui.music

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebola.rekords.R
import com.cebola.rekords.data.Track
import com.cebola.rekords.ui.components.shimmerEffect
import com.cebola.rekords.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(
    viewModel: MusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredTracks by viewModel.filteredTracks.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    val isEmpty by remember {
        derivedStateOf { filteredTracks.isEmpty() && !uiState.isLoading }
    }

    Column(
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
            .semantics {
                contentDescription = "Tela de música com ${filteredTracks.size} faixas"
            }
    ) {
        ScreenHeader(
            viewType = uiState.viewType,
            onToggleViewType = viewModel::onToggleViewType,
            searchQuery = uiState.searchQuery,
            onSearchQueryChanged = viewModel::onSearchQueryChanged
        )

        if (uiState.isLoading) {
            LoadingState(uiState.viewType)
        } else if (isEmpty) {
            EmptyState(
                searchQuery = uiState.searchQuery,
                onRetry = viewModel::retry
            )
        } else {
            TrackContent(
                tracks = filteredTracks,
                viewType = uiState.viewType,
                onTrackClick = viewModel::onTrackClick,
                gridState = gridState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenHeader(
    viewType: ViewType,
    onToggleViewType: () -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    var showSearch by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showSearch) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Biblioteca Musical",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Todas as faixas da Cebola Rekords",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Row {
                IconButton(
                    onClick = { showSearch = !showSearch },
                    modifier = Modifier.semantics {
                        contentDescription = if (showSearch) "Fechar busca" else "Abrir busca"
                    }
                ) {
                    Icon(
                        imageVector = if (showSearch) Icons.Default.Clear else Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onToggleViewType,
                    modifier = Modifier.semantics {
                        contentDescription = "Alternar para visualização em ${if (viewType == ViewType.GRID) "lista" else "grade"}"
                    }
                ) {
                    Icon(
                        imageVector = if (viewType == ViewType.GRID) Icons.AutoMirrored.Filled.List else Icons.Filled.GridView,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showSearch,
            enter = slideInVertically { -it } + fadeIn(),
            exit = fadeOut()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Buscar músicas, artistas ou álbuns") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
    }
}

@Composable
private fun TrackContent(
    tracks: List<Track>,
    viewType: ViewType,
    onTrackClick: (Track) -> Unit,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState
) {
    LazyVerticalGrid(
        state = gridState,
        columns = if (viewType == ViewType.GRID) GridCells.Fixed(2) else GridCells.Fixed(1),
        contentPadding = PaddingValues(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            bottom = Dimens.PaddingLarge + 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        items(
            items = tracks,
            key = { track -> track.id }
        ) { track ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(animationSpec = tween(500), initialOffsetY = { it / 3 }),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                if (viewType == ViewType.GRID) {
                    TrackGridItem(
                        track = track,
                        onTrackClick = { onTrackClick(track) }
                    )
                } else {
                    TrackListItem(
                        track = track,
                        onTrackClick = { onTrackClick(track) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackGridItem(
    track: Track,
    onTrackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable(onClick = onTrackClick)
            .semantics {
                contentDescription = "Tocar ${track.title} de ${track.artistName}"
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(track.artworkData)
                    .crossfade(true)
                    .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                    .build(),
                placeholder = painterResource(R.drawable.ic_cebolarekords_album_art),
                error = painterResource(R.drawable.ic_cebolarekords_album_art),
                contentDescription = "Capa do álbum ${track.albumName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Column(
                modifier = Modifier.padding(Dimens.PaddingMedium)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackListItem(
    track: Track,
    onTrackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTrackClick)
            .semantics {
                contentDescription = "Tocar ${track.title} de ${track.artistName}"
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(Dimens.PaddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(track.artworkData)
                    .crossfade(true)
                    .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                    .build(),
                placeholder = painterResource(R.drawable.ic_cebolarekords_album_art),
                error = painterResource(R.drawable.ic_cebolarekords_album_art),
                contentDescription = "Capa do álbum ${track.albumName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
            )

            Spacer(modifier = Modifier.width(Dimens.PaddingMedium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    searchQuery: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (searchQuery.isNotEmpty()) {
                    "Nenhuma música encontrada para \"$searchQuery\""
                } else {
                    "Nenhuma música disponível"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            if (searchQuery.isEmpty()) {
                androidx.compose.material3.TextButton(
                    onClick = onRetry
                ) {
                    Text("Tentar novamente")
                }
            }
        }
    }
}

@Composable
private fun LoadingState(viewType: ViewType) {
    LazyVerticalGrid(
        columns = if (viewType == ViewType.GRID) GridCells.Fixed(2) else GridCells.Fixed(1),
        contentPadding = PaddingValues(Dimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        items(10) {
            if (viewType == ViewType.GRID) {
                GridItemPlaceholder()
            } else {
                ListItemPlaceholder()
            }
        }
    }
}

@Composable
private fun GridItemPlaceholder() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(20.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(Dimens.PaddingExtraSmall))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(16.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect()
        )
    }
}

@Composable
private fun ListItemPlaceholder() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect()
            )
        }
    }
}