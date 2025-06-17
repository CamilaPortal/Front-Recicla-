package com.example.appreciclaje.network

object SessionManager {
    private var accessToken: String? = null
    private var userDni: Int? = null
    private var userEmail: String? = null
    private var userRol: String? = null

    fun saveLoginData(token: String, dni: Int, email: String, rol: String) {
        accessToken = token
        userDni = dni
        userEmail = email
        userRol = rol
    }

    fun getToken(): String? = accessToken

    fun getDni(): Int? = userDni

    fun getEmail(): String? = userEmail

    fun getRol(): String? = userRol

    fun isLoggedIn(): Boolean = accessToken != null

    fun clearSession() {
        accessToken = null
        userDni = null
        userEmail = null
        userRol = null
    }
}