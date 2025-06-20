package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDataResponse(
    val dni: Int,
    val alias: String,
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val email: String,
    val puntos_disponibles: Int,
)