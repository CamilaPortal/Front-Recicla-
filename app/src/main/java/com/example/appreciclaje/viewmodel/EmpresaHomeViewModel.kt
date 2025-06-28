package com.example.appreciclaje.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.HistorialApi
import com.example.appreciclaje.network.SessionManager
import kotlinx.coroutines.launch

class EmpresaHomeViewModel : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

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

    private fun loadUserData() {
        viewModelScope.launch {
            val userDataResult = HistorialApi.getUserData()
            if (userDataResult.isSuccess) {
                _userName.value = userDataResult.getOrNull()?.nombre ?: ""
            } else {
                _userName.value = SessionManager.getEmail() ?: "Empresa"
            }
        }
    }
}