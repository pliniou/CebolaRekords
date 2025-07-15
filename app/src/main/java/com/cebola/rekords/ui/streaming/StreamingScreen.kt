package com.cebola.rekords.ui.streaming

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebola.rekords.R
import com.cebola.rekords.data.StreamContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamingScreen(
    viewModel: StreamingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabTitles = listOf("SoundCloud", "YouTube")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showPlayer = uiState.selectedStreamUrl != null
    if (showPlayer) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onPlayerDismiss() },
            sheetState = sheetState,
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            uiState.selectedStreamUrl?.let { url ->
                WebContentPlayer(
                    embedUrl = url,
                    modifier = Modifier.fillMaxSize().padding(top = 16.dp)
                )
            }
        }
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
    ) {
        ScreenHeader()
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.onTabSelected(index) },
                    text = { Text(text = title) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        AnimatedContent(
            targetState = uiState.selectedTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "TabContentAnimation"
        ) { targetTab ->
            when (targetTab) {
                0 -> SoundCloudList(
                    content = uiState.soundCloudPlaylists,
                    onItemClick = viewModel::onStreamClick
                )
                1 -> YouTubeList(
                    content = uiState.youtubeVideos,
                    onItemClick = viewModel::onStreamClick
                )
            }
        }
    }
}

@Composable
private fun ScreenHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 16.dp),
    ) {
        Text(
            text = "Streaming",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Playlists e vídeos online",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// CORREÇÃO: Lista leve para o SoundCloud
@Composable
private fun SoundCloudList(
    content: List<StreamContent>,
    onItemClick: (StreamContent) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(content, key = { it.id }) { item ->
            SoundCloudItem(item = item, onClick = { onItemClick(item) })
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// CORREÇÃO: Lista leve para o YouTube
@Composable
private fun YouTubeList(
    content: List<StreamContent>,
    onItemClick: (StreamContent) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(content, key = { it.id }) { item ->
            YouTubeItem(item = item, onClick = { onItemClick(item) })
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// CORREÇÃO: Item leve para a lista do SoundCloud
@Composable
private fun SoundCloudItem(item: StreamContent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Playlist no SoundCloud",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_soundcloud),
                contentDescription = "SoundCloud",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// CORREÇÃO: Item leve para a lista do YouTube com thumbnail
@Composable
private fun YouTubeItem(item: StreamContent, onClick: () -> Unit) {
    // Helper para gerar URL da thumbnail a partir do ID do vídeo
    fun getYouTubeThumbnailUrl(videoId: String): String {
        return "https://img.youtube.com/vi/$videoId/0.jpg"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(getYouTubeThumbnailUrl(item.id))
                    .crossfade(true)
                    .build(),
                contentDescription = "Thumbnail do vídeo ${item.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 120.dp, height = 70.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Default.PlayCircleOutline,
                contentDescription = "Play Video",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp).padding(start = 8.dp)
            )
        }
    }
}