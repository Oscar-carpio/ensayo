package com.example.ensayo.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val fontSize: Flow<Int>

    suspend fun setFontSize(size: Int)

    companion object {
        const val DEFAULT_FONT_SIZE = 16
        const val MIN_FONT_SIZE = 12
        const val MAX_FONT_SIZE = 28
    }
}
