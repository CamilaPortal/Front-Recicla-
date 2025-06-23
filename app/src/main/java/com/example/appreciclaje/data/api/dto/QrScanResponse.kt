package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class QrScanResponse(
    val success: Boolean,
    val message: String? = "Reciclaje confirmado exitosamente",
)