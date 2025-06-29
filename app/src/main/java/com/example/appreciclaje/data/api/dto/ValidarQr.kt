package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ValidarQrRequest(
    val codigo_qr: String
)

@Serializable
data class ValidarQrResponse(
    val valid: Boolean,
    val message: String,
    val canje_info: CanjeInfo?,
    val usuario_info: UsuarioInfoCanje?,
    val empresa_info: EmpresaInfoCanje?,
    val instrucciones: List<String>?
)

@Serializable
data class CanjeInfo(
    val id: Int,
    val premio: String,
    val descripcion: String,
    val puntos_usados: Int,
    val fecha_canje: String,
    val fecha_vencimiento: String
)

@Serializable
data class UsuarioInfoCanje(
    val dni: Int,
    val nombre: String,
    val email: String
)

@Serializable
data class EmpresaInfoCanje(
    val nombre: String,
    val direccion: String
)