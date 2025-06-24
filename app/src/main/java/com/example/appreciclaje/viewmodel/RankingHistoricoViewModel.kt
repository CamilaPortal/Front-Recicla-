package com.example.appreciclaje.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appreciclaje.data.api.RankingApi
import com.example.appreciclaje.data.api.dto.RankingHistoricoItem
import com.example.appreciclaje.data.api.HistorialApi
import com.example.appreciclaje.data.api.dto.MiPosicionHistoricaResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RankingHistoricoViewModel : ViewModel() {
    private val _rankingState = MutableStateFlow<RankingState>(RankingState.Loading)
    val rankingState = _rankingState.asStateFlow()

    private val _currentUserAlias = MutableStateFlow("")
    val currentUserAlias = _currentUserAlias.asStateFlow()

    private val _userPosition = MutableStateFlow<MiPosicionHistoricaResponse?>(null)
    val userPosition = _userPosition.asStateFlow()

    init {
        loadRanking()
        loadCurrentUserAlias()
        loadUserPosition()
    }

    private fun loadCurrentUserAlias() {
        viewModelScope.launch {
            HistorialApi.getUserData()
                .onSuccess { userData ->
                    _currentUserAlias.value = userData.alias
                }
                .onFailure {
                }
        }
    }

    private fun loadUserPosition() {
        viewModelScope.launch {
            RankingApi.getMiPosicionHistorica()
                .onSuccess { response ->
                    _userPosition.value = response
                }
                .onFailure {
                }
        }
    }

    fun loadRanking() {
        viewModelScope.launch {
            _rankingState.value = RankingState.Loading
            RankingApi.getRankingHistorico()
                .onSuccess { response ->
                    _rankingState.value = RankingState.Success(response)
                }
                .onFailure { error ->
                    _rankingState.value = RankingState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    sealed class RankingState {
        object Loading : RankingState()
        data class Success(val ranking: List<RankingHistoricoItem>) : RankingState()
        data class Error(val message: String) : RankingState()
    }
}