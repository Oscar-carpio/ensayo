package com.example.ensayo.ui.directo

import com.example.ensayo.domain.model.SetlistItem

// Id de la canción que está `step` puestos más allá de la actual (+1 siguiente, -1 anterior).
// Devuelve null si la actual no está o si nos saldríamos de la lista.
fun List<SetlistItem>.songIdAt(currentId: String, step: Int): String? {
    val index = indexOfFirst { it.song.id == currentId }
    if (index < 0) return null
    return getOrNull(index + step)?.song?.id
}
