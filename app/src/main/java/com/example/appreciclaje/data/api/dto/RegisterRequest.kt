package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val dni: Int,
    val nombre: String,
    val apellido: String,
    val alias: String,
    val telefono: String,
    val email: String,
    val password: String
)