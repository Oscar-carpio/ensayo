package com.example.ensayo.ui.repertorio

import com.example.ensayo.domain.model.Song

sealed interface RepertorioUiState {
    data object Loading : RepertorioUiState
    data object Empty : RepertorioUiState
    data class Success(
        val songs: List<Song>,
        val query: String = "",
        val selectedKey: String? = null,
        val availableKeys: List<String> = emptyList()
    ) : RepertorioUiState
    data class Error(val message: String) : RepertorioUiState
}
