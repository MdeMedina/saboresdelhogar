package com.example.saboresdehogar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.data.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _favorites = MutableLiveData<List<String>>()
    val favorites: LiveData<List<String>> = _favorites

    init {
        loadCurrentUser()
        loadFavorites()
    }

    /**
     * Carga el usuario actual
     */
    fun loadCurrentUser() {
        _currentUser.value = userRepository.getCurrentUser()
    }

    /**
     * Carga los favoritos del usuario
     */
    fun loadFavorites() {
        _favorites.value = userRepository.getFavorites()
    }

    /**
     * Agrega un item a favoritos
     */
    fun addToFavorites(itemId: String) {
        userRepository.addToFavorites(itemId)
        loadFavorites()
    }

    /**
     * Elimina un item de favoritos
     */
    fun removeFromFavorites(itemId: String) {
        userRepository.removeFromFavorites(itemId)
        loadFavorites()
    }

    /**
     * Alterna el estado de favorito
     */
    fun toggleFavorite(itemId: String) {
        userRepository.toggleFavorite(itemId)
        loadFavorites()
    }

    /**
     * Verifica si un item es favorito
     */
    fun isFavorite(itemId: String): Boolean {
        return userRepository.isFavorite(itemId)
    }
}
