package com.example.ensayo.ui.cancion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.ensayo.domain.chordpro.parseChordPro
import com.example.ensayo.domain.repository.PreferencesRepository
import com.example.ensayo.domain.repository.SongRepository
import com.example.ensayo.ui.navigation.CancionRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CancionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: SongRepository,
    private val preferences: PreferencesRepository
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
    val fontSize: StateFlow<Int> = preferences.fontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PreferencesRepository.DEFAULT_FONT_SIZE
    )

    fun increaseFontSize() {
        viewModelScope.launch { preferences.setFontSize(fontSize.value + 2) }
    }

    fun decreaseFontSize() {
        viewModelScope.launch { preferences.setFontSize(fontSize.value - 2) }
    }
}
