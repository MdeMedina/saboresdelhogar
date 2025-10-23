package com.example.saboresdehogar.model.menu

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: CategoryType,
    val imageUrl: String? = null,
    val isVegetarian: Boolean = false,
    val isAvailable: Boolean = true
)