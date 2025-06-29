package com.example.appreciclaje.data.api

import android.util.Log
import com.example.appreciclaje.data.api.dto.ChangePasswordRequest
import com.example.appreciclaje.data.api.dto.ConfirmarEntregaRequest
import com.example.appreciclaje.data.api.dto.ConfirmarEntregaResponse
import com.example.appreciclaje.data.api.dto.EmpresaProfileResponse
import com.example.appreciclaje.data.api.dto.ErrorValidarResponse
import com.example.appreciclaje.data.api.dto.ValidarQrRequest
import com.example.appreciclaje.data.api.dto.ValidarQrResponse
import com.example.appreciclaje.network.NetworkUtils
import com.example.appreciclaje.network.SessionManager
import com.example.appreciclaje.network.UrlConfig
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EmpresaApi {

    private const val TAG = "EmpresaApi"

    suspend fun getProfile(): Result<EmpresaProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.get("${UrlConfig.BASE_URL}/empresa/mi-perfil/") {
                    header("Authorization", "Bearer $token")
                }

                if (httpResponse.status.isSuccess()) {
                    val response = httpResponse.body<EmpresaProfileResponse>()
                    Result.success(response)
                } else {
                    Log.e(TAG, "Error al obtener el perfil: ${httpResponse.status}")
                    Result.failure(Exception("Error al obtener el perfil de la empresa."))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en getProfile", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde."))
            }
        }
    }

    suspend fun changePassword(passwordData: ChangePasswordRequest): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.put("${UrlConfig.BASE_URL}/empresa/cambiar-password/") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(passwordData)
                }

                when (httpResponse.status) {
                    HttpStatusCode.OK -> Result.success("Contraseña cambiada con éxito.")
                    HttpStatusCode.Unauthorized -> Result.failure(Exception("La contraseña actual es incorrecta."))
                    HttpStatusCode.BadRequest -> Result.failure(Exception("La nueva contraseña debe ser diferente a la actual."))
                    else -> {
                        Log.e(TAG, "Error al cambiar la contraseña: ${httpResponse.status}")
                        Result.failure(Exception("Error al cambiar la contraseña."))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en changePassword", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde."))
            }
        }
    }

    suspend fun validarQr(qrCode: String): Result<ValidarQrResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.post("${UrlConfig.BASE_URL}/empresa/validar-qr/") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(ValidarQrRequest(codigo_qr = qrCode))
                }

                if (httpResponse.status.isSuccess()) {
                    val response = httpResponse.body<ValidarQrResponse>()
                    if (response.valid) {
                        Result.success(response)
                    } else {
                        Result.failure(Exception(response.message))
                    }
                } else {
                    val errorBody = httpResponse.body<ErrorValidarResponse>()
                    Log.e(TAG, "Error al validar QR: ${httpResponse.status} - ${errorBody.detail}")
                    Result.failure(Exception(errorBody.detail))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en validarQr", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde."))
            }
        }
    }

    suspend fun confirmarEntrega(qrCode: String): Result<ConfirmarEntregaResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.post("${UrlConfig.BASE_URL}/empresa/confirmar-entrega/") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(ConfirmarEntregaRequest(codigo_qr = qrCode))
                }

                if (httpResponse.status.isSuccess()) {
                    val response = httpResponse.body<ConfirmarEntregaResponse>()
                    Result.success(response)
                } else {
                    val errorBody = httpResponse.body<ErrorValidarResponse>()
                    Log.e(TAG, "Error al confirmar entrega: ${httpResponse.status} - ${errorBody.detail}")
                    Result.failure(Exception(errorBody.detail))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en confirmarEntrega", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde."))
            }
        }
    }
}