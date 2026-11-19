package com.example.ensayo.testing

import com.example.ensayo.domain.model.Song
import com.example.ensayo.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeSongRepository : SongRepository {
    private val songs = MutableStateFlow<List<Song>>(emptyList())

    fun emit(list: List<Song>) {
        songs.value = list
    }

    override fun observeSongs(): Flow<List<Song>> = songs

    override fun observeSong(id: String): Flow<Song?> =
        songs.map { list -> list.find { it.id == id } }

    override suspend fun save(song: Song) {
        songs.update { list -> list.filterNot { it.id == song.id } + song }
    }

    override suspend fun delete(id: String) {
        songs.update { list -> list.filterNot { it.id == id } }
    }
}
