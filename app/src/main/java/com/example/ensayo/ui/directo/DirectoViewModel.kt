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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

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

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _speed = MutableStateFlow(DEFAULT_SPEED)
    val speed: StateFlow<Int> = _speed.asStateFlow()

    fun togglePlay() {
        _isPlaying.update { !it }
    }

    fun pause() {
        _isPlaying.value = false
    }

    fun faster() {
        _speed.update { (it + 1).coerceAtMost(MAX_SPEED) }
    }

    fun slower() {
        _speed.update { (it - 1).coerceAtLeast(MIN_SPEED) }
    }

    companion object {
        // En el escenario la letra va más grande que en la ficha
        const val LIVE_EXTRA_SIZE = 6
        const val MIN_SPEED = 1
        const val MAX_SPEED = 10
        const val DEFAULT_SPEED = 3
    }
}
