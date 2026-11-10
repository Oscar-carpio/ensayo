package com.example.ensayo.data.repository

import com.example.ensayo.data.local.SongDao
import com.example.ensayo.data.local.SongEntity
import com.example.ensayo.domain.model.Song
import com.example.ensayo.domain.repository.SongRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineSongRepository @Inject constructor(private val dao: SongDao) : SongRepository {
    override fun observeSongs(): Flow<List<Song>> = dao.observeAll().map { list ->
        list.map { it.toDomain() }
    }

    override fun observeSong(id: String): Flow<Song?> = dao.observeById(id).map { it?.toDomain() }

    override suspend fun save(song: Song) =
        dao.upsert(song.toEntity(updatedAt = System.currentTimeMillis()))

    override suspend fun delete(id: String) = dao.markDeleted(id, System.currentTimeMillis())
}

private fun SongEntity.toDomain() = Song(
    id = id,
    title = title,
    artist = artist,
    musicalKey = musicalKey,
    bpm = bpm,
    content = content,
    createdAt = createdAt
)

private fun Song.toEntity(updatedAt: Long) = SongEntity(
    id = id,
    title = title,
    artist = artist,
    musicalKey = musicalKey,
    bpm = bpm,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt
)
