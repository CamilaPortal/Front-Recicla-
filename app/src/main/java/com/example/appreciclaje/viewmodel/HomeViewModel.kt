package com.example.appreciclaje.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.dto.HistorialCanjeResponse
import com.example.appreciclaje.data.api.dto.ReciclajeHistorialResponse
import com.example.appreciclaje.data.api.dto.UserMovement
import com.example.appreciclaje.data.api.HistorialApi
import com.example.appreciclaje.network.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userPoints = MutableLiveData<Int>()
    val userPoints: LiveData<Int> = _userPoints

    private val _userMovements = MutableLiveData<List<UserMovement>>()
    val userMovements: LiveData<List<UserMovement>> = _userMovements

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    init {
        loadUserData()
        _navigateToLogin.value = false
    }

    fun logout() {
        SessionManager.clearSession()
        _navigateToLogin.value = true
    }

    fun onLoginNavigated() {
        _navigateToLogin.value = false
    }

    fun loadUserData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userDataResult = HistorialApi.getUserData()

                if (userDataResult.isSuccess) {
                    val userData = userDataResult.getOrNull()
                    _userName.value = userData?.nombre ?: ""
                    _userPoints.value = userData?.puntos_disponibles ?: 0

                    loadMovementsHistory()
                } else {
                    _error.value = userDataResult.exceptionOrNull()?.message
                    _userName.value = ""
                    _userPoints.value = 0
                    _userMovements.value = emptyList()
                }

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error al cargar datos: ${e.message}"
                _userName.value = ""
                _userPoints.value = 0
                _userMovements.value = emptyList()
            }
        }
    }

    private suspend fun loadMovementsHistory() {
        try {
            val reciclajesResult = HistorialApi.getHistorialReciclaje()
            val reciclajes = reciclajesResult.getOrNull() ?: emptyList()

            val canjesResult = HistorialApi.getHistorialCanje()
            val canjes = canjesResult.getOrNull() ?: emptyList()

            _userMovements.value = combineMovements(reciclajes, canjes)
        } catch (e: Exception) {
            _error.value = "Error al cargar historial: ${e.message}"
            _userMovements.value = emptyList()
        }
    }

    private fun combineMovements(
        reciclajes: List<ReciclajeHistorialResponse>,
        canjes: List<HistorialCanjeResponse>
    ): List<UserMovement> {
        Log.d("HomeViewModel", "Procesando ${reciclajes.size} reciclajes y ${canjes.size} canjes")

        val formatters = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )
        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val combinedMovements = mutableListOf<Pair<Date, UserMovement>>()

        reciclajes.forEach { reciclaje ->
            try {
                val date = parseDate(reciclaje.fecha_reciclaje, formatters) ?: Date()
                combinedMovements.add(
                    Pair(
                        date,
                        UserMovement(
                            id = reciclaje.id,
                            date = outputFormatter.format(date),
                            description = "Reciclaje: ${reciclaje.cantidad_botellas} botellas (${reciclaje.peso} g)",
                            points = reciclaje.puntos,
                            isPositive = true
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al procesar reciclaje: ${reciclaje.id}", e)
            }
        }

        canjes.forEach { canje ->
            try {
                val date = parseDate(canje.fecha_canje, formatters) ?: Date()
                combinedMovements.add(
                    Pair(
                        date,
                        UserMovement(
                            id = canje.id,
                            date = outputFormatter.format(date),
                            description = "Canje: ${canje.canje_nombre}",
                            points = canje.puntos_usados,
                            isPositive = false
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al procesar canje: ${canje.id}", e)
            }
        }

        val result = combinedMovements
            .sortedByDescending { it.first }
            .take(5)
            .map { it.second }

        Log.d("HomeViewModel", "Total de movimientos combinados: ${result.size}")
        return result
    }

    private fun parseDate(dateString: String, formatters: List<SimpleDateFormat>): Date? {
        for (formatter in formatters) {
            try {
                return formatter.parse(dateString)
            } catch (e: Exception) {
            }
        }
        Log.w("HomeViewModel", "No se pudo parsear la fecha: $dateString")
        return null
    }
}