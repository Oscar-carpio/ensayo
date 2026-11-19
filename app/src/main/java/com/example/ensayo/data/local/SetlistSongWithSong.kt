package com.example.ensayo.data.local

import androidx.room.Embedded

data class SetlistSongWithSong(
    @Embedded val song: SongEntity,
    val position: Int,
    val semitones: Int
)
