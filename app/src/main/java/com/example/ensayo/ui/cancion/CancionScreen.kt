package com.example.ensayo.ui.cancion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancionScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CancionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val fontSize by viewModel.fontSize.collectAsStateWithLifecycle()
    val song = (uiState as? CancionUiState.Success)?.song
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(song?.title ?: "") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } },
                actions = {
                    TextButton(onClick = viewModel::decreaseFontSize) { Text("A−") }
                    TextButton(onClick = viewModel::increaseFontSize) { Text("A+") }
                    if (song != null) {
                        TextButton(onClick = { onEdit(song.id) }) { Text("Editar") }
                    }
                }
            )
        }
    ) { innerPadding ->
        CancionContent(
            uiState = uiState,
            fontSize = fontSize,
            onTransposeDown = viewModel::transposeDown,
            onTransposeUp = viewModel::transposeUp,
            onResetTranspose = viewModel::resetTranspose,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CancionContent(
    uiState: CancionUiState,
    fontSize: Int,
    onTransposeDown: () -> Unit,
    onTransposeUp: () -> Unit,
    onResetTranspose: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        CancionUiState.Loading -> Box(modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        CancionUiState.NotFound -> Box(modifier.fillMaxSize(), Alignment.Center) {
            Text("Canción no encontrada")
        }
        is CancionUiState.Success -> SongSheet(
            state = uiState,
            fontSize = fontSize,
            onTransposeDown = onTransposeDown,
            onTransposeUp = onTransposeUp,
            onResetTranspose = onResetTranspose,
            modifier = modifier
        )
    }
}

@Composable
private fun SongSheet(
    state: CancionUiState.Success,
    fontSize: Int,
    onTransposeDown: () -> Unit,
    onTransposeUp: () -> Unit,
    onResetTranspose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shift = if (state.semitones > 0) "+${state.semitones}" else "${state.semitones}"
    val keyLabel = if (state.semitones == 0) {
        "Tono: ${state.displayKey}"
    } else {
        "Tono: ${state.displayKey} ($shift)"
    }
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Column {
                Text(state.song.artist, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onTransposeDown) { Text("−1") }
                    Text(keyLabel, style = MaterialTheme.typography.bodyLarge)
                    TextButton(onClick = onTransposeUp) { Text("+1") }
                    if (state.semitones != 0) {
                        TextButton(onClick = onResetTranspose) { Text("Original") }
                    }
                }
                state.song.bpm?.let {
                    Text("$it BPM", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
        items(state.lines) { line ->
            Column {
                if (line.chords.isNotEmpty()) {
                    Text(
                        text = line.chords,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (line.lyrics.isNotBlank()) {
                    Text(
                        text = line.lyrics,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize.sp
                    )
                } else if (line.chords.isEmpty()) {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
