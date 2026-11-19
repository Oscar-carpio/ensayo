package com.example.ensayo.domain.repository

import com.example.ensayo.domain.model.Setlist
import com.example.ensayo.domain.model.SetlistItem
import kotlinx.coroutines.flow.Flow

interface SetlistRepository {
    fun observeSetlists(): Flow<List<Setlist>>

    fun observeSetlist(id: String): Flow<Setlist?>

    fun observeItems(setlistId: String): Flow<List<SetlistItem>>

    suspend fun save(setlist: Setlist)

    suspend fun delete(id: String)

    suspend fun addSong(setlistId: String, songId: String, semitones: Int = 0)

    suspend fun removeSong(setlistId: String, songId: String)
}
