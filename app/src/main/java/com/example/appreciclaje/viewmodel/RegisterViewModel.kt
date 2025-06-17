package com.example.appreciclaje.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.RegisterApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel : ViewModel() {

    private val _dni = MutableLiveData<String>()
    val dni: LiveData<String> = _dni

    private val _nombre = MutableLiveData<String>()
    val nombre: LiveData<String> = _nombre

    private val _apellido = MutableLiveData<String>()
    val apellido: LiveData<String> = _apellido

    private val _alias = MutableLiveData<String>()
    val alias: LiveData<String> = _alias

    private val _telefono = MutableLiveData<String>()
    val telefono: LiveData<String> = _telefono

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _registerEnable = MutableLiveData<Boolean>()
    val registerEnable: LiveData<Boolean> = _registerEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun onRegisterFieldsChanged(
        dni: String,
        nombre: String,
        apellido: String,
        alias: String,
        telefono: String,
        email: String,
        password: String
    ) {
        _dni.value = dni
        _nombre.value = nombre
        _apellido.value = apellido
        _alias.value = alias
        _telefono.value = telefono
        _email.value = email
        _password.value = password

        _registerEnable.value =
            isValidDni(dni) &&
                    isValidNombre(nombre) &&
                    isValidApellido(apellido) &&
                    isValidAlias(alias) &&
                    isValidTelefono(telefono) &&
                    isValidEmail(email) &&
                    isValidPassword(password)
    }

    private fun isValidDni(dni: String): Boolean = dni.isNotBlank() && dni.length >= 8
    private fun isValidNombre(nombre: String): Boolean = nombre.isNotBlank()
    private fun isValidApellido(apellido: String): Boolean = apellido.isNotBlank()
    private fun isValidAlias(alias: String): Boolean = alias.isNotBlank()
    private fun isValidTelefono(telefono: String): Boolean = telefono.isNotBlank() && Patterns.PHONE.matcher(telefono).matches() && telefono.length >=9
    private fun isValidEmail(email: String): Boolean = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean = password.isNotBlank() && password.length > 5

    suspend fun onRegisterSelected(navigateOnSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val dniInt = _dni.value?.toIntOrNull() ?: 0
                if (dniInt <= 0) {
                    _error.value = "DNI inválido"
                    _isLoading.value = false
                    return@launch
                }

                val result = RegisterApi.register(
                    dni = dniInt,
                    nombre = _nombre.value ?: "",
                    apellido = _apellido.value ?: "",
                    alias = _alias.value ?: "",
                    telefono = _telefono.value ?: "",
                    email = _email.value ?: "",
                    password = _password.value ?: ""
                )

                result.fold(
                    onSuccess = {
                        _isLoading.value = false
                        withContext(Dispatchers.Main) {
                            navigateOnSuccess()
                        }
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
