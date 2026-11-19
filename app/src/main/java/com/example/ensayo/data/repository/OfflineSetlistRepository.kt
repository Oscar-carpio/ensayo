package com.example.ensayo.data.repository

import com.example.ensayo.data.local.SetlistDao
import com.example.ensayo.data.local.SetlistEntity
import com.example.ensayo.data.local.SetlistSongEntity
import com.example.ensayo.domain.model.Setlist
import com.example.ensayo.domain.model.SetlistItem
import com.example.ensayo.domain.repository.SetlistRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineSetlistRepository @Inject constructor(private val dao: SetlistDao) :
    SetlistRepository {

    override fun observeSetlists(): Flow<List<Setlist>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeSetlist(id: String): Flow<Setlist?> =
        dao.observeById(id).map { it?.toDomain() }

    override fun observeItems(setlistId: String): Flow<List<SetlistItem>> =
        dao.observeSongs(setlistId).map { list ->
            list.map { row ->
                SetlistItem(
                    song = row.song.toDomain(),
                    position = row.position,
                    semitones = row.semitones
                )
            }
        }

    override suspend fun save(setlist: Setlist) {
        dao.upsert(setlist.toEntity(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun delete(id: String) {
        dao.markDeleted(id, System.currentTimeMillis())
    }

    override suspend fun addSong(setlistId: String, songId: String, semitones: Int) {
        val position = dao.nextPosition(setlistId)
        dao.upsertSongs(listOf(SetlistSongEntity(setlistId, songId, position, semitones)))
    }

    override suspend fun removeSong(setlistId: String, songId: String) {
        dao.removeSong(setlistId, songId)
    }

    override suspend fun moveSong(setlistId: String, from: Int, to: Int) {
        dao.move(setlistId, from, to)
    }
}

private fun SetlistEntity.toDomain() = Setlist(
    id = id,
    name = name,
    date = date,
    createdAt = createdAt
)

private fun Setlist.toEntity(updatedAt: Long) = SetlistEntity(
    id = id,
    name = name,
    date = date,
    createdAt = createdAt,
    updatedAt = updatedAt
)
