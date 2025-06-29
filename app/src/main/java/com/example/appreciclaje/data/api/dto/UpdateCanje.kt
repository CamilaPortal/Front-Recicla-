package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActualizarPuntosRequest(
    val puntos: Int
)

@Serializable
data class ActualizarEstadoRequest(
    val is_active: Boolean
)