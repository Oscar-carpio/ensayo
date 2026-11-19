package com.example.ensayo.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `setlists` (
                `id` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `date` INTEGER,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                `deleted` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `setlist_songs` (
                `setlistId` TEXT NOT NULL,
                `songId` TEXT NOT NULL,
                `position` INTEGER NOT NULL,
                `semitones` INTEGER NOT NULL,
                PRIMARY KEY(`setlistId`, `songId`),
                FOREIGN KEY(`setlistId`) REFERENCES `setlists`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`songId`) REFERENCES `songs`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_setlist_songs_songId` ON `setlist_songs` (`songId`)"
        )
    }
}
