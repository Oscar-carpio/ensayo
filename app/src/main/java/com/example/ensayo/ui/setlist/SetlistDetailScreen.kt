package com.example.ensayo.ui.setlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ensayo.domain.chordpro.Transposer
import com.example.ensayo.domain.model.SetlistItem
import com.example.ensayo.domain.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistDetailScreen(
    onBack: () -> Unit,
    onSongClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SetlistDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val success = uiState as? SetlistDetailUiState.Success

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(success?.setlist?.name ?: "") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } }
            )
        },
        floatingActionButton = {
            if (success != null) {
                ExtendedFloatingActionButton(onClick = { showAddDialog = true }) {
                    Text("+ Canción")
                }
            }
        }
    ) { innerPadding ->
        SetlistDetailContent(
            uiState = uiState,
            onSongClick = onSongClick,
            onRemove = viewModel::removeSong,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showAddDialog && success != null) {
        AddSongDialog(
            songs = success.availableSongs,
            onSongSelected = viewModel::addSong,
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun SetlistDetailContent(
    uiState: SetlistDetailUiState,
    onSongClick: (String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        SetlistDetailUiState.Loading -> Box(modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        SetlistDetailUiState.NotFound -> Box(modifier.fillMaxSize(), Alignment.Center) {
            Text("Setlist no encontrado")
        }
        is SetlistDetailUiState.Success -> if (uiState.items.isEmpty()) {
            Box(modifier.fillMaxSize(), Alignment.Center) {
                Text(
                    text = "Este setlist está vacío.\nAñade canciones con «+ Canción».",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                itemsIndexed(uiState.items, key = { _, item -> item.song.id }) { index, item ->
                    Column {
                        ListItem(
                            leadingContent = { Text("${index + 1}.") },
                            headlineContent = { Text(item.song.title) },
                            supportingContent = { Text(keyLabel(item)) },
                            trailingContent = {
                                TextButton(onClick = { onRemove(item.song.id) }) {
                                    Text("Quitar")
                                }
                            },
                            modifier = Modifier.clickable { onSongClick(item.song.id) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun AddSongDialog(
    songs: List<Song>,
    onSongSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir canción") },
        text = {
            if (songs.isEmpty()) {
                Text("Todas las canciones ya están en este setlist.")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(songs, key = { it.id }) { song ->
                        ListItem(
                            headlineContent = { Text(song.title) },
                            supportingContent = { Text("Tono: ${song.musicalKey}") },
                            modifier = Modifier.clickable { onSongSelected(song.id) }
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

private fun keyLabel(item: SetlistItem): String {
    val key = Transposer.transposeChord(item.song.musicalKey, item.semitones)
    return when {
        item.semitones > 0 -> "Tono: $key (+${item.semitones})"
        item.semitones < 0 -> "Tono: $key (${item.semitones})"
        else -> "Tono: $key"
    }
}
