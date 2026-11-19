package com.example.ensayo.ui.directo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.ensayo.domain.chordpro.renderChordPro
import com.example.ensayo.domain.chordpro.renderKey
import com.example.ensayo.domain.model.SetlistItem
import com.example.ensayo.domain.repository.PreferencesRepository
import com.example.ensayo.domain.repository.SetlistRepository
import com.example.ensayo.domain.repository.SongRepository
import com.example.ensayo.ui.navigation.DirectoRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class DirectoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val songRepository: SongRepository,
    private val setlistRepository: SetlistRepository,
    preferences: PreferencesRepository
) : ViewModel() {
    private val route = savedStateHandle.toRoute<DirectoRoute>()

    // La cola: todo el setlist, o una lista de una sola canción. null = todavía cargando
    private val queue: StateFlow<List<SetlistItem>?> = queueFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val currentSongId = MutableStateFlow(route.id)

    val uiState: StateFlow<DirectoUiState> = combine(
        queue,
        currentSongId,
        preferences.notation,
        preferences.fontSize
    ) { items, currentId, notation, fontSize ->
        val index = items?.indexOfFirst { it.song.id == currentId } ?: -1
        when {
            items == null -> DirectoUiState.Loading
            index < 0 -> DirectoUiState.NotFound
            else -> {
                val item = items[index]
                DirectoUiState.Success(
                    songId = item.song.id,
                    title = item.song.title,
                    displayKey = renderKey(item.song.musicalKey, item.semitones, notation),
                    lines = renderChordPro(item.song.content, item.semitones, notation),
                    fontSize = fontSize + LIVE_EXTRA_SIZE,
                    position = index + 1,
                    total = items.size,
                    nextTitle = items.getOrNull(index + 1)?.song?.title
                )
            }
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

    fun next() = goTo(step = 1)

    fun previous() = goTo(step = -1)

    private fun goTo(step: Int) {
        val target = queue.value?.songIdAt(currentSongId.value, step) ?: return
        // Cada canción empieza parada: el músico decide cuándo arrancar
        _isPlaying.value = false
        currentSongId.value = target
    }

    private fun queueFlow(): Flow<List<SetlistItem>> {
        val setlistId = route.setlistId
        return if (setlistId != null) {
            setlistRepository.observeItems(setlistId)
        } else {
            songRepository.observeSong(route.id).map { song ->
                listOfNotNull(
                    song?.let { SetlistItem(song = it, position = 0, semitones = route.semitones) }
                )
            }
        }
    }

    companion object {
        // En el escenario la letra va más grande que en la ficha
        const val LIVE_EXTRA_SIZE = 6
        const val MIN_SPEED = 1
        const val MAX_SPEED = 10
        const val DEFAULT_SPEED = 3
    }
}
