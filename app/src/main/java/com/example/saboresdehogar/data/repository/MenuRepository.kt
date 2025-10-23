package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.model.menu.MenuCategory
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.data.source.local.LocalDataSource

class MenuRepository(private val localDataSource: LocalDataSource) {

    /**
     * Obtiene todos los items del menú
     */
    fun getMenuItems(): List<MenuItem> {
        return localDataSource.getMenuItems() ?: emptyList()
    }

    /**
     * Obtiene items por categoría
     */
    fun getItemsByCategory(category: CategoryType): List<MenuItem> {
        return getMenuItems().filter { it.category == category }
    }

    /**
     * Obtiene el menú agrupado por categorías
     */
    fun getMenuByCategories(): List<MenuCategory> {
        val items = getMenuItems()
        return CategoryType.values().map { categoryType ->
            MenuCategory(
                type = categoryType,
                displayName = categoryType.getDisplayName(),
                items = items.filter { it.category == categoryType }
            )
        }.filter { it.items.isNotEmpty() }
    }

    /**
     * Busca un item por ID
     */
    fun getItemById(itemId: String): MenuItem? {
        return getMenuItems().firstOrNull { it.id == itemId }
    }

    /**
     * Busca items por nombre (búsqueda)
     */
    fun searchItems(query: String): List<MenuItem> {
        val lowerQuery = query.lowercase()
        return getMenuItems().filter {
            it.name.lowercase().contains(lowerQuery) ||
                    it.description.lowercase().contains(lowerQuery)
        }
    }

    /**
     * Obtiene items vegetarianos
     */
    fun getVegetarianItems(): List<MenuItem> {
        return getMenuItems().filter { it.isVegetarian }
    }

    /**
     * Obtiene items disponibles
     */
    fun getAvailableItems(): List<MenuItem> {
        return getMenuItems().filter { it.isAvailable }
    }
}
