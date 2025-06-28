package com.cebolarekords.player.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cebolarekords.player.R
import com.cebolarekords.player.ui.components.AnimatedListItem
import com.cebolarekords.player.ui.theme.PoppinsFamily
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onNavigateToArtists: () -> Unit,
    onNavigateToMusic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val uriHandler = LocalUriHandler.current

    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isContentVisible = true
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item { AnimatedHeader(isContentVisible = isContentVisible) }
        item {
            AnimatedListItem(delay = 600L) { // Delay para aparecer após o cabeçalho
                WelcomeHeroCard()
            }
        }
        // REMOVIDO: Seção AnimatedQuickActions e seu título "Explore"
        /*
        item {
            AnimatedQuickActions(
                isContentVisible = isContentVisible,
                onNavigateToArtists = onNavigateToArtists,
                onNavigateToMusic = onNavigateToMusic
            )
        }
        */
        item {
            AnimatedSocialSection(
                isContentVisible = isContentVisible,
                onSocialClick = { url -> uriHandler.openUri(url) }
            )
        }
        item { AnimatedFooter(isContentVisible = isContentVisible) }
    }
}

@Composable
private fun AnimatedHeader(isContentVisible: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200),
        label = "headerAlpha"
    )
    val slideOffsetY by animateDpAsState(
        targetValue = if (isContentVisible) 0.dp else (-50).dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "headerSlide"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .alpha(alpha)
            .offset(y = slideOffsetY),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ALTERADO: Fundo do Surface alterado para transparente para o ícone transparente
        Surface(
            shape = CircleShape,
            color = Color.Transparent, // ALTERADO: Fundo transparente
            shadowElevation = 8.dp, // Mantém a sombra
            modifier = Modifier.size(90.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_cebolarekords_circle_white_transparent),
                contentDescription = "Cebola Rekords Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Cebola Rekords",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Text(
            text = "An Cebola Company",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = PoppinsFamily,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun WelcomeHeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.ic_backgroud_3966x3966)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagem de fundo decorativa",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
            )

            // Gradiente para melhorar a legibilidade do texto (se houvesse) ou apenas efeito visual
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.0f),
                                Color.Black.copy(alpha = 0.5f),
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // ALTERADO: Conteúdo de texto removido, deixando apenas a imagem e o gradiente
        }
    }
}

/* REMOVIDO:
@Composable
private fun AnimatedQuickActions(isContentVisible: Boolean, onNavigateToArtists: () -> Unit, onNavigateToMusic: () -> Unit) {
    val actions = remember {
        listOf(
            QuickAction("Artistas", "Conheça", Icons.Default.Person, { MaterialTheme.colorScheme.primary }, onNavigateToArtists),
            QuickAction("Músicas", "Ouça nosso catálogo", Icons.Default.MusicNote, { MaterialTheme.colorScheme.secondary }, onNavigateToMusic)
        )
    }
    AnimatedVisibility(
        visible = isContentVisible,
        enter = fadeIn(animationSpec = tween(600, delayMillis = 800))
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            SectionTitle("Explore")
            actions.forEachIndexed { index, action ->
                AnimatedListItem(delay = (index * 150L)) {
                    QuickActionCard(action = action)
                }
            }
        }
    }
}
*/

@Composable
private fun QuickActionCard(action: QuickAction, modifier: Modifier = Modifier) {
    Card(
        onClick = action.onClick,
        modifier = modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = action.color().copy(alpha = 0.1f), modifier = Modifier.size(56.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = action.icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = action.color())
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = action.title, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), color = MaterialTheme.colorScheme.onSurface)
                Text(text = action.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun AnimatedSocialSection(isContentVisible: Boolean, onSocialClick: (String) -> Unit) {
    val socialLinks = remember {
        listOf(
            SocialLink("Instagram", "https://instagram.com/cebolarekords", R.drawable.ic_instagram, Color(0xFFE4405F)),
            SocialLink("Spotify", "https://open.spotify.com/intl-pt/artist/5RygIOY1z4G7AZhhSAuSBe?si=SBzhdiZoS1qc8COGlcxbXQ", R.drawable.ic_spotify, Color(0xFF1DB954)),
            SocialLink("YouTube", "https://youtube.com/@cebolarekords", R.drawable.ic_youtube, Color(0xFFFF0000)),
            SocialLink("SoundCloud", "https://on.soundcloud.com/YO3JYqmDL5HvrCpFGe", R.drawable.ic_soundcloud, Color(0xFFFF5500))
        )
    }
    AnimatedVisibility(
        visible = isContentVisible,
        enter = fadeIn(animationSpec = tween(600, delayMillis = 1000)) // Aumenta o delay
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally // ALTERADO: Centraliza a linha de redes sociais
        ) {
            SectionTitle("Redes Sociais", modifier = Modifier.padding(horizontal = 24.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp), contentPadding = PaddingValues(horizontal = 24.dp)) {
                items(socialLinks) { social ->
                    AnimatedListItem(delay = 0L) {
                        SocialButton(social = social, onClick = { onSocialClick(social.url) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialButton(social: SocialLink, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "socialScale"
    )
    Surface(
        onClick = onClick,
        modifier = Modifier.size(64.dp).graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest, // ALTERADO: Cor de fundo para contrastar elegantemente
        tonalElevation = 4.dp,
        interactionSource = interactionSource
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = social.iconRes),
                contentDescription = social.platform,
                modifier = Modifier.size(28.dp),
                tint = social.color
            )
        }
    }
}


@Composable
private fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun AnimatedFooter(isContentVisible: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(800, delayMillis = 1800), // Aumenta o delay
        label = "footerAlpha"
    )
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = "© 2025 Cebola Rekords", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), textAlign = TextAlign.Center)
        Text(text = "Brasília • Brasil", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), textAlign = TextAlign.Center)
    }
}

private data class QuickAction(val title: String, val subtitle: String, val icon: ImageVector, val color: @Composable () -> Color, val onClick: () -> Unit)
private data class SocialLink(val platform: String, val url: String, @DrawableRes val iconRes: Int, val color: Color)