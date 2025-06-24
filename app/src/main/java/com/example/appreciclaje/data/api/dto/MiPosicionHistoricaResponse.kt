package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class MiPosicionHistoricaResponse(
    val mi_posicion_historica: Int? = null,
    val total_usuarios_activos: Int,
    val mis_puntos_disponibles: Int,
    val mis_puntos_historicos: Int,
    val mis_reciclajes_realizados: Int,
    val porcentaje_superior: Double? = null,
    val mensaje: String? = null
)