package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CanjesEmpresaResponse(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val puntos: Int,
    val empresa_id: Int,
    val empresa_nombre: String,
    val is_active: Boolean,
    val stock_inicial: Int,
    val stock_actual: Int
)