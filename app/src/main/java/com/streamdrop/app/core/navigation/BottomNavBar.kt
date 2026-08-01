package com.streamdrop.app.core.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.streamdrop.app.R
import com.streamdrop.app.core.ui.theme.*

/**
 * StreamDrop Bottom Navigation Bar
 *
 * A floating, pill-shaped bottom navigation bar with glassmorphism background.
 * The selected tab shows a violet indicator pill underneath the icon.
 */

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val labelRes: Int,
)

private val bottomNavItems = listOf(
    BottomNavItem(
        screen       = Screen.Home,
        icon         = Icons.Rounded.Home,
        selectedIcon = Icons.Rounded.Home,
        labelRes     = R.string.nav_home,
    ),
    BottomNavItem(
        screen       = Screen.History,
        icon         = Icons.Rounded.History,
        selectedIcon = Icons.Rounded.History,
        labelRes     = R.string.nav_history,
    ),
    BottomNavItem(
        screen       = Screen.Settings,
        icon         = Icons.Rounded.Settings,
        selectedIcon = Icons.Rounded.Settings,
        labelRes     = R.string.nav_settings,
    ),
)

@Composable
fun StreamDropBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show bottom bar on top-level destinations
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Settings.route,
    )

    if (!showBottomBar) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .navigationBarsPadding(),
    ) {
        // Glass card container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(SurfaceElevated.copy(alpha = 0.95f))
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.screen.route
                    BottomNavTab(
                        item       = item,
                        isSelected = isSelected,
                        onClick    = {
                            if (!isSelected) {
                                navController.navigate(item.screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavTab(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Violet400 else TextTertiary,
        animationSpec = tween(durationMillis = 200),
        label = "icon_color"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Violet500.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "bg_color"
    )

    NavigationBarItem(
        selected  = isSelected,
        onClick   = onClick,
        icon      = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = stringResource(item.labelRes),
                    tint    = iconColor,
                    modifier = Modifier.size(22.dp),
                )
            }
        },
        label = {
            Text(
                text  = stringResource(item.labelRes),
                color = iconColor,
                style = MaterialTheme.typography.labelSmall,
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor      = Violet400,
            unselectedIconColor    = TextTertiary,
            selectedTextColor      = Violet400,
            unselectedTextColor    = TextTertiary,
            indicatorColor         = Color.Transparent,
        ),
    )
}
