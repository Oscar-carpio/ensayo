package com.example.ensayo.ui.directo

import com.example.ensayo.domain.chordpro.ChordLine

sealed interface DirectoUiState {
    data object Loading : DirectoUiState

    data object NotFound : DirectoUiState

    data class Success(
        val title: String,
        val displayKey: String,
        val lines: List<ChordLine>,
        val fontSize: Int
    ) : DirectoUiState
}
