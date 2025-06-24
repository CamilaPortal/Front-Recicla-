package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RankingHistoricoItem(
    val posicion: Int,
    val alias: String,
    val nombre: String,
    val apellido: String,
    val puntos_disponibles: Int,
    val total_puntos_ganados: Int,
    val total_reciclajes_realizados: Int
)