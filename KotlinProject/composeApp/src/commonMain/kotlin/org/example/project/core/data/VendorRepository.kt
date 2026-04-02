package org.example.project.core.data

data class Vendor(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val distance: String,
    val rating: String,
    val reviewCount: Int,
    val isOnline: Boolean,
    val products: List<Product>
)

data class Product(
    val id: String,
    val name: String,
    val price: String
)

object VendorRepository {
    val vendors = listOf(
        Vendor("1", "Seu Carlos - Ovos Caipiras", "Ovos frescos direto da granja", "Ovos", "0.3 km", "4.9", 87, true,
            listOf(Product("p1", "Ovos Brancos (dúzia)", "R$ 12,00"), Product("p2", "Ovos Caipiras (dúzia)", "R$ 15,00"), Product("p3", "Ovos de Codorna (cartela)", "R$ 8,00"))
        ),
        Vendor("2", "Dona Maria - Ovos Orgânicos", "Ovos orgânicos certificados", "Ovos", "0.8 km", "4.7", 45, true,
            listOf(Product("p4", "Ovos Orgânicos (dúzia)", "R$ 18,00"), Product("p5", "Ovos de Pata (meia dúzia)", "R$ 14,00"))
        ),
        Vendor("3", "Granja São José", "Ovos brancos e vermelhos", "Ovos", "1.5 km", "4.5", 32, false,
            listOf(Product("p6", "Ovos Vermelhos (dúzia)", "R$ 10,00"), Product("p7", "Ovos Brancos (dúzia)", "R$ 9,00"))
        ),
        Vendor("4", "Banca do Seu João", "Frutas da estação fresquinhas", "Frutas", "0.2 km", "4.8", 120, true,
            listOf(Product("p8", "Banana Prata (kg)", "R$ 5,00"), Product("p9", "Maçã (kg)", "R$ 8,00"), Product("p10", "Laranja (kg)", "R$ 4,00"))
        ),
        Vendor("5", "Frutas da Horta", "Frutas orgânicas e sucos naturais", "Frutas", "0.6 km", "4.6", 67, true,
            listOf(Product("p11", "Morango (caixa)", "R$ 12,00"), Product("p12", "Suco Natural 500ml", "R$ 7,00"))
        ),
        Vendor("6", "Cesta da Vovó", "Cestas de frutas variadas", "Frutas", "1.1 km", "4.4", 28, false,
            listOf(Product("p13", "Cesta Pequena", "R$ 25,00"), Product("p14", "Cesta Grande", "R$ 45,00"))
        ),
        Vendor("7", "Pão Quentinho", "Pães artesanais frescos", "Padaria", "0.4 km", "5.0", 200, true,
            listOf(Product("p15", "Pão Francês (6 un)", "R$ 6,00"), Product("p16", "Pão de Queijo (10 un)", "R$ 12,00"), Product("p17", "Bolo de Chocolate", "R$ 35,00"))
        ),
        Vendor("8", "Padaria do Zé", "Pães, bolos e salgados", "Padaria", "0.7 km", "4.3", 55, true,
            listOf(Product("p18", "Pão Integral (kg)", "R$ 14,00"), Product("p19", "Coxinha (un)", "R$ 5,00"), Product("p20", "Sonho (un)", "R$ 6,00"))
        ),
        Vendor("9", "Doce Trigo", "Pães integrais e sem glúten", "Padaria", "1.3 km", "4.8", 91, false,
            listOf(Product("p21", "Pão Sem Glúten (kg)", "R$ 18,00"), Product("p22", "Torta de Limão", "R$ 40,00"))
        ),
        Vendor("10", "Limpa Tudo Express", "Produtos de limpeza ecológicos", "Produtos de Limpeza", "0.5 km", "4.6", 38, true,
            listOf(Product("p23", "Detergente Ecológico", "R$ 4,00"), Product("p24", "Kit Limpeza Completo", "R$ 25,00"))
        ),
        Vendor("11", "Casa Limpa", "Kit completo de limpeza", "Produtos de Limpeza", "0.9 km", "4.2", 22, false,
            listOf(Product("p25", "Desinfetante (2L)", "R$ 8,00"), Product("p26", "Esponja (3 un)", "R$ 5,00"))
        ),
        Vendor("12", "Tapioca da Tia", "Tapiocas recheadas artesanais", "Lanches", "0.1 km", "4.9", 180, true,
            listOf(Product("p27", "Tapioca de Queijo", "R$ 8,00"), Product("p28", "Tapioca de Frango", "R$ 10,00"), Product("p29", "Tapioca Doce", "R$ 9,00"))
        ),
        Vendor("13", "Dogão do Manoel", "Hot dogs e hambúrgueres", "Lanches", "0.4 km", "4.7", 95, true,
            listOf(Product("p30", "Hot Dog Completo", "R$ 10,00"), Product("p31", "X-Burger", "R$ 15,00"))
        ),
        Vendor("14", "Açaí da Amazônia", "Açaí e bowls naturais", "Lanches", "0.6 km", "4.8", 140, true,
            listOf(Product("p32", "Açaí 300ml", "R$ 12,00"), Product("p33", "Açaí 500ml", "R$ 18,00"), Product("p34", "Bowl Detox", "R$ 20,00"))
        ),
        Vendor("15", "Coxinha do Vô", "Salgados fritos na hora", "Lanches", "1.0 km", "4.5", 60, false,
            listOf(Product("p35", "Coxinha (un)", "R$ 5,00"), Product("p36", "Risole (un)", "R$ 5,00"), Product("p37", "Bolinha de Queijo (un)", "R$ 4,00"))
        ),
        Vendor("16", "Doces da Dona Lúcia", "Brigadeiros e doces finos", "Doces", "0.3 km", "4.9", 155, true,
            listOf(Product("p38", "Brigadeiro (un)", "R$ 3,00"), Product("p39", "Caixa de Bombom (12 un)", "R$ 30,00"), Product("p40", "Pudim", "R$ 20,00"))
        ),
        Vendor("17", "Bolo da Vizinha", "Bolos caseiros e tortas", "Doces", "0.7 km", "4.6", 78, true,
            listOf(Product("p41", "Bolo de Cenoura", "R$ 25,00"), Product("p42", "Torta de Maçã", "R$ 30,00"))
        ),
        Vendor("18", "Churros & Cia", "Churros recheados quentinhos", "Doces", "1.2 km", "4.4", 42, false,
            listOf(Product("p43", "Churro Doce de Leite", "R$ 6,00"), Product("p44", "Churro Chocolate", "R$ 6,00"))
        )
    )

    fun getById(id: String): Vendor? = vendors.find { it.id == id }

    fun getByCategory(category: String): List<Vendor> = vendors.filter { it.category == category }

    val favorites = listOf("1", "4", "12", "16")

    val notifications = listOf(
        "Tapioca da Tia" to "está a 0.1 km de você!",
        "Pão Quentinho" to "acabou de ficar online!",
        "Doces da Dona Lúcia" to "tem um novo produto!",
        "Banca do Seu João" to "está chegando em 3 min!",
        "Açaí da Amazônia" to "promoção: Açaí 500ml por R$15!",
        "Dogão do Manoel" to "está a 0.4 km de você!",
        "Dona Maria - Ovos Orgânicos" to "voltou a vender!",
        "Pão Quentinho" to "pão fresco saindo do forno!"
    )
}
