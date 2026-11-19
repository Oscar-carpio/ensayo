package com.example.ensayo.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        EnsayoDatabase::class.java
    )

    @Test
    fun migrate1To2KeepsSongs() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                """
                INSERT INTO songs
                (id, title, artist, musicalKey, bpm, content, createdAt, updatedAt, deleted)
                VALUES ('song-1', 'Amazing Grace', 'John Newton', 'G', 72, '[G]Amazing', 0, 0, 0)
                """.trimIndent()
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        db.query("SELECT title FROM songs WHERE id = 'song-1'").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("Amazing Grace", cursor.getString(0))
        }
    }

    @Test
    fun migrate1To2CreatesSetlistTables() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                """
                INSERT INTO songs
                (id, title, artist, musicalKey, bpm, content, createdAt, updatedAt, deleted)
                VALUES ('song-1', 'Amazing Grace', '', 'G', NULL, '', 0, 0, 0)
                """.trimIndent()
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        db.execSQL(
            "INSERT INTO setlists (id, name, createdAt, updatedAt, deleted) " +
                "VALUES ('set-1', 'Culto domingo', 0, 0, 0)"
        )
        db.execSQL(
            "INSERT INTO setlist_songs (setlistId, songId, position, semitones) " +
                "VALUES ('set-1', 'song-1', 0, 2)"
        )
        db.query("SELECT semitones FROM setlist_songs WHERE setlistId = 'set-1'").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(2, cursor.getInt(0))
        }
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}
