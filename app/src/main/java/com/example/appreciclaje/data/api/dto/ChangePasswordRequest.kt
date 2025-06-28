package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val password_actual: String,
    val password_nueva: String,
    val confirmar_password: String
)