package com.cebolarekords.player.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import com.cebolarekords.player.ui.theme.Dimens
import kotlinx.coroutines.delay

@Composable
fun AnimatedListItem(
    delay: Long = 0L,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(500))
    ) {
        content()
    }
}

@Composable
fun FadeScaleTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(220)) +
                scaleIn(initialScale = 0.92f, transformOrigin = TransformOrigin.Center),
        exit = fadeOut(animationSpec = tween(90)) +
                scaleOut(targetScale = 0.92f, transformOrigin = TransformOrigin.Center),
        content = content
    )
}

@Composable
fun MorphingButtonTransition(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(expanded, label = "morphTransition")
    val width by transition.animateDp(
        transitionSpec = { tween(300) },
        label = "widthTransition"
    ) { if (it) 120.dp else 48.dp }
    val cornerRadius by transition.animateDp(
        transitionSpec = { tween(300) },
        label = "cornerTransition"
    ) { if (it) Dimens.CornerRadiusMedium else Dimens.CornerRadiusExtraLarge }
    val elevation by transition.animateDp(
        transitionSpec = { tween(300) },
        label = "elevationTransition"
    ) { if (it) Dimens.ElevationMedium else Dimens.ElevationLarge }

    Surface(
        modifier = modifier.size(width, Dimens.PlayerControlSize),
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = elevation,
        content = content
    )
}