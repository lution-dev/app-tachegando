package org.example.project.core.navigation

sealed class NavDestination(val route: String, val title: String) {
    // Common
    object Splash : NavDestination("splash", "Splash")
    object Onboarding : NavDestination("onboarding", "Início")
    object Login : NavDestination("login", "Login")
    object ForgotPassword : NavDestination("forgot_password", "Recuperar Senha")
    object RegisterType : NavDestination("register_type", "Tipo de Conta")
    object ConsumerRegister : NavDestination("consumer_register", "Cadastro Consumidor")
    object VendorRegister : NavDestination("vendor_register", "Cadastro Vendedor")
    object Notifications : NavDestination("notifications", "Notificações")

    // Consumer
    object ConsumerHome : NavDestination("consumer_home", "Mapa")
    object ConsumerCategories : NavDestination("consumer_categories", "Buscar")
    object ConsumerVendorList : NavDestination("consumer_vendor_list/{category}", "Vendedores") {
        fun createRoute(category: String) = "consumer_vendor_list/$category"
    }
    object ConsumerVendorProfile : NavDestination("consumer_vendor_profile/{vendorId}", "Vendedor") {
        fun createRoute(vendorId: String) = "consumer_vendor_profile/$vendorId"
    }
    object ConsumerVendorTracking : NavDestination("consumer_vendor_tracking/{vendorId}", "Acompanhar") {
        fun createRoute(vendorId: String) = "consumer_vendor_tracking/$vendorId"
    }
    object ConsumerProfile : NavDestination("consumer_profile", "Perfil")
    object ConsumerFavorites : NavDestination("consumer_favorites", "Favoritos")

    // Vendor
    object VendorDashboard : NavDestination("vendor_dashboard", "Início")
    object VendorRoute : NavDestination("vendor_route", "Rota")
    object VendorHeatmap : NavDestination("vendor_heatmap", "Demanda")
    object VendorSettings : NavDestination("vendor_settings", "Ajustes")
    object VendorSubscription : NavDestination("vendor_subscription", "Assinatura")
    object VendorProducts : NavDestination("vendor_products", "Editar Produtos")
}
