package com.example.ensayo.ui.cancion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ensayo.domain.chordpro.ChordLine
import com.example.ensayo.domain.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancionScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CancionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val song = (uiState as? CancionUiState.Success)?.song
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(song?.title ?: "") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } },
                actions = {
                    if (song != null) {
                        TextButton(onClick = { onEdit(song.id) }) { Text("Editar") }
                    }
                }
            )
        }
    ) { innerPadding ->
        CancionContent(uiState = uiState, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun CancionContent(uiState: CancionUiState, modifier: Modifier = Modifier) {
    when (uiState) {
        CancionUiState.Loading -> Box(modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        CancionUiState.NotFound -> Box(modifier.fillMaxSize(), Alignment.Center) {
            Text("Canción no encontrada")
        }
        is CancionUiState.Success -> SongSheet(uiState.song, uiState.lines, modifier)
    }
}

@Composable
private fun SongSheet(song: Song, lines: List<ChordLine>, modifier: Modifier = Modifier) {
    val info = listOfNotNull("Tono: ${song.musicalKey}", song.bpm?.let { "$it BPM" })
        .joinToString(" · ")
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Column {
                Text(song.artist, style = MaterialTheme.typography.titleMedium)
                Text(info, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
            }
        }
        items(lines) { line ->
            Column {
                if (line.chords.isNotEmpty()) {
                    Text(
                        text = line.chords,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (line.lyrics.isNotBlank()) {
                    Text(text = line.lyrics, fontFamily = FontFamily.Monospace)
                } else if (line.chords.isEmpty()) {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
