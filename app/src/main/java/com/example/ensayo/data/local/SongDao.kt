package com.example.ensayo.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs WHERE deleted = 0 ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id AND deleted = 0")
    fun observeById(id: String): Flow<SongEntity?>

    @Upsert
    suspend fun upsert(song: SongEntity)

    @Query("UPDATE songs SET deleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun markDeleted(id: String, timestamp: Long)
}
