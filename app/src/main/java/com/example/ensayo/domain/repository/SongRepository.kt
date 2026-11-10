package com.example.ensayo.domain.repository

import com.example.ensayo.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun observeSongs(): Flow<List<Song>>
    fun observeSong(id: String): Flow<Song?>
    suspend fun save(song: Song)
    suspend fun delete(id: String)
}
