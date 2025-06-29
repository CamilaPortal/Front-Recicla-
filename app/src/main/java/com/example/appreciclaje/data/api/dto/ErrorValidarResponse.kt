package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorValidarResponse (
    val detail: String
)