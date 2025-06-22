package com.example.appreciclaje.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QrScannerViewModel : ViewModel() {
    private val _scannedCode = MutableStateFlow<String?>(null)
    val scannedCode: StateFlow<String?> = _scannedCode

    fun onCodeScanned(code: String) {
        _scannedCode.value = code
    }

    fun clearCode() {
        _scannedCode.value = null
    }
}
