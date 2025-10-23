package com.example.saboresdehogar.model.user

data class Address(
    val id: String,
    val street: String,
    val number: String,
    val apartment: String? = null,
    val comuna: String,
    val city: String = "Santiago",
    val reference: String? = null,
    val isDefault: Boolean = false
) {
    fun getFullAddress(): String {
        val apt = apartment?.let { ", Depto. $it" } ?: ""
        return "$street $number$apt, $comuna, $city"
    }
}