package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.model.auth.AuthResponse
import com.example.saboresdehogar.model.auth.LoginCredentials
import com.example.saboresdehogar.model.auth.RegisterRequest
import com.example.saboresdehogar.model.auth.UserSession
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.data.source.local.LocalDataSource
import java.util.UUID

class AuthRepository(private val localDataSource: LocalDataSource) {

    /**
     * Login de usuario
     */
    fun login(credentials: LoginCredentials): AuthResponse {
        val user = localDataSource.getUserByEmail(credentials.email)

        return if (user != null) {
            // En producción aquí validarías la contraseña
            // Por ahora simplemente creamos la sesión
            val token = UUID.randomUUID().toString()
            val expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 horas

            val session = UserSession(
                user = user,
                token = token,
                expiresAt = expiresAt
            )

            localDataSource.saveUserSession(session)

            AuthResponse(
                success = true,
                user = user,
                token = token,
                expiresAt = expiresAt,
                message = "Login exitoso"
            )
        } else {
            AuthResponse(
                success = false,
                message = "Usuario o contraseña incorrectos",
                errorCode = "INVALID_CREDENTIALS"
            )
        }
    }

    /**
     * Registro de nuevo usuario (CORREGIDO)
     */
    fun register(request: RegisterRequest): AuthResponse {
        // Verificar si el usuario ya existe
        val existingUser = localDataSource.getUserByEmail(request.email)

        return if (existingUser != null) {
            AuthResponse(
                success = false,
                message = "El email ya está registrado",
                errorCode = "EMAIL_EXISTS"
            )
        } else {
            val newUser = User(
                id = UUID.randomUUID().toString(),
                email = request.email,
                name = request.name,
                phone = request.phone,
                rut = request.rut,
                defaultAddress = request.address
            )

            // En una app real, aquí guardarías el usuario en la base de datos
            // Por ahora solo creamos la sesión
            val token = UUID.randomUUID().toString()
            val expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000)

            val session = UserSession(
                user = newUser,
                token = token,
                expiresAt = expiresAt
            )

            localDataSource.saveUserSession(session)

            AuthResponse(
                success = true,
                user = newUser,
                token = token,
                expiresAt = expiresAt,
                message = "Registro exitoso"
            )
        }
    }

    /**
     * Obtiene la sesión actual
     */
    fun getCurrentSession(): UserSession? {
        return localDataSource.getUserSession()
    }

    /**
     * Verifica si hay usuario logueado
     */
    fun isLoggedIn(): Boolean {
        return localDataSource.isUserLoggedIn()
    }

    /**
     * Cierra sesión
     */
    fun logout() {
        localDataSource.logout()
    }

    /**
     * Obtiene el usuario actual
     */
    fun getCurrentUser(): User? {
        return getCurrentSession()?.user
    }
}