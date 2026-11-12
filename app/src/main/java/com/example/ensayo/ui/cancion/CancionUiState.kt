package com.example.ensayo.ui.cancion

import com.example.ensayo.domain.chordpro.ChordLine
import com.example.ensayo.domain.model.Song

sealed interface CancionUiState {
    data object Loading : CancionUiState
    data object NotFound : CancionUiState
    data class Success(val song: Song, val lines: List<ChordLine>) : CancionUiState
}
