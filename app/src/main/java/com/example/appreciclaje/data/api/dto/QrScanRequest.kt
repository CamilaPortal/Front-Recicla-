package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class QrScanRequest(
    val qr_token_value: String
)