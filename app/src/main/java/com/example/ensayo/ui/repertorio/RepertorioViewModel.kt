package com.example.ensayo.ui.repertorio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ensayo.domain.model.Song
import com.example.ensayo.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RepertorioViewModel @Inject constructor(repository: SongRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val selectedKey = MutableStateFlow<String?>(null)

    val uiState: StateFlow<RepertorioUiState> =
        combine(repository.observeSongs(), query, selectedKey) { songs, text, key ->
            if (songs.isEmpty()) {
                RepertorioUiState.Empty
            } else {
                RepertorioUiState.Success(
                    songs = filterSongs(songs, text, key),
                    query = text,
                    selectedKey = key,
                    availableKeys = songs.map { it.musicalKey }.distinct().sorted()
                )
            }
        }
            .catch { emit(RepertorioUiState.Error(it.message ?: "Error desconocido")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RepertorioUiState.Loading
            )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onKeySelected(key: String) {
        selectedKey.value = if (selectedKey.value == key) null else key
    }
}

internal fun filterSongs(songs: List<Song>, query: String, key: String?): List<Song> =
    songs.filter { song ->
        val matchesQuery = query.isBlank() ||
            song.title.contains(query, ignoreCase = true) ||
            song.artist.contains(query, ignoreCase = true)
        val matchesKey = key == null || song.musicalKey == key
        matchesQuery && matchesKey
    }
