package com.example.ensayo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SongEntity::class, SetlistEntity::class, SetlistSongEntity::class],
    version = 2,
    exportSchema = true
)
abstract class EnsayoDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    abstract fun setlistDao(): SetlistDao
}
