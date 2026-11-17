package com.example.ensayo.ui.directo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.ensayo.domain.chordpro.renderChordPro
import com.example.ensayo.domain.chordpro.renderKey
import com.example.ensayo.domain.repository.PreferencesRepository
import com.example.ensayo.domain.repository.SongRepository
import com.example.ensayo.ui.navigation.DirectoRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DirectoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: SongRepository,
    preferences: PreferencesRepository
) : ViewModel() {
    private val route = savedStateHandle.toRoute<DirectoRoute>()

    val uiState: StateFlow<DirectoUiState> = combine(
        repository.observeSong(route.id),
        preferences.notation,
        preferences.fontSize
    ) { song, notation, fontSize ->
        if (song == null) {
            DirectoUiState.NotFound
        } else {
            DirectoUiState.Success(
                title = song.title,
                displayKey = renderKey(song.musicalKey, route.semitones, notation),
                lines = renderChordPro(song.content, route.semitones, notation),
                fontSize = fontSize + LIVE_EXTRA_SIZE
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DirectoUiState.Loading
    )

    private companion object {
        // En el escenario la letra va más grande que en la ficha
        const val LIVE_EXTRA_SIZE = 6
    }
}
