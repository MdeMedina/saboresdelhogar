package com.example.saboresdehogar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.saboresdehogar.model.auth.AuthResponse
import com.example.saboresdehogar.model.auth.LoginCredentials
import com.example.saboresdehogar.model.auth.RegisterRequest
import com.example.saboresdehogar.model.auth.UserSession
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.data.repository.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _authResponse = MutableLiveData<AuthResponse?>()
    val authResponse: LiveData<AuthResponse?> = _authResponse

    init {
        checkLoginStatus()
    }

    /**
     * Verifica si hay sesión activa
     */
    fun checkLoginStatus() {
        val isLogged = authRepository.isLoggedIn()
        _isLoggedIn.value = isLogged

        if (isLogged) {
            _currentUser.value = authRepository.getCurrentUser()
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Realiza el login
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authResponse.value = AuthResponse(
                success = false,
                message = "Email y contraseña son requeridos",
                errorCode = "EMPTY_FIELDS"
            )
            return
        }

        _authState.value = AuthState.Loading

        val credentials = LoginCredentials(email, password)
        val response = authRepository.login(credentials)

        _authResponse.value = response

        if (response.success) {
            _currentUser.value = response.user
            _isLoggedIn.value = true
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Error(response.message ?: "Error al iniciar sesión")
        }
    }

    /**
     * Registra un nuevo usuario
     */
    fun register(email: String, password: String, name: String, phone: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank() || phone.isBlank()) {
            _authResponse.value = AuthResponse(
                success = false,
                message = "Todos los campos son requeridos",
                errorCode = "EMPTY_FIELDS"
            )
            return
        }

        _authState.value = AuthState.Loading

        val request = RegisterRequest(email, password, name, phone)
        val response = authRepository.register(request)

        _authResponse.value = response

        if (response.success) {
            _currentUser.value = response.user
            _isLoggedIn.value = true
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Error(response.message ?: "Error al registrarse")
        }
    }

    /**
     * Cierra sesión
     */
    fun logout() {
        authRepository.logout()
        _currentUser.value = null
        _isLoggedIn.value = false
        _authState.value = AuthState.Unauthenticated
        _authResponse.value = null
    }

    /**
     * Limpia el mensaje de respuesta
     */
    fun clearAuthResponse() {
        _authResponse.value = null
    }

    /**
     * Obtiene la sesión actual
     */
    fun getCurrentSession(): UserSession? {
        return authRepository.getCurrentSession()
    }
}

/**
 * Estados de autenticación
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}
