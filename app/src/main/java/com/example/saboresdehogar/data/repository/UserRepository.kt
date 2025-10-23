package com.example.saboresdehogar.data.repository
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.data.source.local.LocalDataSource

class UserRepository(private val localDataSource: LocalDataSource) {

    /**
     * Obtiene el usuario actual
     */
    fun getCurrentUser(): User? {
        return localDataSource.getUserSession()?.user
    }

    /**
     * Obtiene los items favoritos del usuario
     */
    fun getFavorites(): List<String> {
        return localDataSource.getFavorites()
    }

    /**
     * Agrega un item a favoritos
     */
    fun addToFavorites(itemId: String) {
        localDataSource.addToFavorites(itemId)
    }

    /**
     * Elimina un item de favoritos
     */
    fun removeFromFavorites(itemId: String) {
        localDataSource.removeFromFavorites(itemId)
    }

    /**
     * Verifica si un item es favorito
     */
    fun isFavorite(itemId: String): Boolean {
        return getFavorites().contains(itemId)
    }

    /**
     * Alterna el estado de favorito de un item
     */
    fun toggleFavorite(itemId: String) {
        if (isFavorite(itemId)) {
            removeFromFavorites(itemId)
        } else {
            addToFavorites(itemId)
        }
    }
}