package com.example.appreciclaje.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmpresaProfileResponse(
    val usuario_info: UsuarioInfo,
    val empresa_info: EmpresaInfo
)

@Serializable
data class UsuarioInfo(
    val dni: Int,
    val alias: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: Long,
    val rol: String
)

@Serializable
data class EmpresaInfo(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val direccion: String,
    val telefono: Long,
    val email: String,
    val is_active: Boolean
)