package com.example.appreciclaje.data.api

import android.util.Log
import com.example.appreciclaje.data.api.dto.CanjeDisponibleResponse
import com.example.appreciclaje.network.NetworkUtils
import com.example.appreciclaje.network.SessionManager
import com.example.appreciclaje.network.UrlConfig
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

object CanjesApi {

    private const val TAG = "CanjesApi"

    suspend fun getCanjesDisponibles(): Result<List<CanjeDisponibleResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.get("${UrlConfig.BASE_URL}/canjes/disponibles/") {
                    header("Authorization", "Bearer $token")
                }

                if (!httpResponse.status.isSuccess()) {
                    return@withContext Result.failure(Exception("Error en el servidor: ${httpResponse.status}"))
                }

                val response = httpResponse.body<List<CanjeDisponibleResponse>>()
                Result.success(response)
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en getCanjesDisponibles", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde"))
            }
        }
    }

    suspend fun realizarCanje(canjeId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.post("${UrlConfig.BASE_URL}/historial-canje/realizar-canje/") {
                    url {
                        parameters.append("canje_id", canjeId.toString())
                    }
                    header("Authorization", "Bearer $token")
                }

                if (httpResponse.status.isSuccess()) {
                    Result.success(Unit)
                } else {
                    val errorBody = httpResponse.bodyAsText()
                    val errorMessage = try {
                        val errorJson = Json.decodeFromString<JsonObject>(errorBody)
                        errorJson["detail"]?.jsonPrimitive?.content ?: "Error del servidor."
                    } catch (e: Exception) {
                        Log.e(TAG, "Fallo al parsear el cuerpo del error: $errorBody", e)
                        "No se pudo realizar el canje."
                    }
                    Log.e(TAG, "Error al realizar canje: ${httpResponse.status} - $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en realizarCanje", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde"))
            }
        }
    }
}
