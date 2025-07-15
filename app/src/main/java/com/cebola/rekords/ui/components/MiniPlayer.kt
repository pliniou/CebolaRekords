package com.cebola.rekords.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebola.rekords.R

@Composable
fun MiniPlayer(
    track: MediaItem?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onPlayerClick: (() -> Unit)? = null,
    progress: Float = 0f,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val metadata = track?.mediaMetadata
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val context = LocalContext.current
    
    val elevation by animateDpAsState(
        targetValue = if (isPlaying) 12.dp else 4.dp,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "elevation"
    )
    
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 4.dp)
            .clickable(
                enabled = onPlayerClick != null && track != null,
                onClick = { onPlayerClick?.invoke() }
            )
            .semantics {
                contentDescription = if (track != null) {
                    "Mini player: ${metadata?.title} de ${metadata?.artist}. ${if (isPlaying) "Tocando" else "Pausado"}"
                } else {
                    "Mini player: Nenhuma música selecionada"
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(elevation),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(metadata?.artworkData ?: metadata?.artworkUri)
                            .crossfade(true)
                            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "Capa de ${metadata?.title ?: "música"}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_cebolarekords_album_art),
                        error = painterResource(R.drawable.ic_cebolarekords_album_art)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = metadata?.title?.toString() ?: "Nenhuma música",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = metadata?.artist?.toString() ?: "Toque uma música",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Surface(
                    onClick = onPlayPauseClick,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(44.dp)
                        .scale(buttonScale)
                        .semantics {
                            contentDescription = if (isPlaying) "Pausar música" else "Tocar música"
                        },
                    enabled = track != null,
                    interactionSource = interactionSource
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = if (track != null) Color.White else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            if (track != null && progress > 0f) {
                LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .semantics {
                                            contentDescription = "Progresso da música: ${(progress * 100).toInt()}%"
                                        },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round,
                )
            }
        }
    }
}