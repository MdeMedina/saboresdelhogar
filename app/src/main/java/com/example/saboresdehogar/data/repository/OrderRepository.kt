package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.model.order.Order
import com.example.saboresdehogar.model.order.OrderType
import com.example.saboresdehogar.model.cart.ShoppingCart
import com.example.saboresdehogar.data.source.local.LocalDataSource
import java.util.UUID

class OrderRepository(
    private val localDataSource: LocalDataSource,
    private val cartRepository: CartRepository
) {

    /**
     * Crea una nueva orden
     */
    fun createOrder(
        customerName: String,
        customerPhone: String,
        orderType: OrderType,
        deliveryAddress: String? = null,
        notes: String? = null
    ): Order? {
        val cart = cartRepository.getCart()

        if (cart.items.isEmpty()) {
            return null
        }

        val order = Order(
            id = UUID.randomUUID().toString(),
            items = cart.items.toList(),
            total = cart.total,
            customerName = customerName,
            customerPhone = customerPhone,
            orderType = orderType,
            deliveryAddress = deliveryAddress,
            notes = notes,
            userId = localDataSource.getUserSession()?.user?.id
        )

        localDataSource.saveOrder(order)
        cartRepository.clearCart()

        return order
    }

    /**
     * Obtiene todas las órdenes del usuario
     */
    fun getUserOrders(): List<Order> {
        return localDataSource.getOrders()
    }

    /**
     * Obtiene una orden por ID
     */
    fun getOrderById(orderId: String): Order? {
        return localDataSource.getOrderById(orderId)
    }

    /**
     * Obtiene el historial de órdenes ordenado por fecha
     */
    fun getOrderHistory(): List<Order> {
        return getUserOrders().sortedByDescending { it.timestamp }
    }
}