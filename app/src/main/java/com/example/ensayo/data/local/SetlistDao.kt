package com.example.ensayo.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SetlistDao {
    @Query("SELECT * FROM setlists WHERE deleted = 0 ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<SetlistEntity>>

    @Query("SELECT * FROM setlists WHERE id = :id")
    fun observeById(id: String): Flow<SetlistEntity?>

    @Query(
        """
        SELECT songs.*, setlist_songs.position AS position, setlist_songs.semitones AS semitones
        FROM setlist_songs
        INNER JOIN songs ON songs.id = setlist_songs.songId
        WHERE setlist_songs.setlistId = :setlistId AND songs.deleted = 0
        ORDER BY setlist_songs.position
        """
    )
    fun observeSongs(setlistId: String): Flow<List<SetlistSongWithSong>>

    @Upsert
    suspend fun upsert(setlist: SetlistEntity)

    @Query("UPDATE setlists SET deleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun markDeleted(id: String, timestamp: Long)

    @Upsert
    suspend fun upsertSongs(items: List<SetlistSongEntity>)

    @Query("DELETE FROM setlist_songs WHERE setlistId = :setlistId AND songId = :songId")
    suspend fun removeSong(setlistId: String, songId: String)

    @Query(
        "SELECT COALESCE(MAX(position), -1) + 1 FROM setlist_songs WHERE setlistId = :setlistId"
    )
    suspend fun nextPosition(setlistId: String): Int
}
