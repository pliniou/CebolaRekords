package com.cebola.rekords.ui.about

import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebola.rekords.R
import com.cebola.rekords.ui.components.AnimatedListItem
import com.cebola.rekords.ui.theme.PoppinsFamily

@Composable
fun SobreScreen() {
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (_: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            AnimatedListItem(delay = 100L) {
                ScreenHeader()
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            AnimatedListItem(delay = 200L) {
                InfoCard(
                    icon = Icons.Default.Info,
                    title = "Cebola Rekords",
                    subtitle = "Selo Eletrônico Visionário",
                    isExpandable = false
                ) {
                    Text(
                        text = "Fundada em Brasília, capital da música eletrônica brasileira. Conectamos artistas visionários com públicos apaixonados, criando pontes entre a cena underground e o cenário global.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            AnimatedListItem(delay = 300L) {
                InfoCard(
                    icon = Icons.Default.Code,
                    title = "Stack Tecnológico",
                    subtitle = "Construído com as melhores ferramentas",
                    isExpandable = true,
                    isInitiallyExpanded = false
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        TechListItem("Kotlin & Coroutines", "Linguagem moderna e programação assíncrona para máxima performance.")
                        TechListItem("Jetpack Compose", "UI declarativa, reativa e totalmente nativa para uma experiência fluida.")
                        TechListItem("Material Design 3", "Design system do Google, garantindo uma estética moderna e coesa.")
                        TechListItem("Media3 ExoPlayer", "Reprodução de mídia de alta performance com controles avançados.")
                        TechListItem("Hilt & Room", "Injeção de dependência robusta e persistência de dados local eficiente.")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(48.dp)) }

        item {
            AnimatedListItem(delay = 400L) {
                Footer(versionName = versionName)
            }
        }
    }
}

@Composable
private fun ScreenHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(100.dp),
            shadowElevation = 12.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cebolarekords),
                contentDescription = "Logo Cebola Rekords",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(CircleShape)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Cebola Rekords",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Player de Música Eletrônica",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InfoCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isExpandable: Boolean = false,
    isInitiallyExpanded: Boolean = true,
    content: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(isInitiallyExpanded) }
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isExpandable,
                onClick = { if (isExpandable) isExpanded = !isExpanded }
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }

                if (isExpandable) {
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        animationSpec = tween(300),
                        label = "rotation"
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Recolher" else "Expandir",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.graphicsLayer { rotationZ = rotationAngle }
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)),
                exit = shrinkVertically(animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun TechListItem(title: String, description: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(6.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun Footer(versionName: String) {
    Column(
        modifier = Modifier.fillMaxWidth().alpha(0.7f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Versão $versionName",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "© 2025 Cebola Studios",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}