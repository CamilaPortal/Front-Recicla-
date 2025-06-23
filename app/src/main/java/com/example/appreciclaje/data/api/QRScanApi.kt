package com.example.appreciclaje.data.api

import android.util.Log
import com.example.appreciclaje.data.api.dto.QrScanResponse
import com.example.appreciclaje.data.api.dto.QrScanRequest
import com.example.appreciclaje.network.NetworkUtils
import com.example.appreciclaje.network.SessionManager
import com.example.appreciclaje.network.UrlConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object QrScanApi {
    private const val TAG = "QrScanApi"

    suspend fun confirmScan(qrToken: String): Result<QrScanResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()

                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión nuevamente."))
                }

                val httpResponse = NetworkUtils.httpClient.post("${UrlConfig.BASE_URL}/reciclaje-app/confirmar-escaneo/") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $token")
                    setBody(QrScanRequest(qr_token_value = qrToken))
                }

                Log.d(TAG, "Código HTTP recibido: ${httpResponse.status.value} (${httpResponse.status.description})")

                when (httpResponse.status.value) {
                    in 200..299 -> {
                        if (httpResponse.status.value == 201) {
                            return@withContext Result.success(
                                QrScanResponse(
                                    success = true,
                                    message = "¡Reciclaje confirmado exitosamente!",
                                )
                            )
                        }

                        try {
                            val response = httpResponse.body<QrScanResponse>()
                            return@withContext Result.success(response)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al parsear la respuesta", e)
                            return@withContext Result.success(
                                QrScanResponse(
                                    success = true,
                                    message = "Reciclaje confirmado exitosamente",
                                )
                            )
                        }
                    }
                    400 -> return@withContext Result.failure(Exception("El formato del código QR no es válido. Intenta nuevamente."))
                    401, 403 -> return@withContext Result.failure(Exception("Tu sesión ha expirado. Por favor, inicia sesión nuevamente."))
                    404 -> return@withContext Result.failure(Exception("El código QR escaneado no es válido o ha expirado."))
                    410 -> return@withContext Result.failure(Exception("Este código QR ya ha sido utilizado anteriormente."))
                    500, 502, 503, 504 -> return@withContext Result.failure(Exception("Hay un problema con el servidor. Intenta más tarde."))
                    else -> return@withContext Result.failure(Exception("Ha ocurrido un error inesperado. Por favor, intenta nuevamente."))
                }
            } catch (e: ClientRequestException) {
                Log.e(TAG, "ClientRequestException: ${e.response.status}", e)

                val errorMessage = when (e.response.status.value) {
                    400 -> "El formato del código QR no es válido. Intenta nuevamente."
                    401, 403 -> "Tu sesión ha expirado. Por favor, inicia sesión nuevamente."
                    404 -> "El código QR escaneado no es válido o ha expirado."
                    410 -> "Este código QR ya ha sido utilizado anteriormente."
                    500, 502, 503, 504 -> "Hay un problema con el servidor. Intenta más tarde."
                    else -> "Ha ocurrido un error inesperado. Por favor, intenta nuevamente."
                }

                return@withContext Result.failure(Exception(errorMessage))
            } catch (e: Exception) {
                Log.e(TAG, "Excepción general", e)
                return@withContext Result.failure(Exception("Error de conexión. Verifica tu internet e intenta nuevamente."))
            }
        }
    }
}