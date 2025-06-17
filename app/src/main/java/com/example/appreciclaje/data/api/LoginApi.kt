package com.example.appreciclaje.data.api

import android.util.Log
import com.example.appreciclaje.data.api.DTO.LoginRequest
import com.example.appreciclaje.data.api.DTO.LoginResponse
import com.example.appreciclaje.network.NetworkUtils
import com.example.appreciclaje.network.UrlConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException

object LoginApi {
    private const val TAG = "LoginApi"

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val httpResponse = NetworkUtils.httpClient.post("${UrlConfig.BASE_URL}/auth/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(email, password))
                }

                if (httpResponse.status == HttpStatusCode.Unauthorized) {
                    Log.d(TAG, "Recibido código 401 Unauthorized")
                    return@withContext Result.failure(Exception("Credenciales incorrectas"))
                }

                if (!httpResponse.status.isSuccess()) {
                    Log.d(TAG, "Recibido código HTTP: ${httpResponse.status}")
                    return@withContext Result.failure(Exception("Error en el servidor: ${httpResponse.status}"))
                }

                val response = httpResponse.body<LoginResponse>()
                Result.success(response)
            } catch (e: ClientRequestException) {
                Log.e(TAG, "ClientRequestException: ${e.response.status}", e)
                val result = when (e.response.status) {
                    HttpStatusCode.Unauthorized -> "Credenciales incorrectas"
                    HttpStatusCode.NotFound -> "Usuario no encontrado"
                    else -> "Error de servidor: ${e.response.status}"
                }
                Result.failure(Exception(result))
            } catch (e: SerializationException) {
                Log.e(TAG, "SerializationException", e)
                Result.failure(Exception("Usuario no registrado o credenciales incorrectas"))
            } catch (e: Exception) {
                Log.e(TAG, "Excepción general", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde"))
            }
        }
    }
}