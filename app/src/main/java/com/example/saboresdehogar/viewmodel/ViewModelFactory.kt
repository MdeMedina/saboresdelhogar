package com.example.saboresdehogar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.saboresdehogar.data.repository.*
import com.example.saboresdehogar.data.source.local.JsonDataSource
import com.example.saboresdehogar.data.source.local.LocalDataSource

/**
 * Factory para crear ViewModels con dependencias
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    // Inicializar data sources
    private val jsonDataSource = JsonDataSource(context)
    private val localDataSource = LocalDataSource(jsonDataSource)

    // Inicializar repositories
    private val menuRepository = MenuRepository(localDataSource)
    private val authRepository = AuthRepository(localDataSource)
    private val cartRepository = CartRepository(localDataSource)
    private val orderRepository = OrderRepository(localDataSource, cartRepository)
    private val userRepository = UserRepository(localDataSource)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> {
                MenuViewModel(menuRepository) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(cartRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> {
                OrderViewModel(orderRepository, cartRepository) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}