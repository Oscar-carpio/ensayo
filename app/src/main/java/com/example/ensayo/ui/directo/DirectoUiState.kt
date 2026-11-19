package com.example.ensayo.ui.directo

import com.example.ensayo.domain.chordpro.ChordLine

sealed interface DirectoUiState {
    data object Loading : DirectoUiState

    data object NotFound : DirectoUiState

    data class Success(
        val songId: String,
        val title: String,
        val displayKey: String,
        val lines: List<ChordLine>,
        val fontSize: Int,
        // Posición en el setlist (1, 2, 3...). Con una canción suelta: 1 de 1
        val position: Int = 1,
        val total: Int = 1,
        val nextTitle: String? = null
    ) : DirectoUiState
}
