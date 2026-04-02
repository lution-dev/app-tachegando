package org.example.project.core.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isVendor: Boolean
) {
    val items = if (isVendor) {
        listOf(
            NavDestination.VendorDashboard,
            NavDestination.VendorRoute,
            NavDestination.VendorHeatmap,
            NavDestination.VendorSettings
        )
    } else {
        listOf(
            NavDestination.ConsumerHome,
            NavDestination.ConsumerCategories,
            NavDestination.Notifications,
            NavDestination.ConsumerProfile
        )
    }

    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        items.forEach { dest ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = destinationIcon(dest.route),
                        contentDescription = dest.title
                    )
                },
                label = { Text(dest.title) },
                selected = currentRoute == dest.route,
                onClick = {
                    if (currentRoute != dest.route) {
                        onNavigate(dest.route)
                    }
                }
            )
        }
    }
}

private fun destinationIcon(route: String): ImageVector {
    return when (route) {
        NavDestination.ConsumerHome.route -> LocationOnIcon
        NavDestination.ConsumerCategories.route -> SearchIcon
        NavDestination.Notifications.route -> NotificationsIcon
        NavDestination.ConsumerProfile.route -> PersonIcon
        NavDestination.VendorDashboard.route -> HomeIcon
        NavDestination.VendorRoute.route -> RouteIcon
        NavDestination.VendorHeatmap.route -> HeatmapIcon
        NavDestination.VendorSettings.route -> SettingsIcon
        else -> HomeIcon
    }
}
