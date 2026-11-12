package com.example.ensayo.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.ensayo.domain.model.Song
import com.example.ensayo.domain.repository.SongRepository
import com.example.ensayo.ui.navigation.EditorRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SongRepository
) : ViewModel() {

    private val songId: String? = savedStateHandle.toRoute<EditorRoute>().id
    private var original: Song? = null

    private val _uiState = MutableStateFlow(EditorUiState(isNew = songId == null))
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        if (songId != null) {
            viewModelScope.launch {
                val song = repository.observeSong(songId).first() ?: return@launch
                original = song
                _uiState.update {
                    it.copy(
                        title = song.title,
                        artist = song.artist,
                        musicalKey = song.musicalKey,
                        bpm = song.bpm?.toString() ?: "",
                        content = song.content
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }

    fun onArtistChange(value: String) = _uiState.update { it.copy(artist = value) }

    fun onKeyChange(value: String) = _uiState.update { it.copy(musicalKey = value) }

    fun onContentChange(value: String) = _uiState.update { it.copy(content = value) }

    fun onBpmChange(value: String) {
        if (value.length <= 3 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(bpm = value) }
        }
    }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val base = original ?: Song(title = "", musicalKey = "", content = "")
            val song = base.copy(
                title = state.title.trim(),
                artist = state.artist.trim(),
                musicalKey = state.musicalKey.trim(),
                bpm = state.bpm.toIntOrNull(),
                content = state.content
            )
            repository.save(song)
            _uiState.update { it.copy(isSaving = false, saved = true) }
        }
    }
}
