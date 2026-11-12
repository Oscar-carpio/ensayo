package com.example.ensayo.ui.repertorio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ensayo.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RepertorioViewModel @Inject constructor(repository: SongRepository) : ViewModel() {
    val uiState: StateFlow<RepertorioUiState> = repository.observeSongs()
        .map { songs ->
            if (songs.isEmpty()) RepertorioUiState.Empty else RepertorioUiState.Success(songs)
        }
        .catch { emit(RepertorioUiState.Error(it.message ?: "Error desconocido")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RepertorioUiState.Loading
        )
}
