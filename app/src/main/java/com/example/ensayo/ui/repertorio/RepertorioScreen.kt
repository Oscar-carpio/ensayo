package com.example.ensayo.ui.repertorio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
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
    onSongClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RepertorioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RepertorioContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onKeySelected = viewModel::onKeySelected,
        onSongClick = onSongClick,
        modifier = modifier
    )
}

@Composable
fun RepertorioContent(
    uiState: RepertorioUiState,
    onQueryChange: (String) -> Unit,
    onKeySelected: (String) -> Unit,
    onSongClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        RepertorioUiState.Loading -> Box(modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        RepertorioUiState.Empty -> CenteredText("Todavía no hay canciones", modifier)
        is RepertorioUiState.Error -> CenteredText(uiState.message, modifier)
        is RepertorioUiState.Success -> SongList(
            state = uiState,
            onQueryChange = onQueryChange,
            onKeySelected = onKeySelected,
            onSongClick = onSongClick,
            modifier = modifier
        )
    }
}

@Composable
private fun SongList(
    state: RepertorioUiState.Success,
    onQueryChange: (String) -> Unit,
    onKeySelected: (String) -> Unit,
    onSongClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            label = { Text("Buscar por título o artista") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.availableKeys) { key ->
                FilterChip(
                    selected = key == state.selectedKey,
                    onClick = { onKeySelected(key) },
                    label = { Text(key) }
                )
            }
        }
        if (state.songs.isEmpty()) {
            CenteredText("Sin resultados")
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.songs, key = { it.id }) { song ->
                    ListItem(
                        headlineContent = { Text(song.title) },
                        supportingContent = { Text(song.artist) },
                        trailingContent = { Text(song.musicalKey) },
                        modifier = Modifier.clickable { onSongClick(song.id) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun CenteredText(text: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
