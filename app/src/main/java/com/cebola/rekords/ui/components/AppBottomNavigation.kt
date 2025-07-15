package com.cebolarekords.player.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolarekords.player.navigation.AppNavigation

@Composable
fun AppBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ),
        color = Color.Transparent,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        NavigationContent(
            currentRoute = currentRoute,
            onNavigate = onNavigate,
            haptic = haptic
        )
    }
}

@Composable
private fun NavigationContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    haptic: HapticFeedback
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp,
            modifier = Modifier
                .height(72.dp)
                .padding(horizontal = 4.dp)
        ) {
            AppNavigation.bottomNavItems.forEach { screen ->
                NavigationItem(
                    screen = screen,
                    currentRoute = currentRoute,
                    onNavigate = onNavigate,
                    haptic = haptic
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavigationItem(
    screen: AppNavigation,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    haptic: HapticFeedback
) {
    val isSelected = currentRoute == screen.route
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )

    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )

    NavigationBarItem(
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.title,
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            )
        },
        label = {
            Text(
                text = screen.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            )
        },
        selected = isSelected,
        onClick = {
            if (!isSelected) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onNavigate(screen.route)
            }
        },
        colors = colors,
        alwaysShowLabel = true
    )
}