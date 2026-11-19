package com.example.ensayo.ui.setlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ensayo.domain.model.Setlist
import com.example.ensayo.domain.repository.SetlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SetlistsViewModel @Inject constructor(private val repository: SetlistRepository) :
    ViewModel() {

    val setlists: StateFlow<List<Setlist>> = repository.observeSetlists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun create(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.save(Setlist(name = trimmed)) }
    }

    fun delete(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }
}
