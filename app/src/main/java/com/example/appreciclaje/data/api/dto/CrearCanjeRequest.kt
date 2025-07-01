package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CrearCanjeRequest(
    val nombre: String,
    val descripcion: String,
    val puntos: Int,
    val stock_inicial: Int
)