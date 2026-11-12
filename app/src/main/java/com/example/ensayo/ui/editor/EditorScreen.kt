package com.example.ensayo.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onDone()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isNew) "Nueva canción" else "Editar canción") },
                navigationIcon = { TextButton(onClick = onDone) { Text("Cancelar") } },
                actions = {
                    TextButton(onClick = viewModel::save, enabled = uiState.canSave) {
                        Text("Guardar")
                    }
                }
            )
        }
    ) { innerPadding ->
        EditorContent(
            uiState = uiState,
            onTitleChange = viewModel::onTitleChange,
            onArtistChange = viewModel::onArtistChange,
            onKeyChange = viewModel::onKeyChange,
            onBpmChange = viewModel::onBpmChange,
            onContentChange = viewModel::onContentChange,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun EditorContent(
    uiState: EditorUiState,
    onTitleChange: (String) -> Unit,
    onArtistChange: (String) -> Unit,
    onKeyChange: (String) -> Unit,
    onBpmChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text("Título *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.artist,
            onValueChange = onArtistChange,
            label = { Text("Autor") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.musicalKey,
            onValueChange = onKeyChange,
            label = { Text("Tono * (ej. G, Am)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.bpm,
            onValueChange = onBpmChange,
            label = { Text("BPM") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.content,
            onValueChange = onContentChange,
            label = { Text("Letra con acordes: [G]Amazing [C]grace") },
            textStyle = TextStyle(fontFamily = FontFamily.Monospace),
            minLines = 10,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }
}
