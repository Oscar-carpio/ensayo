package com.example.ensayo.ui.setlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ensayo.domain.model.Setlist

@Composable
fun SetlistsScreen(
    onSetlistClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SetlistsViewModel = hiltViewModel()
) {
    val setlists by viewModel.setlists.collectAsStateWithLifecycle()
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        SetlistsContent(
            setlists = setlists,
            onSetlistClick = onSetlistClick,
            onDelete = viewModel::delete,
            modifier = Modifier.fillMaxSize()
        )
        ExtendedFloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+ Nuevo")
        }
    }

    if (showDialog) {
        NewSetlistDialog(
            onConfirm = { name ->
                viewModel.create(name)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun SetlistsContent(
    setlists: List<Setlist>,
    onSetlistClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (setlists.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "Todavía no hay setlists.\nCrea uno con «+ Nuevo».",
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(setlists, key = { it.id }) { setlist ->
                Column {
                    ListItem(
                        headlineContent = { Text(setlist.name) },
                        trailingContent = {
                            TextButton(onClick = { onDelete(setlist.id) }) { Text("Borrar") }
                        },
                        modifier = Modifier.clickable { onSetlistClick(setlist.id) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun NewSetlistDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo setlist") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre (ej. Culto domingo)") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) {
                Text("Crear")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
