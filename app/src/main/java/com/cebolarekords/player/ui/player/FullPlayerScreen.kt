package com.cebolarekords.player.ui.player

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebolarekords.player.R
import com.cebolarekords.player.player.PlayerUiState
import com.cebolarekords.player.ui.theme.PoppinsFamily
import java.util.concurrent.TimeUnit

@Composable
fun FullPlayerScreen(
    playerState: PlayerUiState,
    onNavigateUp: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onSeek: (Long) -> Unit
) {
    val currentTrack = playerState.currentTrack
    val artworkData = currentTrack?.mediaMetadata?.artworkData

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BackgroundArtwork(artworkData = artworkData)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerTopBar(onNavigateUp = onNavigateUp)
            Spacer(modifier = Modifier.height(32.dp))
            AlbumArtWithAnimation(artworkData = artworkData)
            Spacer(modifier = Modifier.weight(1f))
            TrackInfoSection(
                title = currentTrack?.mediaMetadata?.title?.toString(),
                artist = currentTrack?.mediaMetadata?.artist?.toString()
            )
            Spacer(modifier = Modifier.height(32.dp))
            SeekBarWithTimer(
                duration = playerState.duration,
                currentPosition = playerState.currentPosition,
                onSeek = onSeek
            )
            Spacer(modifier = Modifier.height(24.dp))
            PlaybackControls(
                isPlaying = playerState.isPlaying,
                onPlayPauseClick = onPlayPauseClick,
                onSkipNextClick = onSkipNextClick,
                onSkipPreviousClick = onSkipPreviousClick,
                isEnabled = currentTrack != null
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BackgroundArtwork(artworkData: ByteArray?) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(artworkData)
            .allowHardware(false).crossfade(true).build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.ic_cebolarekords_album_art),
        error = painterResource(id = R.drawable.ic_cebolarekords_album_art),
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 1.0f))),
                    blendMode = BlendMode.Multiply
                )
            }
    )
}

@Composable
private fun PlayerTopBar(onNavigateUp: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onNavigateUp) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White.copy(alpha = 0.8f))
        }
        Box(
            modifier = Modifier.width(32.dp).height(4.dp).clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun AlbumArtWithAnimation(artworkData: ByteArray?) {
    Card(
        modifier = Modifier.fillMaxWidth(0.85f).aspectRatio(1f),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(16.dp)
    ) {
        AsyncImage(
            model = artworkData,
            contentDescription = "Capa do álbum",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_cebolarekords_album_art),
            error = painterResource(id = R.drawable.ic_cebolarekords_album_art),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TrackInfoSection(title: String?, artist: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title ?: "Nenhuma música",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = Color.White
        )
        Text(
            text = artist ?: "Selecione uma música",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SeekBarWithTimer(duration: Long, currentPosition: Long, onSeek: (Long) -> Unit) {
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isSeeking, currentPosition) {
        if (!isSeeking) {
            seekPosition = currentPosition
        }
    }

    Column {
        Slider(
            value = (if (isSeeking) seekPosition else currentPosition).toFloat(),
            onValueChange = { isSeeking = true; seekPosition = it.toLong() },
            onValueChangeFinished = { onSeek(seekPosition); isSeeking = false },
            valueRange = 0f..(if (duration > 0) duration.toFloat() else 1f),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TimeText(timeMillis = if (isSeeking) seekPosition else currentPosition)
            TimeText(timeMillis = duration)
        }
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    isEnabled: Boolean,
    onPlayPauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSkipPreviousClick, modifier = Modifier.size(56.dp), enabled = isEnabled) {
            Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = "Música anterior", modifier = Modifier.size(36.dp), tint = if (isEnabled) Color.White else Color.White.copy(alpha = 0.4f))
        }
        Surface(onClick = onPlayPauseClick, modifier = Modifier.size(72.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary, shadowElevation = 8.dp, enabled = isEnabled) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = if (isPlaying) "Pausar música" else "Tocar música", modifier = Modifier.size(40.dp), tint = Color.White)
            }
        }
        IconButton(onClick = onSkipNextClick, modifier = Modifier.size(56.dp), enabled = isEnabled) {
            Icon(imageVector = Icons.Filled.SkipNext, contentDescription = "Próxima música", modifier = Modifier.size(36.dp), tint = if (isEnabled) Color.White else Color.White.copy(alpha = 0.4f))
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatDuration(millis: Long): String {
    if (millis < 0) return "00:00"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
private fun TimeText(timeMillis: Long) {
    val timeString = formatDuration(timeMillis)
    AnimatedContent(
        targetState = timeString,
        transitionSpec = { fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150)) },
        label = "timeAnimation"
    ) { targetText ->
        Text(
            text = targetText,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}