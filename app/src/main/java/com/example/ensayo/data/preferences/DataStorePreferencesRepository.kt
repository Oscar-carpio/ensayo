package com.example.ensayo.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.ensayo.domain.repository.PreferencesRepository
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStorePreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    override val fontSize: Flow<Int> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[FONT_SIZE] ?: PreferencesRepository.DEFAULT_FONT_SIZE }

    override suspend fun setFontSize(size: Int) {
        val clamped = size.coerceIn(
            PreferencesRepository.MIN_FONT_SIZE,
            PreferencesRepository.MAX_FONT_SIZE
        )
        dataStore.edit { prefs -> prefs[FONT_SIZE] = clamped }
    }

    private companion object {
        val FONT_SIZE = intPreferencesKey("font_size")
    }
}
