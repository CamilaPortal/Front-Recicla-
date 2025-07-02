package com.example.appreciclaje.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.EmpresaApi
import com.example.appreciclaje.data.api.dto.CanjesEmpresaResponse
import com.example.appreciclaje.data.api.dto.CrearCanjeRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AdministrarCanjesViewModel : ViewModel() {

    private val _canjes = MutableLiveData<List<CanjesEmpresaResponse>>()
    val canjes: LiveData<List<CanjesEmpresaResponse>> = _canjes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _updateSuccess = MutableSharedFlow<String>()
    val updateSuccess = _updateSuccess.asSharedFlow()

    init {
        loadMisCanjes()
    }

    fun loadMisCanjes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = EmpresaApi.getMisCanjes()
                result.onSuccess {
                    _canjes.value = it
                }.onFailure {
                    _error.value = it.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarPuntos(canjeId: Int, nuevosPuntos: Int) {
        viewModelScope.launch {
            val result = EmpresaApi.actualizarPuntosCanje(canjeId, nuevosPuntos)
            result.onSuccess {
                updateLocalCanje(canjeId, puntos = nuevosPuntos)
                _updateSuccess.emit("Puntos actualizados correctamente")
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun actualizarEstado(canjeId: Int, nuevoEstado: Boolean) {
        viewModelScope.launch {
            val resultEstado = EmpresaApi.actualizarEstadoCanje(canjeId, nuevoEstado)
            resultEstado.onSuccess {
                if (!nuevoEstado) {
                    val resultStock = EmpresaApi.actualizarStockCanje(canjeId, 0)
                    resultStock.onSuccess {
                        updateLocalCanje(canjeId, isActive = false, stockActual = 0)
                        _updateSuccess.emit("Canje desactivado y stock actualizado a 0")
                    }.onFailure {
                        updateLocalCanje(canjeId, isActive = nuevoEstado)
                        _error.value = it.message
                    }
                } else {
                    updateLocalCanje(canjeId, isActive = nuevoEstado)
                    _updateSuccess.emit("Estado actualizado correctamente")
                }
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun crearCanje(nombre: String, descripcion: String, puntos: Int, stockInicial: Int) {
        viewModelScope.launch {
            val request = CrearCanjeRequest(nombre, descripcion, puntos, stock_inicial = stockInicial)
            val result = EmpresaApi.crearCanje(request)
            result.onSuccess {
                _updateSuccess.emit("Canje creado correctamente")
                loadMisCanjes()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun actualizarStock(canjeId: Int, nuevoStock: Int) {
        viewModelScope.launch {
            val canjeActual = _canjes.value?.find { it.id == canjeId } ?: return@launch

            val result = EmpresaApi.actualizarStockCanje(canjeId, nuevoStock)
            result.onSuccess {
                val nuevoEstadoActivo = when {
                    nuevoStock == 0 -> false
                    nuevoStock > 0 && canjeActual.stock_actual == 0 -> true
                    else -> null
                }
                updateLocalCanje(canjeId, stockActual = nuevoStock, isActive = nuevoEstadoActivo)
                _updateSuccess.emit("Stock actualizado correctamente")
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    private fun updateLocalCanje(canjeId: Int, puntos: Int? = null, isActive: Boolean? = null, stockActual: Int? = null) {
        val currentList = _canjes.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == canjeId }
        if (index != -1) {
            val canjeToUpdate = currentList[index]
            val updatedCanje = canjeToUpdate.copy(
                puntos = puntos ?: canjeToUpdate.puntos,
                is_active = isActive ?: canjeToUpdate.is_active,
                stock_actual = stockActual ?: canjeToUpdate.stock_actual
            )
            currentList[index] = updatedCanje
            _canjes.value = currentList
        }
    }
}