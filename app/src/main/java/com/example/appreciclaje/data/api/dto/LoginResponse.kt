package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val access_token: String,
    val dni: Int,
    val email: String,
    val rol: String
)