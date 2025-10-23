package com.example.saboresdehogar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.model.menu.MenuCategory
import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.data.repository.MenuRepository

class MenuViewModel(private val menuRepository: MenuRepository) : ViewModel() {

    // LiveData para observar desde la UI
    private val _menuItems = MutableLiveData<List<MenuItem>>()
    val menuItems: LiveData<List<MenuItem>> = _menuItems

    private val _menuCategories = MutableLiveData<List<MenuCategory>>()
    val menuCategories: LiveData<List<MenuCategory>> = _menuCategories

    private val _selectedCategory = MutableLiveData<CategoryType?>()
    val selectedCategory: LiveData<CategoryType?> = _selectedCategory

    private val _searchResults = MutableLiveData<List<MenuItem>>()
    val searchResults: LiveData<List<MenuItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadMenu()
    }

    /**
     * Carga todos los items del menú
     */
    fun loadMenu() {
        _isLoading.value = true
        try {
            val items = menuRepository.getMenuItems()
            _menuItems.value = items
            _menuCategories.value = menuRepository.getMenuByCategories()
            _error.value = null
        } catch (e: Exception) {
            _error.value = "Error al cargar el menú: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Filtra por categoría
     */
    fun filterByCategory(category: CategoryType?) {
        _selectedCategory.value = category
        _menuItems.value = if (category != null) {
            menuRepository.getItemsByCategory(category)
        } else {
            menuRepository.getMenuItems()
        }
    }

    /**
     * Busca items por nombre o descripción
     */
    fun searchItems(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        _searchResults.value = menuRepository.searchItems(query)
    }

    /**
     * Limpia la búsqueda
     */
    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    /**
     * Obtiene un item por ID
     */
    fun getItemById(itemId: String): MenuItem? {
        return menuRepository.getItemById(itemId)
    }

    /**
     * Obtiene items vegetarianos
     */
    fun loadVegetarianItems() {
        _menuItems.value = menuRepository.getVegetarianItems()
    }

    /**
     * Obtiene items disponibles
     */
    fun loadAvailableItems() {
        _menuItems.value = menuRepository.getAvailableItems()
    }
}