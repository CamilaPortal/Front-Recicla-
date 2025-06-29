package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmarEntregaRequest(
    val codigo_qr: String
)

@Serializable
data class ConfirmarEntregaResponse(
    val success: Boolean,
    val message: String,
    val entrega_confirmada: EntregaConfirmadaInfo?,
    val qr_status: String?
)

@Serializable
data class EntregaConfirmadaInfo(
    val premio: String,
    val usuario: String,
    val dni_usuario: Int,
    val fecha_entrega: String,
    val puntos_canjeados: Int
)