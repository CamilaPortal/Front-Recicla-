package com.example.appreciclaje.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.LoginApi
import com.example.appreciclaje.network.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {


    private val _email= MutableLiveData<String>()
    val email : LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password : LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable : LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error


    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidPassword(password: String): Boolean =password.length > 3

    private fun isValidEmail(email: String): Boolean =Patterns.EMAIL_ADDRESS.matcher(email).matches()

    suspend fun onLoginSelected(navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = LoginApi.login(
                    email = _email.value ?: "",
                    password = _password.value ?: ""
                )

                result.fold(
                    onSuccess = { response ->
                        SessionManager.saveLoginData(
                            token = response.access_token,
                            dni = response.dni,
                            email = response.email,
                            rol = response.rol
                        )
                        _isLoading.value = false
                        navigate()
                    },
                    onFailure = { exception ->
                        _isLoading.value = false
                        _error.value = exception.message
                    }
                )
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error inesperado: ${e.message}"
            }
        }
    }
}