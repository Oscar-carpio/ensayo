package com.example.ensayo.ui.repertorio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RepertorioScreen(
    modifier: Modifier = Modifier,
    viewModel: RepertorioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RepertorioContent(uiState = uiState, modifier = modifier)
}

@Composable
fun RepertorioContent(uiState: RepertorioUiState, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            RepertorioUiState.Loading -> CircularProgressIndicator()
            RepertorioUiState.Empty -> Text("Todavía no hay canciones")
            is RepertorioUiState.Error -> Text(uiState.message)
            is RepertorioUiState.Success -> LazyColumn(Modifier.fillMaxSize()) {
                items(uiState.songs, key = { it.id }) { song ->
                    Text(song.title, Modifier.padding(16.dp))
                }
            }
        }
    }
}
