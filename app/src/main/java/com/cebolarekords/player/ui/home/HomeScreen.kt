package com.cebolarekords.player.ui.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.cebolarekords.player.data.SocialLink
import com.cebolarekords.player.ui.components.AnimatedListItem
import com.cebolarekords.player.ui.theme.PoppinsFamily
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
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
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item {
            AnimatedHeader(isContentVisible = isContentVisible)
        }
        item {
            AnimatedListItem(delay = 300L) {
                WelcomeHeroCard()
            }
        }
        item {
            AnimatedListItem(delay = 400L) {
                SocialSection(
                    onSocialClick = { url -> uriHandler.openUri(url) }
                )
            }
        }
        item {
            AnimatedFooter(isContentVisible = isContentVisible)
        }
    }
}

@Composable
private fun AnimatedHeader(isContentVisible: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "headerAlpha"
    )
    val slideOffsetY by animateDpAsState(
        targetValue = if (isContentVisible) 0.dp else (-40).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "headerSlide"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .offset(y = slideOffsetY),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxSize()
            ) {}
            Image(
                painter = painterResource(R.drawable.ic_cebolarekords_circle_white_transparent),
                contentDescription = "Cebola Rekords Logo",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Cebola Rekords",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Música eletrônica de Brasília",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun WelcomeHeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.ic_backgroud_3966x3966)
                    .crossfade(true)
                    .build(),
                contentDescription = "Background musical",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "Bem-vindo",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Descubra nossa coleção musical única",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = PoppinsFamily,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun SocialSection(onSocialClick: (String) -> Unit) {
    val socialLinks = remember {
        listOf(
            SocialLink("Instagram", "https://www.instagram.com/cebolarekords?igshid=MzMyNGUyNmU2YQ%3D%3D", R.drawable.ic_instagram, Color(0xFFE4405F)),
            SocialLink("Spotify", "https://open.spotify.com/intl-pt/artist/5RygIOY1z4G7AZhhSAuSBe?si=ijSHxj1lRs-mD21BE7KgYQ&nd=1&dlsi=1aaa2b07f7c74e32", R.drawable.ic_spotify, Color(0xFF1DB954)),
            SocialLink("YouTube", "https://youtube.com/@bsbplinio?si=aaj6WijgRTD7UfjB", R.drawable.ic_youtube, Color(0xFFFF0000)),
            SocialLink("SoundCloud", "https://soundcloud.com/pliniou", R.drawable.ic_soundcloud, Color(0xFFFF5500))
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Conecte-se conosco",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(socialLinks) { social ->
                SocialButton(
                    social = social,
                    onClick = { onSocialClick(social.url) }
                )
            }
        }
    }
}

@Composable
private fun SocialButton(social: SocialLink, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "socialScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.width(80.dp)
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(68.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 4.dp,
            interactionSource = interactionSource
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = social.iconRes),
                    contentDescription = social.platform,
                    modifier = Modifier.size(30.dp),
                    tint = social.color
                )
            }
        }

        Text(
            text = social.platform,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AnimatedFooter(isContentVisible: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 900),
        label = "footerAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(3.dp)
                .background(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(1.5.dp)
                )
        )

        Text(
            text = "© 2025 Cebola Rekords",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Text(
            text = "Brasília • Brasil",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = PoppinsFamily
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}