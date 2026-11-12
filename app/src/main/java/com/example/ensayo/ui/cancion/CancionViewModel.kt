package com.example.ensayo.ui.cancion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.ensayo.domain.chordpro.Transposer
import com.example.ensayo.domain.chordpro.parseChordPro
import com.example.ensayo.domain.repository.PreferencesRepository
import com.example.ensayo.domain.repository.SongRepository
import com.example.ensayo.ui.navigation.CancionRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CancionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: SongRepository,
    private val preferences: PreferencesRepository
) : ViewModel() {
    private val songId = savedStateHandle.toRoute<CancionRoute>().id
    private val semitones = MutableStateFlow(0)

    val uiState: StateFlow<CancionUiState> =
        combine(repository.observeSong(songId), semitones) { song, steps ->
            if (song == null) {
                CancionUiState.NotFound
            } else {
                val content = Transposer.transposeChordPro(song.content, steps)
                CancionUiState.Success(
                    song = song,
                    lines = parseChordPro(content),
                    semitones = steps,
                    displayKey = Transposer.transposeChord(song.musicalKey, steps)
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CancionUiState.Loading
        )

    val fontSize: StateFlow<Int> = preferences.fontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PreferencesRepository.DEFAULT_FONT_SIZE
    )

    fun transposeUp() {
        semitones.update { normalize(it + 1) }
    }

    fun transposeDown() {
        semitones.update { normalize(it - 1) }
    }

    fun resetTranspose() {
        semitones.value = 0
    }

    fun increaseFontSize() {
        viewModelScope.launch { preferences.setFontSize(fontSize.value + 2) }
    }

    fun decreaseFontSize() {
        viewModelScope.launch { preferences.setFontSize(fontSize.value - 2) }
    }

    // Mantiene el valor entre -5 y +6: subir 7 semitonos es lo mismo que bajar 5
    private fun normalize(steps: Int): Int = (steps + 5).mod(12) - 5
}
