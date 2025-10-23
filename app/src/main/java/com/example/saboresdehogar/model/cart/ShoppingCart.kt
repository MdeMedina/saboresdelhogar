package com.example.saboresdehogar.model.cart

import com.example.saboresdehogar.model.menu.MenuItem

data class ShoppingCart(
    val items: MutableList<CartItem> = mutableListOf()
) {
    val total: Double
        get() = items.sumOf { it.subtotal }

    val itemCount: Int
        get() = items.sumOf { it.quantity }

    fun addItem(menuItem: MenuItem) {
        val existingItem = items.find { it.menuItem.id == menuItem.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            items.add(CartItem(menuItem, 1))
        }
    }

    fun removeItem(menuItemId: String) {
        items.removeAll { it.menuItem.id == menuItemId }
    }

    fun updateQuantity(menuItemId: String, quantity: Int) {
        items.find { it.menuItem.id == menuItemId }?.quantity = quantity
    }

    fun clear() {
        items.clear()
    }
}
