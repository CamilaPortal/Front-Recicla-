package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class HistorialCanjeResponse(
    val id: Int,
    val puntos_usados: Int,
    val fecha_canje: String,
    val usuario_dni: Int,
    val canje_id: Int,
    val canje_nombre: String,
    val canje_descripcion: String
)