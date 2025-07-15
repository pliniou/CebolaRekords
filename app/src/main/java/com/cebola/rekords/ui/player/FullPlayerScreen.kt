package com.cebolarekords.player.ui.player

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebolarekords.player.R
import com.cebolarekords.player.player.PlayerUiState
import com.cebolarekords.player.ui.theme.Dimens
import com.cebolarekords.player.ui.theme.PoppinsFamily
import java.util.concurrent.TimeUnit

@SuppressLint("DefaultLocale")
private fun formatDuration(millis: Long): String {
    if (millis < 0) return "00:00"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}

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
                .systemBarsPadding() // REFINAMENTO: Adaptação elegante às barras do sistema.
                .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerTopBar(onNavigateUp = onNavigateUp)
            Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))
            AlbumArtWithAnimation(artworkData = artworkData)
            Spacer(modifier = Modifier.weight(1f)) // REFINAMENTO: Breathing room adequado.
            TrackInfoSection(
                title = currentTrack?.mediaMetadata?.title?.toString(),
                artist = currentTrack?.mediaMetadata?.artist?.toString()
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))
            SeekBarWithTimer(
                duration = playerState.duration,
                currentPosition = playerState.currentPosition,
                onSeek = onSeek
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
            PlaybackControls(
                playerState = playerState,
                onPlayPauseClick = onPlayPauseClick,
                onSkipNextClick = onSkipNextClick,
                onSkipPreviousClick = onSkipPreviousClick
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        }
    }
}

@Composable
private fun BackgroundArtwork(artworkData: ByteArray?) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val gradientColors = remember(backgroundColor) {
        listOf(
            backgroundColor.copy(alpha = 0.5f),
            backgroundColor
        )
    }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(artworkData)
            .allowHardware(false) // Necessário para efeitos de gradiente/shader.
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.ic_cebolarekords_album_art),
        error = painterResource(id = R.drawable.ic_cebolarekords_album_art),
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                // REFINAMENTO: Efeito de vinheta para focar no centro da UI.
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        radius = size.maxDimension * 0.8f
                    ),
                    blendMode = BlendMode.Darken
                )
                // REFINAMENTO: Gradiente vertical para integrar a imagem ao fundo suavemente.
                drawRect(
                    brush = Brush.verticalGradient(gradientColors),
                    blendMode = BlendMode.Multiply
                )
            }
            .graphicsLayer(alpha = 0.6f) // REFINAMENTO: Alfa reduzido para a imagem não dominar a tela.
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
            // REFINAMENTO: Uso de ícone AutoMirrored para suportar layouts RTL (direita-para-esquerda).
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White.copy(alpha = 0.8f))
        }
        // REFINAMENTO: Handle visual para indicar que a tela é um BottomSheet modal.
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        )
        Spacer(modifier = Modifier.size(48.dp)) // Alinhamento consistente.
    }
}

@Composable
private fun AlbumArtWithAnimation(artworkData: ByteArray?) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .aspectRatio(1f),
        shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge), // Bordas curvas consistentes.
        elevation = CardDefaults.cardElevation(Dimens.ElevationLarge)
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
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)) {
        Text(
            text = title ?: "Nenhuma música",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = Color.White
        )
        Text(
            text = artist ?: "Selecione uma música",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium
            ),
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeekBarWithTimer(duration: Long, currentPosition: Long, onSeek: (Long) -> Unit) {
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableLongStateOf(0L) }
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

    // REFINAMENTO: Animação sutil no polegar (thumb) do slider, oferecendo feedback visual discreto.
    val thumbSize by animateDpAsState(
        targetValue = if (isDragged) 24.dp else 20.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "thumbSize"
    )

    LaunchedEffect(isSeeking, currentPosition) {
        if (!isSeeking) {
            seekPosition = currentPosition
        }
    }

    Column {
        Slider(
            value = (if (isSeeking) seekPosition else currentPosition).toFloat(),
            onValueChange = {
                isSeeking = true
                seekPosition = it.toLong()
            },
            onValueChangeFinished = {
                onSeek(seekPosition)
                isSeeking = false
            },
            valueRange = 0f..(if (duration > 0) duration.toFloat() else 1f),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            interactionSource = interactionSource,
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    thumbSize = DpSize(thumbSize, thumbSize)
                )
            }
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TimeText(timeMillis = if (isSeeking) seekPosition else currentPosition)
            TimeText(timeMillis = duration)
        }
    }
}

@Composable
private fun PlaybackControls(
    playerState: PlayerUiState,
    onPlayPauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit
) {
    val isEnabled = playerState.currentTrack != null
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val prevButtonEnabled = isEnabled && playerState.hasPreviousTrack
        IconButton(onClick = onSkipPreviousClick, modifier = Modifier.size(56.dp), enabled = prevButtonEnabled) {
            Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = "Música anterior", modifier = Modifier.size(36.dp), tint = if (prevButtonEnabled) Color.White else Color.White.copy(alpha = 0.4f))
        }

        Surface(
            onClick = onPlayPauseClick,
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = Dimens.ElevationLarge,
            enabled = isEnabled
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (playerState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = if (playerState.isPlaying) "Pausar música" else "Tocar música", modifier = Modifier.size(40.dp), tint = Color.White)
            }
        }

        val nextButtonEnabled = isEnabled && playerState.hasNextTrack
        IconButton(onClick = onSkipNextClick, modifier = Modifier.size(56.dp), enabled = nextButtonEnabled) {
            Icon(imageVector = Icons.Filled.SkipNext, contentDescription = "Próxima música", modifier = Modifier.size(36.dp), tint = if (nextButtonEnabled) Color.White else Color.White.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun TimeText(timeMillis: Long) {
    val timeString = formatDuration(timeMillis)
    // REFINAMENTO: Animação de fade suave para a atualização do tempo, evitando saltos visuais (jank).
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