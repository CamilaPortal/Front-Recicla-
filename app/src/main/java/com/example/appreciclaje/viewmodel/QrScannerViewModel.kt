package com.example.appreciclaje.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.QrScanApi
import com.example.appreciclaje.network.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    data class TokenExtracted(val token: String) : ScanState()
    object Sending : ScanState()
    object Success : ScanState()
    data class Error(val message: String) : ScanState()
}

class QrScannerViewModel : ViewModel() {
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState

    private val _scannedCode = MutableStateFlow<String?>(null)
    val scannedCode: StateFlow<String?> = _scannedCode

    fun onCodeScanned(code: String) {
        _scannedCode.value = code

        val tokenRegex = "Token:\\s*([a-zA-Z0-9-]+)".toRegex()
        val matchResult = tokenRegex.find(code)

        if (matchResult != null && matchResult.groupValues.size > 1) {
            val token = matchResult.groupValues[1]
            Log.d("QrScannerViewModel", "Token extraído: $token")
            _scanState.value = ScanState.TokenExtracted(token)
        } else {
            Log.e("QrScannerViewModel", "No se pudo extraer el token del código: $code")
            _scanState.value = ScanState.Error("Código QR inválido. No se encontró el token.")
        }
    }

    fun confirmScan() {
        val currentState = _scanState.value
        if (currentState is ScanState.TokenExtracted) {
            viewModelScope.launch {
                _scanState.value = ScanState.Sending
                try {
                    val result = QrScanApi.confirmScan(currentState.token)

                    result.onSuccess { response ->
                        if (response.success) {
                            _scanState.value = ScanState.Success
                        } else {
                            _scanState.value = ScanState.Error(response.message ?: "Error desconocido")
                        }
                    }.onFailure { error ->
                        _scanState.value = ScanState.Error(error.message ?: "Error desconocido")
                    }
                } catch (e: Exception) {
                    _scanState.value = ScanState.Error("Error al procesar: ${e.message}")
                }
            }
        }
    }

    fun clearCode() {
        _scannedCode.value = null
        _scanState.value = ScanState.Idle
    }

    fun checkSession(): Boolean {
        val hasSession = SessionManager.isLoggedIn()
        Log.d("QrScannerViewModel", "¿Usuario con sesión activa?: $hasSession")

        if (!hasSession) {
            _scanState.value = ScanState.Error("No hay sesión activa. Inicia sesión nuevamente.")
        }
        return hasSession
    }
}

