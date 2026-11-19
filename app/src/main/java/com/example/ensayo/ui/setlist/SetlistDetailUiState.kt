package com.example.ensayo.ui.setlist

import com.example.ensayo.domain.model.Setlist
import com.example.ensayo.domain.model.SetlistItem
import com.example.ensayo.domain.model.Song

sealed interface SetlistDetailUiState {
    data object Loading : SetlistDetailUiState

    data object NotFound : SetlistDetailUiState

    data class Success(
        val setlist: Setlist,
        val items: List<SetlistItem>,
        val availableSongs: List<Song>
    ) : SetlistDetailUiState
}
