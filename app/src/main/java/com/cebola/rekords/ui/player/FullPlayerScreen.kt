package com.cebola.rekords.ui.player

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebola.rekords.R
import com.cebola.rekords.playback.PlayerUiState
import com.cebola.rekords.ui.theme.Dimens
import com.cebola.rekords.ui.theme.PoppinsFamily
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
    onSeek: (Long) -> Unit,
    onShuffleToggle: (() -> Unit)? = null,
    onRepeatToggle: (() -> Unit)? = null
) {
    val currentTrack = playerState.currentTrack
    val artworkData = currentTrack?.mediaMetadata?.artworkData

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .semantics {
                contentDescription = "Player de música completo"
            }
    ) {
        BackgroundArtwork(artworkData = artworkData)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(
                    horizontal = Dimens.PaddingLarge,
                    vertical = Dimens.PaddingMedium
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerTopBar(onNavigateUp = onNavigateUp)
            Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))
            AlbumArtWithAnimation(
                artworkData = artworkData,
                isPlaying = playerState.isPlaying
            )
            Spacer(modifier = Modifier.weight(1f))
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
                onSkipPreviousClick = onSkipPreviousClick,
                onShuffleToggle = onShuffleToggle,
                onRepeatToggle = onRepeatToggle
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
            .allowHardware(false)
            .crossfade(true)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.ic_cebolarekords_album_art),
        error = painterResource(id = R.drawable.ic_cebolarekords_album_art),
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        radius = size.maxDimension * 0.8f
                    ),
                    blendMode = BlendMode.Darken
                )
                drawRect(
                    brush = Brush.verticalGradient(gradientColors),
                    blendMode = BlendMode.Multiply
                )
            }
            .graphicsLayer(alpha = 0.6f)
    )
}

@Composable
private fun PlayerTopBar(onNavigateUp: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onNavigateUp,
            modifier = Modifier.semantics {
                contentDescription = "Voltar para tela anterior"
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f)
            )
        }
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun AlbumArtWithAnimation(
    artworkData: ByteArray?,
    isPlaying: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "albumArtScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .aspectRatio(1f)
            .scale(scale), // Aplicando a escala aqui.
        shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge),
        elevation = CardDefaults.cardElevation(Dimens.ElevationLarge)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(artworkData)
                .crossfade(true)
                .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                .build(),
            contentDescription = "Capa do álbum",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_cebolarekords_album_art),
            error = painterResource(id = R.drawable.ic_cebolarekords_album_art),
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = "Capa do álbum em reprodução"
                }
        )
    }
}

@Composable
private fun TrackInfoSection(title: String?, artist: String?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        Text(
            text = title ?: "Nenhuma música",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier.semantics {
                contentDescription = "Título da música: ${title ?: "Nenhuma música"}"
            }
        )
        Text(
            text = artist ?: "Selecione uma música",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium
            ),
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics {
                contentDescription = "Artista: ${artist ?: "Selecione uma música"}"
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeekBarWithTimer(
    duration: Long,
    currentPosition: Long,
    onSeek: (Long) -> Unit
) {
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableLongStateOf(0L) }
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

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

    Column(
        modifier = Modifier.semantics {
            contentDescription = "Controle de posição da música"
        }
    ) {
        Slider(
            value = (if (isSeeking) seekPosition else currentPosition).toFloat(),
            onValueChange = { value ->
                isSeeking = true
                seekPosition = value.toLong()
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimeText(
                timeMillis = if (isSeeking) seekPosition else currentPosition,
                contentDescription = "Posição atual"
            )
            TimeText(
                timeMillis = duration,
                contentDescription = "Duração total"
            )
        }
    }
}

@Composable
private fun PlaybackControls(
    playerState: PlayerUiState,
    onPlayPauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onShuffleToggle: (() -> Unit)?,
    onRepeatToggle: (() -> Unit)?
) {
    val isEnabled = playerState.currentTrack != null
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shuffle Button
        IconButton(
            onClick = { onShuffleToggle?.invoke() },
            enabled = isEnabled && onShuffleToggle != null,
            modifier = Modifier.semantics {
                contentDescription = if (playerState.isShuffleEnabled) "Desativar modo aleatório" else "Ativar modo aleatório"
            }
        ) {
            Icon(
                imageVector = if (playerState.isShuffleEnabled) Icons.Filled.ShuffleOn else Icons.Filled.Shuffle,
                contentDescription = null,
                tint = if (playerState.isShuffleEnabled) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(28.dp)
            )
        }

        // Previous Button
        val prevButtonEnabled = isEnabled && playerState.hasPreviousTrack
        IconButton(
            onClick = onSkipPreviousClick,
            modifier = Modifier
                .size(64.dp)
                .semantics {
                    contentDescription = "Música anterior"
                },
            enabled = prevButtonEnabled
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (prevButtonEnabled) Color.White else Color.White.copy(alpha = 0.4f)
            )
        }

        // Play/Pause Button
        Surface(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(72.dp)
                .semantics {
                    contentDescription = if (playerState.isPlaying) "Pausar música" else "Tocar música"
                },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = Dimens.ElevationLarge,
            enabled = isEnabled
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
        }

        // Next Button
        val nextButtonEnabled = isEnabled && playerState.hasNextTrack
        IconButton(
            onClick = onSkipNextClick,
            modifier = Modifier
                .size(64.dp)
                .semantics {
                    contentDescription = "Próxima música"
                },
            enabled = nextButtonEnabled
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (nextButtonEnabled) Color.White else Color.White.copy(alpha = 0.4f)
            )
        }

        // Repeat Button
        IconButton(
            onClick = { onRepeatToggle?.invoke() },
            enabled = isEnabled && onRepeatToggle != null,
            modifier = Modifier.semantics {
                contentDescription = when (playerState.repeatMode) {
                    Player.REPEAT_MODE_OFF -> "Ativar repetição"
                    Player.REPEAT_MODE_ALL -> "Repetir uma música"
                    Player.REPEAT_MODE_ONE -> "Desativar repetição"
                    else -> "Controle de repetição"
                }
            }
        ) {
            val (icon, tint) = when (playerState.repeatMode) {
                Player.REPEAT_MODE_OFF -> Icons.Filled.Repeat to Color.White.copy(alpha = 0.8f)
                Player.REPEAT_MODE_ALL -> Icons.Filled.Repeat to MaterialTheme.colorScheme.primary
                Player.REPEAT_MODE_ONE -> Icons.Filled.RepeatOne to MaterialTheme.colorScheme.primary
                else -> Icons.Filled.Repeat to Color.White.copy(alpha = 0.8f)
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun TimeText(
    timeMillis: Long,
    contentDescription: String
) {
    val timeString = formatDuration(timeMillis)
    AnimatedContent(
        targetState = timeString,
        transitionSpec = {
            fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
        },
        label = "timeAnimation"
    ) { targetText ->
        Text(
            text = targetText,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.semantics {
                this.contentDescription = "$contentDescription: $targetText"
            }
        )
    }
}