package com.example.saboresdehogar.model.auth

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String
)