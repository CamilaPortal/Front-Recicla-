package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CanjeDisponibleResponse(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val puntos: Int,
    val empresa_nombre: String,
)