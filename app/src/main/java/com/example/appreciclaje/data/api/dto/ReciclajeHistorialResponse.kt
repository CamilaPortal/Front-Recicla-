package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReciclajeHistorialResponse(
    val id: Int,
    val puntos: Int,
    val fecha_reciclaje: String,
    val peso: Float,
    val cantidad_botellas: Int,
    val id_cesto: String
)