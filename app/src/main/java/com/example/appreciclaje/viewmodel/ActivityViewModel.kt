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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MovementFilterType {
    ALL,
    RECYCLING,
    EXCHANGE
}

class ActivityViewModel : ViewModel() {
    private val _userMovements = MutableLiveData<List<UserMovement>>()
    val userMovements: LiveData<List<UserMovement>> = _userMovements

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _filterType = MutableLiveData(MovementFilterType.ALL)
    val filterType: LiveData<MovementFilterType> = _filterType

    private var allMovements = listOf<UserMovement>()

    init {
        loadMovementsHistory()
    }

    fun setFilter(filter: MovementFilterType) {
        if (_filterType.value != filter) {
            _filterType.value = filter
            applyFilter()
        }
    }

    fun loadMovementsHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val reciclajesResult = HistorialApi.getHistorialReciclaje()
                val reciclajes = reciclajesResult.getOrNull() ?: emptyList()

                val canjesResult = HistorialApi.getHistorialCanje()
                val canjes = canjesResult.getOrNull() ?: emptyList()

                allMovements = combineMovements(reciclajes, canjes)
                applyFilter()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error al cargar historial: ${e.message}"
                allMovements = emptyList()
                _userMovements.value = emptyList()
            }
        }
    }

    private fun applyFilter() {
        _userMovements.value = when (_filterType.value) {
            MovementFilterType.RECYCLING -> allMovements.filter { it.isPositive }
            MovementFilterType.EXCHANGE -> allMovements.filter { !it.isPositive }
            else -> allMovements
        }
    }

    private fun combineMovements(
        reciclajes: List<ReciclajeHistorialResponse>,
        canjes: List<HistorialCanjeResponse>
    ): List<UserMovement> {
        Log.d("ActivityViewModel", "Procesando ${reciclajes.size} reciclajes y ${canjes.size} canjes")

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
                Log.e("ActivityViewModel", "Error al procesar reciclaje: ${reciclaje.id}", e)
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
                Log.e("ActivityViewModel", "Error al procesar canje: ${canje.id}", e)
            }
        }

        return combinedMovements
            .sortedByDescending { it.first }
            .map { it.second }
    }

    private fun parseDate(dateString: String, formatters: List<SimpleDateFormat>): Date? {
        for (formatter in formatters) {
            try {
                return formatter.parse(dateString)
            } catch (e: Exception) {
            }
        }
        Log.w("ActivityViewModel", "No se pudo parsear la fecha: $dateString")
        return null
    }
}