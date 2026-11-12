package com.example.ensayo.domain.repository

import com.example.ensayo.domain.chordpro.Notation
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val fontSize: Flow<Int>
    val notation: Flow<Notation>

    suspend fun setFontSize(size: Int)

    suspend fun setNotation(notation: Notation)

    companion object {
        const val DEFAULT_FONT_SIZE = 16
        const val MIN_FONT_SIZE = 12
        const val MAX_FONT_SIZE = 28
    }
}
