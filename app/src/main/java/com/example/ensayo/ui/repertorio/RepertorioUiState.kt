package com.example.ensayo.ui.repertorio

import com.example.ensayo.domain.model.Song

sealed interface RepertorioUiState {
    data object Loading : RepertorioUiState
    data object Empty : RepertorioUiState
    data class Success(val songs: List<Song>) : RepertorioUiState
    data class Error(val message: String) : RepertorioUiState
}
