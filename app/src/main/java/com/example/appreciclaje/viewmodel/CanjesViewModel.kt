package com.example.appreciclaje.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.CanjesApi
import com.example.appreciclaje.data.api.dto.CanjeDisponibleResponse
import kotlinx.coroutines.launch

class CanjesViewModel : ViewModel() {
    private val _canjes = MutableLiveData<List<CanjeDisponibleResponse>>()
    val canjes: LiveData<List<CanjeDisponibleResponse>> = _canjes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _canjeResult = MutableLiveData<String?>(null)
    val canjeResult: LiveData<String?> = _canjeResult

    init {
        loadCanjesDisponibles()
    }

    fun loadCanjesDisponibles() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = CanjesApi.getCanjesDisponibles()
            result.onSuccess {
                _canjes.value = it
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun realizarCanje(canjeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = CanjesApi.realizarCanje(canjeId)
            result.onSuccess {
                _canjeResult.value = "¡Canje realizado con éxito!"
                loadCanjesDisponibles()
            }.onFailure {
                _canjeResult.value = it.message ?: "Error al realizar el canje."
            }
            _isLoading.value = false
        }
    }

    fun clearCanjeResult() {
        _canjeResult.value = null
    }
}