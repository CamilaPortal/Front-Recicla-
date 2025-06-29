package com.example.appreciclaje.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.EmpresaApi
import com.example.appreciclaje.data.api.dto.ConfirmarEntregaResponse
import com.example.appreciclaje.data.api.dto.ValidarQrResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ValidarCanjeState {
    object Idle : ValidarCanjeState()
    object Scanning : ValidarCanjeState()
    object Validating : ValidarCanjeState()
    data class Success(val data: ValidarQrResponse) : ValidarCanjeState()
    object Confirming : ValidarCanjeState()
    data class ConfirmationSuccess(val data: ConfirmarEntregaResponse) : ValidarCanjeState()
    data class Error(val message: String) : ValidarCanjeState()
}

class ValidarCanjesViewModel : ViewModel() {
    private val _state = MutableStateFlow<ValidarCanjeState>(ValidarCanjeState.Idle)
    val state: StateFlow<ValidarCanjeState> = _state

    private var currentQrCode: String? = null

    fun onCodeScanned(code: String) {
        viewModelScope.launch {
            _state.value = ValidarCanjeState.Validating
            currentQrCode = code
            val result = EmpresaApi.validarQr(code)
            result.onSuccess { response ->
                _state.value = ValidarCanjeState.Success(response)
            }.onFailure { error ->
                _state.value = ValidarCanjeState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    fun confirmarEntrega() {
        currentQrCode?.let { code ->
            viewModelScope.launch {
                _state.value = ValidarCanjeState.Confirming
                val result = EmpresaApi.confirmarEntrega(code)
                result.onSuccess { response ->
                    _state.value = ValidarCanjeState.ConfirmationSuccess(response)
                }.onFailure { error ->
                    _state.value = ValidarCanjeState.Error(error.message ?: "Error desconocido")
                }
            }
        } ?: run {
            _state.value = ValidarCanjeState.Error("No se encontró un código QR para confirmar.")
        }
    }

    fun resetState() {
        currentQrCode = null
        _state.value = ValidarCanjeState.Idle
    }
}