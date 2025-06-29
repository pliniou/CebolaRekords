package com.cebolarekords.player.ui.artists

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebolarekords.player.data.Artist
import com.cebolarekords.player.ui.theme.PoppinsFamily

@Composable
fun ArtistCard(
    artist: Artist,
    modifier: Modifier = Modifier
) {
    var showInfo by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cardScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (showInfo) 16.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "cardElevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { showInfo = !showInfo }
                )
            },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        AnimatedContent(
            targetState = showInfo,
            transitionSpec = {
                (slideInVertically(
                    animationSpec = tween(450, easing = EaseOutCubic)
                ) { height -> height } + fadeIn(animationSpec = tween(450))) togetherWith
                        (slideOutVertically(
                            animationSpec = tween(450, easing = EaseInCubic)
                        ) { height -> -height } + fadeOut(animationSpec = tween(450)))
            },
            label = "ArtistCardFlip"
        ) { isInfoVisible ->
            if (isInfoVisible) {
                ArtistCardBack(artist = artist)
            } else {
                ArtistCardFront(artist = artist)
            }
        }
    }
}

@Composable
private fun ArtistCardFront(artist: Artist) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagem com gradient overlay sutil
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(artist.coverImage)
                .crossfade(true)
                .build(),
            contentDescription = "Foto de perfil de ${artist.name}",
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay na parte inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.97f)
                        )
                    )
                )
        )

        // Nome do artista
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
                .padding(horizontal = 20.dp, vertical = 24.dp)
        )

        // Ícone de informação aprimorado
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
                .size(36.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 4.dp
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Ver biografia",
                tint = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun ArtistCardBack(artist: Artist) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header melhorado
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
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

        // Biografia com scroll suave
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = artist.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = PoppinsFamily,
                    lineHeight = 24.sp,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                textAlign = TextAlign.Justify
            )
        }
    }
}