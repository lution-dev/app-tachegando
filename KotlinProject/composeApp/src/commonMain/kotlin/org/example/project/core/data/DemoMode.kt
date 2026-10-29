package org.example.project.core.data

import org.example.project.getPlatform

/**
 * Modo de demonstração para testes: só fica disponível na versão web e serve dados fictícios
 * no lugar do Supabase, sem exigir login.
 */
object DemoMode {
    val isAvailable: Boolean = getPlatform().name.startsWith("Web")

    var isActive: Boolean = false

    val vendors = listOf(
        Vendor(
            id = "1", name = "Tapioca da Tia", description = "Tapiocas recheadas na hora.",
            category = "Comida", distance = "0.1 km", rating = "4.8", reviewCount = 124, isOnline = true,
            latitude = -23.5613, longitude = -46.6565,
            products = listOf(Product("p1", "Tapioca de queijo", "R$ 8,00"), Product("p2", "Tapioca de coco", "R$ 7,00"))
        ),
        Vendor(
            id = "2", name = "Pão Quentinho", description = "Pão fresco saindo do forno.",
            category = "Comida", distance = "0.3 km", rating = "4.6", reviewCount = 87, isOnline = true,
            latitude = -23.5625, longitude = -46.6540,
            products = listOf(Product("p3", "Pão francês (6 un.)", "R$ 6,00"))
        ),
        Vendor(
            id = "3", name = "Dona Maria - Ovos Orgânicos", description = "Ovos caipiras e hortaliças.",
            category = "Hortifruti", distance = "0.6 km", rating = "4.9", reviewCount = 56, isOnline = false,
            latitude = -23.5590, longitude = -46.6600,
            products = listOf(Product("p4", "Dúzia de ovos", "R$ 14,00"))
        ),
        Vendor(
            id = "4", name = "Açaí da Amazônia", description = "Açaí 500ml com complementos.",
            category = "Bebidas", distance = "0.4 km", rating = "4.7", reviewCount = 203, isOnline = true,
            latitude = -23.5640, longitude = -46.6580,
            products = listOf(Product("p5", "Açaí 500ml", "R$ 16,00"))
        ),
        Vendor(
            id = "5", name = "Doces da Dona Lúcia", description = "Brigadeiros e cocadas artesanais.",
            category = "Doces", distance = "0.8 km", rating = "5.0", reviewCount = 41, isOnline = true,
            latitude = -23.5600, longitude = -46.6520,
            products = listOf(Product("p6", "Brigadeiro (cx. 6)", "R$ 12,00"))
        )
    )
}
