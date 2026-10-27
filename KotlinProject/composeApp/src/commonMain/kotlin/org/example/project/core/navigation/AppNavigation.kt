package org.example.project.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import kotlinx.coroutines.launch
import org.example.project.features.auth.ConsumerRegisterScreen
import org.example.project.features.auth.ForgotPasswordScreen
import org.example.project.features.auth.LoginScreen
import org.example.project.features.auth.RegisterTypeScreen
import org.example.project.features.auth.VendorRegisterScreen
import org.example.project.features.consumer.CategoryGridScreen
import org.example.project.features.consumer.ConsumerHomeScreen
import org.example.project.features.consumer.ConsumerProfileScreen
import org.example.project.features.consumer.FavoritesScreen
import org.example.project.features.consumer.VendorListScreen
import org.example.project.features.consumer.VendorProfileScreen
import org.example.project.features.consumer.VendorTrackingScreen
import org.example.project.features.notifications.NotificationsScreen
import org.example.project.features.onboarding.OnboardingScreen
import org.example.project.features.splash.SplashScreen
import org.example.project.features.vendor.HeatmapScreen
import org.example.project.features.vendor.SubscriptionScreen
import org.example.project.features.vendor.VendorDashboardScreen
import org.example.project.features.vendor.VendorProductsScreen
import org.example.project.features.vendor.VendorRouteScreen
import org.example.project.features.vendor.VendorSettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val showConsumerBottomNav = currentRoute in listOf(
        NavDestination.ConsumerHome.route,
        NavDestination.ConsumerCategories.route,
        NavDestination.Notifications.route,
        NavDestination.ConsumerProfile.route
    )

    val showVendorBottomNav = currentRoute in listOf(
        NavDestination.VendorDashboard.route,
        NavDestination.VendorRoute.route,
        NavDestination.VendorHeatmap.route,
        NavDestination.VendorSettings.route
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        bottomBar = {
            if (showConsumerBottomNav || showVendorBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(
                                if (showConsumerBottomNav) NavDestination.ConsumerHome.route
                                else NavDestination.VendorDashboard.route
                            ) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    isVendor = showVendorBottomNav
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavDestination.Splash.route) {
                SplashScreen(onNavigateNext = { navController.navigate(NavDestination.Onboarding.route) })
            }
            composable(NavDestination.Onboarding.route) {
                OnboardingScreen(
                    onLoginClick = { navController.navigate(NavDestination.Login.route) },
                    onRegisterClick = { navController.navigate(NavDestination.RegisterType.route) }
                )
            }
            composable(NavDestination.Login.route) {
                LoginScreen(
                    onLogin = { role ->
                        val isVendor = role == "vendor"
                        val destination = if (isVendor) {
                            NavDestination.VendorDashboard.route
                        } else {
                            NavDestination.ConsumerHome.route
                        }
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (isVendor) "Você entrou como profissional."
                                else "Você entrou como cliente."
                            )
                        }
                        navController.navigate(destination) {
                            popUpTo(NavDestination.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(NavDestination.RegisterType.route) },
                    onForgotPassword = { navController.navigate(NavDestination.ForgotPassword.route) }
                )
            }
            composable(NavDestination.ForgotPassword.route) {
                ForgotPasswordScreen(onBack = { navController.popBackStack() })
            }
            composable(NavDestination.RegisterType.route) {
                RegisterTypeScreen(
                    onBack = { navController.popBackStack() },
                    onConsumerSelected = { navController.navigate(NavDestination.ConsumerRegister.route) },
                    onVendorSelected = { navController.navigate(NavDestination.VendorRegister.route) }
                )
            }
            composable(NavDestination.ConsumerRegister.route) {
                ConsumerRegisterScreen(
                    onBack = { navController.popBackStack() },
                    onRegisterComplete = { navController.navigate(NavDestination.Login.route) }
                )
            }
            composable(NavDestination.VendorRegister.route) {
                VendorRegisterScreen(
                    onBack = { navController.popBackStack() },
                    onRegisterComplete = { navController.navigate(NavDestination.VendorSubscription.route) }
                )
            }
            composable(NavDestination.Notifications.route) {
                NotificationsScreen(
                    onNotificationClick = { vendorId ->
                        navController.navigate(NavDestination.ConsumerVendorProfile.createRoute(vendorId))
                    }
                )
            }
            composable(NavDestination.ConsumerHome.route) {
                ConsumerHomeScreen(
                    onVendorClick = { vendorId ->
                        navController.navigate(NavDestination.ConsumerVendorTracking.createRoute(vendorId))
                    }
                )
            }
            composable(NavDestination.ConsumerCategories.route) {
                CategoryGridScreen(
                    onCategoryClick = { category ->
                        navController.navigate(NavDestination.ConsumerVendorList.createRoute(category))
                    }
                )
            }
            composable(
                route = NavDestination.ConsumerVendorList.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.read { getStringOrNull("category") } ?: ""
                VendorListScreen(
                    category = category,
                    onBack = { navController.popBackStack() },
                    onVendorClick = { vendorId ->
                        navController.navigate(NavDestination.ConsumerVendorProfile.createRoute(vendorId))
                    }
                )
            }
            composable(
                route = NavDestination.ConsumerVendorProfile.route,
                arguments = listOf(navArgument("vendorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val vendorId = backStackEntry.arguments?.read { getStringOrNull("vendorId") } ?: ""
                VendorProfileScreen(
                    vendorId = vendorId,
                    onBack = { navController.popBackStack() },
                    onTrackClick = { vid ->
                        navController.navigate(NavDestination.ConsumerVendorTracking.createRoute(vid))
                    }
                )
            }
            composable(
                route = NavDestination.ConsumerVendorTracking.route,
                arguments = listOf(navArgument("vendorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val vendorId = backStackEntry.arguments?.read { getStringOrNull("vendorId") } ?: ""
                VendorTrackingScreen(
                    vendorId = vendorId,
                    onBack = { navController.popBackStack() },
                    onProfileClick = { id ->
                        navController.navigate(NavDestination.ConsumerVendorProfile.createRoute(id))
                    }
                )
            }
            composable(NavDestination.ConsumerProfile.route) {
                ConsumerProfileScreen(
                    onFavoritesClick = { navController.navigate(NavDestination.ConsumerFavorites.route) },
                    onLogout = {
                        navController.navigate(NavDestination.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(NavDestination.ConsumerFavorites.route) {
                FavoritesScreen(
                    onBack = { navController.popBackStack() },
                    onVendorClick = { vendorId ->
                        navController.navigate(NavDestination.ConsumerVendorProfile.createRoute(vendorId))
                    }
                )
            }
            composable(NavDestination.VendorDashboard.route) {
                VendorDashboardScreen(
                    onStartRoute = { navController.navigate(NavDestination.VendorRoute.route) }
                )
            }
            composable(NavDestination.VendorRoute.route) {
                VendorRouteScreen(
                    onStopRoute = { navController.navigate(NavDestination.VendorDashboard.route) },
                    onStartRoute = { navController.navigate("err_config") }
                )
            }

            composable("err_config") {
                org.example.project.features.vendor.err.RouteConfigScreen(
                    onBack = { navController.popBackStack() },
                    onRouteStarted = { routeId ->
                        navController.navigate("err_active_route/$routeId") {
                            popUpTo(NavDestination.VendorDashboard.route) { inclusive = false }
                        }
                    }
                )
            }

            composable(
                route = "err_active_route/{routeId}",
                arguments = listOf(androidx.navigation.navArgument("routeId") { type = androidx.navigation.NavType.StringType })
            ) { backStackEntry ->
                val rId = backStackEntry.arguments?.read { getStringOrNull("routeId") } ?: return@composable
                org.example.project.features.vendor.err.ActiveRouteScreen(
                    routeId = rId,
                    onStopRoute = {
                        navController.navigate(NavDestination.VendorDashboard.route) {
                            popUpTo(NavDestination.VendorDashboard.route) { inclusive = true }
                        }
                    }
                )
            }
            // ----------------------------------------
            composable(NavDestination.VendorHeatmap.route) {
                HeatmapScreen()
            }
            composable(NavDestination.VendorSettings.route) {
                VendorSettingsScreen(
                    onSubscriptionClick = { navController.navigate(NavDestination.VendorSubscription.route) },
                    onLogout = {
                        navController.navigate(NavDestination.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEditProducts = { navController.navigate(NavDestination.VendorProducts.route) }
                )
            }
            composable(NavDestination.VendorSubscription.route) {
                SubscriptionScreen(
                    onBack = { navController.popBackStack() },
                    onSubscribe = {
                        navController.navigate(NavDestination.VendorDashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(NavDestination.VendorProducts.route) {
                VendorProductsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
