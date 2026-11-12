package com.example.ensayo.data.local

import javax.inject.Inject

class SampleDataSeeder @Inject constructor(private val dao: SongDao) {
    suspend fun seedIfEmpty() {
        if (dao.count() == 0) {
            SampleSongs.all.forEach { dao.upsert(it) }
        }
    }
}
