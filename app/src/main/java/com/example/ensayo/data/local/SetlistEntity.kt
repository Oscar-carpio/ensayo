package com.example.ensayo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "setlists")
data class SetlistEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)
