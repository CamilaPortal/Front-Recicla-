package com.example.appreciclaje.data.api

import android.util.Log
import com.example.appreciclaje.data.api.dto.MiPosicionHistoricaResponse
import com.example.appreciclaje.data.api.dto.RankingHistoricoItem
import com.example.appreciclaje.network.NetworkUtils
import com.example.appreciclaje.network.SessionManager
import com.example.appreciclaje.network.UrlConfig
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RankingApi {
    private const val TAG = "RankingApi"

    suspend fun getRankingHistorico(): Result<List<RankingHistoricoItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val httpResponse = NetworkUtils.httpClient.get("${UrlConfig.BASE_URL}/ranking/puntos-historicos/") {
                    contentType(ContentType.Application.Json)
                }

                if (!httpResponse.status.isSuccess()) {
                    Log.d(TAG, "Recibido código HTTP: ${httpResponse.status}")
                    return@withContext Result.failure(Exception("Error en el servidor: ${httpResponse.status}"))
                }

                val rankingList = httpResponse.body<List<RankingHistoricoItem>>()
                Result.success(rankingList)
            } catch (e: Exception) {
                Log.e(TAG, "Error obteniendo ranking histórico", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde"))
            }
        }
    }
    suspend fun getMiPosicionHistorica(): Result<MiPosicionHistoricaResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.getToken()

                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("No hay sesión activa"))
                }

                val httpResponse = NetworkUtils.httpClient.get("${UrlConfig.BASE_URL}/ranking/mi-posicion-historica/") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $token")
                }

                if (!httpResponse.status.isSuccess()) {
                    Log.d(TAG, "Recibido código HTTP: ${httpResponse.status}")
                    return@withContext Result.failure(Exception("Error en el servidor: ${httpResponse.status}"))
                }

                val posicionData = httpResponse.body<MiPosicionHistoricaResponse>()
                Result.success(posicionData)
            } catch (e: Exception) {
                Log.e(TAG, "Error obteniendo mi posición en el ranking", e)
                Result.failure(Exception("Error de conexión. Inténtalo más tarde"))
            }
        }
    }
}