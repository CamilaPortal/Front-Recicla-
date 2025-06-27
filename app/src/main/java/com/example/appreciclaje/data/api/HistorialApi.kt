package com.example.appreciclaje.data.api

import android.util.Log
import com.example.appreciclaje.data.api.dto.HistorialCanjeResponse
import com.example.appreciclaje.data.api.dto.ReciclajeHistorialResponse
import com.example.appreciclaje.data.api.dto.UserDataResponse
import com.example.appreciclaje.network.NetworkUtils
import com.example.appreciclaje.network.SessionManager
import com.example.appreciclaje.network.UrlConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HistorialApi {
    private const val TAG = "HistorialApi"

    suspend fun getUserData(): Result<UserDataResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()

                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.get("${UrlConfig.BASE_URL}/usuario/perfil/") {
                    header("Authorization", "Bearer $token")
                }

                if (!httpResponse.status.isSuccess()) {
                    Log.d(TAG, "Recibido código HTTP: ${httpResponse.status}")
                    return@withContext Result.failure(Exception("Error en el servidor: ${httpResponse.status}"))
                }

                val response = httpResponse.body<UserDataResponse>()
                Result.success(response)
            } catch (e: ClientRequestException) {
                Log.e(TAG, "ClientRequestException: ${e.response.status}", e)
                val result = when (e.response.status) {
                    HttpStatusCode.Unauthorized -> "Sesión expirada o inválida"
                    HttpStatusCode.NotFound -> "Usuario no encontrado"
                    else -> "Error de servidor: ${e.response.status}"
                }
                Result.failure(Exception(result))
            } catch (e: Exception) {
                Log.e(TAG, "Excepción general", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde"))
            }
        }
    }

    suspend fun getHistorialReciclaje(): Result<List<ReciclajeHistorialResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()

                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.get("${UrlConfig.BASE_URL}/historial-canje/") {
                    header("Authorization", "Bearer $token")
                }

                if (!httpResponse.status.isSuccess()) {
                    Log.d(TAG, "Recibido código HTTP: ${httpResponse.status}")
                    return@withContext Result.failure(Exception("Error en el servidor: ${httpResponse.status}"))
                }

                val response = httpResponse.body<List<ReciclajeHistorialResponse>>()
                Result.success(response)
            } catch (e: ClientRequestException) {
                Log.e(TAG, "ClientRequestException: ${e.response.status}", e)
                val result = when (e.response.status) {
                    HttpStatusCode.Unauthorized -> "Sesión expirada o inválida"
                    HttpStatusCode.NotFound -> "No se encontró historial de reciclajes"
                    else -> "Error de servidor: ${e.response.status}"
                }
                Result.failure(Exception(result))
            } catch (e: Exception) {
                Log.e(TAG, "Excepción general", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde"))
            }
        }
    }

    suspend fun getHistorialCanje(): Result<List<HistorialCanjeResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()

                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.get("${UrlConfig.BASE_URL}/historial-canje/") {
                    header("Authorization", "Bearer $token")
                }

                if (!httpResponse.status.isSuccess()) {
                    Log.d(TAG, "Recibido código HTTP: ${httpResponse.status}")
                    return@withContext Result.failure(Exception("Error en el servidor: ${httpResponse.status}"))
                }

                val response = httpResponse.body<List<HistorialCanjeResponse>>()
                Result.success(response)
            } catch (e: ClientRequestException) {
                Log.e(TAG, "ClientRequestException: ${e.response.status}", e)
                val result = when (e.response.status) {
                    HttpStatusCode.Unauthorized -> "Sesión expirada o inválida"
                    HttpStatusCode.NotFound -> "No se encontró historial de canjes"
                    else -> "Error de servidor: ${e.response.status}"
                }
                Result.failure(Exception(result))
            } catch (e: Exception) {
                Log.e(TAG, "Excepción general", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde"))
            }
        }
    }
}