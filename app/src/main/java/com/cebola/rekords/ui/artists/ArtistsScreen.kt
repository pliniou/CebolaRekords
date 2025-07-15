package com.cebolarekords.player.ui.artists

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cebolarekords.player.ui.components.AnimatedListItem
import com.cebolarekords.player.ui.theme.Dimens
import com.cebolarekords.player.ui.theme.PoppinsFamily
import kotlinx.coroutines.delay

@Composable
fun ArtistsScreen(
    viewModel: ArtistsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val artists = uiState.artists
    val listState = rememberLazyListState()
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(120) // Pequeno atraso para iniciar a animação de entrada.
        isContentVisible = true
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(all = Dimens.PaddingLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge),
        modifier = Modifier
            .fillMaxSize()
            .background(
                // REFINAMENTO: Uso de um gradiente sutil no fundo para adicionar profundidade visual
                // sem sobrecarregar o design, mantendo a paleta de cores harmoniosa.
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        item {
            AnimatedVisibility(
                visible = isContentVisible,
                // REFINAMENTO: Animação de entrada suave para o cabeçalho, com easing para um
                // início e fim de movimento mais natural.
                enter = slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(700, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(700))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.PaddingSmall),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)
                ) {
                    Text(
                        text = "Nossos Artistas",
                        // REFINAMENTO: Consistência na hierarquia tipográfica usando os estilos do tema.
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Talentos da Cebola Rekords",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = PoppinsFamily,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                    )
                }
            }
        }
        itemsIndexed(
            items = artists,
            key = { _, artist -> artist.id }
        ) { index, artist ->
            // Efeito de parallax sutil para os itens visíveis, adicionando dinamismo à rolagem.
            val parallaxOffset = listState.firstVisibleItemScrollOffset * 0.1f

            AnimatedListItem(delay = (index * 60L)) {
                ArtistCard(
                    artist = artist,
                    modifier = Modifier.graphicsLayer {
                        // Aplica o parallax apenas ao primeiro item visível para otimizar a performance.
                        translationY = if (listState.firstVisibleItemIndex == index) parallaxOffset else 0f
                    }
                )
            }
        }
        item {
            // Espaçamento no final da lista para garantir que o MiniPlayer não sobreponha o último item.
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}