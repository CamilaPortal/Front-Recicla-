package com.example.appreciclaje.data.api

import android.util.Log
import com.example.appreciclaje.data.api.dto.RegisterRequest
import com.example.appreciclaje.data.api.dto.RegisterResponse
import com.example.appreciclaje.network.NetworkUtils
import com.example.appreciclaje.network.UrlConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

object RegisterApi {
    private const val TAG = "RegisterApi"

    suspend fun register(
        dni: Int,
        nombre: String,
        apellido: String,
        alias: String,
        telefono: String,
        email: String,
        password: String
    ): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val httpResponse = NetworkUtils.httpClient.post("${UrlConfig.BASE_URL}/auth/register") {
                    contentType(ContentType.Application.Json)
                    setBody(RegisterRequest(dni, nombre, apellido, alias, telefono, email, password))
                }

                if (!httpResponse.status.isSuccess()) {
                    Log.d(TAG, "Recibido código HTTP: ${httpResponse.status}")

                    if (httpResponse.status == HttpStatusCode.Conflict) {
                        try {
                            val errorText = httpResponse.bodyAsText()
                            val errorJson = Json.decodeFromString<JsonObject>(errorText)
                            val detail = errorJson["detail"]?.jsonPrimitive?.content

                            return@withContext Result.failure(
                                Exception(detail ?: "El usuario ya existe")
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al procesar mensaje de error", e)
                            return@withContext Result.failure(Exception("El usuario ya existe"))
                        }
                    }

                    return@withContext when (httpResponse.status) {
                        HttpStatusCode.BadRequest -> Result.failure(Exception("Datos inválidos"))
                        else -> Result.failure(Exception("Error en el servidor: ${httpResponse.status}"))
                    }
                }

                val response = httpResponse.body<RegisterResponse>()
                Result.success(response)
            } catch (e: ClientRequestException) {
                Log.e(TAG, "ClientRequestException: ${e.response.status}", e)

                if (e.response.status == HttpStatusCode.Conflict) {
                    try {
                        val errorText = e.response.bodyAsText()
                        val errorJson = Json.decodeFromString<JsonObject>(errorText)
                        val detail = errorJson["detail"]?.jsonPrimitive?.content

                        return@withContext Result.failure(
                            Exception(detail ?: "El usuario ya existe")
                        )
                    } catch (ex: Exception) {
                        Log.e(TAG, "Error al procesar mensaje de error", ex)
                    }
                }

                val result = when (e.response.status) {
                    HttpStatusCode.BadRequest -> "Datos inválidos"
                    else -> "Error de servidor: ${e.response.status}"
                }
                Result.failure(Exception(result))
            } catch (e: SerializationException) {
                Log.e(TAG, "SerializationException", e)
                Result.failure(Exception("Error al procesar la respuesta del servidor"))
            } catch (e: Exception) {
                Log.e(TAG, "Excepción general", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde"))
            }
        }
    }
}