package com.example.appreciclaje.data.api.dto

data class UserMovement(
    val id: Int,
    val date: String,
    val description: String,
    val points: Int,
    val isPositive: Boolean
)