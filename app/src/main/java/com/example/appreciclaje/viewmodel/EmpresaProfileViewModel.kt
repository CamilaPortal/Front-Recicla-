package com.example.appreciclaje.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.EmpresaApi
import com.example.appreciclaje.data.api.dto.ChangePasswordRequest
import com.example.appreciclaje.data.api.dto.EmpresaProfileResponse
import kotlinx.coroutines.launch

class EmpresaProfileViewModel : ViewModel() {

    private val _profile = MutableLiveData<EmpresaProfileResponse?>()
    val profile: LiveData<EmpresaProfileResponse?> = _profile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _passwordChangeResult = MutableLiveData<String?>()
    val passwordChangeResult: LiveData<String?> = _passwordChangeResult

    init {
        loadProfile()
    }

    private fun loadProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = EmpresaApi.getProfile()
            if (result.isSuccess) {
                _profile.value = result.getOrNull()
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun changePassword(current: String, new: String, confirm: String) {
        if (current.isEmpty() || new.isEmpty() || confirm.isEmpty()) {
            _passwordChangeResult.value = "Todos los campos son obligatorios."
            return
        }
        if (new != confirm) {
            _passwordChangeResult.value = "Las nuevas contraseñas no coinciden."
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = EmpresaApi.changePassword(ChangePasswordRequest(current, new, confirm))
            if (result.isSuccess) {
                _passwordChangeResult.value = result.getOrNull()
            } else {
                _passwordChangeResult.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun clearPasswordChangeResult() {
        _passwordChangeResult.value = null
    }
}