package com.cebolarekords.player.ui.about

import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolarekords.player.R
import com.cebolarekords.player.ui.theme.PoppinsFamily
import kotlinx.coroutines.delay

@Composable
fun SobreScreen() {
    val scrollState = rememberScrollState()
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isContentVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        AnimatedVisibility(
            visible = isContentVisible,
            enter = slideInVertically(
                initialOffsetY = { -it / 3 },
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(700))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                AnimatedHeader()
                AnimatedCardItem(delay = 0) { LabelInfoCard() }
                AnimatedCardItem(delay = 100) { CityContextCard() }
                AnimatedCardItem(delay = 200) { TechStackCard() }
                AnimatedCardItem(delay = 300) { VersionCard() }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AnimatedHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(88.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxSize()
            ) {}
            Image(
                painter = painterResource(id = R.drawable.ic_cebolarekords_circle_white_transparent),
                contentDescription = "Logo Cebola Rekords",
                modifier = Modifier.size(64.dp)
            )
        }
        Text(
            text = "Sobre o App",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun AnimatedCardItem(
    delay: Long,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(animationSpec = tween(500))
    ) {
        content()
    }
}

@Composable
fun LabelInfoCard() {
    var isExpanded by remember { mutableStateOf(false) }
    EnhancedInfoCard(
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        headerContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxSize()
                    ) {}
                    Image(
                        painter = painterResource(id = R.drawable.ic_cebolarekords_circle_white_transparent),
                        contentDescription = "Logo Cebola Rekords",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Cebola Rekords",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Selo musical eletrônico",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        expandedContent = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Fundada em Brasília em 2020, celebramos a diversidade da música eletrônica brasileira.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = PoppinsFamily,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
                Text(
                    text = "Conectamos artistas visionários com públicos apaixonados, criando pontes entre o underground local e a cena global.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = PoppinsFamily,
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    )
}

@Composable
fun CityContextCard() {
    var isExpanded by remember { mutableStateOf(false) }
    EnhancedInfoCard(
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        headerContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxSize()
                    ) {}
                    Image(
                        painter = painterResource(id = R.drawable.ic_brasilia_1024x1024_branco_transparente),
                        contentDescription = "Brasília DF",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Brasília/DF",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Capital da eletrônica brasileira",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        },
        expandedContent = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Reconhecida mundialmente como epicentro da música eletrônica brasileira desde os anos 90.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = PoppinsFamily,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
                Text(
                    text = "Nossa cidade cultiva uma das cenas mais inovadoras da América Latina, inspirando cada projeto que apoiamos.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = PoppinsFamily,
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    )
}

@Composable
fun TechStackCard() {
    var isExpanded by remember { mutableStateOf(false) }
    EnhancedInfoCard(
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        headerContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxSize()
                    ) {}
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Tecnologias",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Tecnologias",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Stack moderno Android",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        expandedContent = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Desenvolvido com tecnologias modernas para a melhor experiência musical:",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = PoppinsFamily,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TechItem("Kotlin", "Linguagem moderna e robusta")
                    TechItem("Jetpack Compose", "Interface declarativa nativa")
                    TechItem("Material Design 3", "Sistema de design Google")
                    TechItem("Media3 ExoPlayer", "Reprodução otimizada")
                }
            }
        }
    )
}

@Composable
fun TechItem(title: String, description: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(8.dp)
                .offset(y = 6.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {}
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PoppinsFamily
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = PoppinsFamily
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun VersionCard() {
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) { "0.5" }
    }
    EnhancedInfoCard(
        isExpanded = false,
        onToggle = { },
        headerContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxSize()
                        ) {}
                        Image(
                            painter = painterResource(id = R.drawable.ic_desenvolvimento_1024_branco_transparente),
                            contentDescription = "Desenvolvimento",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Versão Beta",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Em desenvolvimento",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = versionName ?: "0.5",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFamily
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        },
        expandedContent = null
    )
}

@Composable
fun EnhancedInfoCard(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    headerContent: @Composable () -> Unit,
    expandedContent: (@Composable () -> Unit)?
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateDpAsState(
        targetValue = when {
            isExpanded -> 8.dp
            isPressed -> 6.dp
            else -> 2.dp
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardElevation"
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed && expandedContent != null) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    // ACESSIBILIDADE: Adicionado semantics para descrever o estado do card.
    val cardStateDescription = if (isExpanded) "Expandido" else "Recolhido"
    val cardModifier = Modifier
        .fillMaxWidth()
        .scale(scale)
        .semantics {
            stateDescription = cardStateDescription
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = expandedContent != null,
            onClick = onToggle,
            role = Role.Button
        )


    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) { headerContent() }
                if (expandedContent != null) {
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        animationSpec = tween(300),
                        label = "chevronRotation"
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Recolher" else "Expandir",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(4.dp)
                                .graphicsLayer { rotationZ = rotationAngle }
                        )
                    }
                }
            }
            expandedContent?.let { content ->
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                        content()
                    }
                }
            }
        }
    }
}