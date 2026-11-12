package com.example.ensayo.ui.editor

data class EditorUiState(
    val title: String = "",
    val artist: String = "",
    val musicalKey: String = "",
    val bpm: String = "",
    val content: String = "",
    val isNew: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false
) {
    val canSave: Boolean
        get() = title.isNotBlank() && musicalKey.isNotBlank() && !isSaving
}
