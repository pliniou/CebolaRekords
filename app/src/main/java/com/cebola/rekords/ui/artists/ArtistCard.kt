package com.cebola.rekords.ui.artists

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebola.rekords.data.Artist
import com.cebola.rekords.ui.theme.Dimens
import com.cebola.rekords.ui.theme.PoppinsFamily

@Composable
fun ArtistCard(
    artist: Artist,
    modifier: Modifier = Modifier
) {
    var showInfo by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (showInfo) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardRotation"
    )
    val context = LocalContext.current
    val artworkRequest = remember(artist.id, context) {
        ImageRequest.Builder(context)
            .data(artist.coverImage)
            .crossfade(true)
            .build()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { showInfo = !showInfo })
            }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        if (rotation <= 90f) {
            ArtistCardFront(artist = artist, artwork = artworkRequest)
        } else {
            Box(
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                ArtistCardBack(artist = artist)
            }
        }
    }
}

@Composable
private fun ArtistCardFront(artist: Artist, artwork: ImageRequest) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = artwork,
            contentDescription = "Foto de perfil de ${artist.name}",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Dimens.CornerRadiusExtraLarge)),
            contentScale = ContentScale.Crop
        )
        // Gradiente para garantir a legibilidade do nome do artista sobre a imagem.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                )
        )
        Text(
            text = artist.name,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(all = Dimens.PaddingLarge)
        )
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimens.PaddingMedium)
                .size(36.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            shadowElevation = 4.dp
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Ver biografia",
                tint = Color.White,
                modifier = Modifier.padding(Dimens.PaddingSmall)
            )
        }
    }
}

@Composable
private fun ArtistCardBack(artist: Artist) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(Dimens.PaddingLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Text(
                text = artist.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = artist.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                textAlign = TextAlign.Start
            )
        }
    }
}