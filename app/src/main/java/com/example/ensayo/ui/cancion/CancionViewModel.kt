package com.example.ensayo.ui.cancion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.ensayo.domain.chordpro.parseChordPro
import com.example.ensayo.domain.repository.SongRepository
import com.example.ensayo.ui.navigation.CancionRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CancionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: SongRepository
) : ViewModel() {
    private val songId = savedStateHandle.toRoute<CancionRoute>().id

    val uiState: StateFlow<CancionUiState> = repository.observeSong(songId)
        .map { song ->
            if (song == null) {
                CancionUiState.NotFound
            } else {
                CancionUiState.Success(song = song, lines = parseChordPro(song.content))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CancionUiState.Loading
        )
}
