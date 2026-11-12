package com.example.ensayo.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ensayo.domain.chordpro.Notation
import com.example.ensayo.domain.repository.PreferencesRepository
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStorePreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    private val safeData: Flow<Preferences> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }

    override val fontSize: Flow<Int> = safeData
        .map { prefs -> prefs[FONT_SIZE] ?: PreferencesRepository.DEFAULT_FONT_SIZE }

    override val notation: Flow<Notation> = safeData
        .map { prefs -> prefs[NOTATION].toNotation() }

    override suspend fun setFontSize(size: Int) {
        val clamped = size.coerceIn(
            PreferencesRepository.MIN_FONT_SIZE,
            PreferencesRepository.MAX_FONT_SIZE
        )
        dataStore.edit { prefs -> prefs[FONT_SIZE] = clamped }
    }

    override suspend fun setNotation(notation: Notation) {
        dataStore.edit { prefs -> prefs[NOTATION] = notation.name }
    }

    private fun String?.toNotation(): Notation =
        Notation.entries.firstOrNull { it.name == this } ?: Notation.AMERICAN

    private companion object {
        val FONT_SIZE = intPreferencesKey("font_size")
        val NOTATION = stringPreferencesKey("notation")
    }
}
