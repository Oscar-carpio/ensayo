package com.example.ensayo.ui.setlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.ensayo.domain.repository.SetlistRepository
import com.example.ensayo.domain.repository.SongRepository
import com.example.ensayo.ui.navigation.SetlistDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SetlistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val setlistRepository: SetlistRepository,
    songRepository: SongRepository
) : ViewModel() {
    private val setlistId = savedStateHandle.toRoute<SetlistDetailRoute>().id

    val uiState: StateFlow<SetlistDetailUiState> = combine(
        setlistRepository.observeSetlist(setlistId),
        setlistRepository.observeItems(setlistId),
        songRepository.observeSongs()
    ) { setlist, items, allSongs ->
        if (setlist == null) {
            SetlistDetailUiState.NotFound
        } else {
            val alreadyIn = items.map { it.song.id }.toSet()
            SetlistDetailUiState.Success(
                setlist = setlist,
                items = items,
                availableSongs = allSongs.filterNot { it.id in alreadyIn }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SetlistDetailUiState.Loading
    )

    fun addSong(songId: String) {
        viewModelScope.launch { setlistRepository.addSong(setlistId, songId) }
    }

    fun removeSong(songId: String) {
        viewModelScope.launch { setlistRepository.removeSong(setlistId, songId) }
    }

    fun moveUp(index: Int) {
        viewModelScope.launch { setlistRepository.moveSong(setlistId, index, index - 1) }
    }

    fun moveDown(index: Int) {
        viewModelScope.launch { setlistRepository.moveSong(setlistId, index, index + 1) }
    }
}
