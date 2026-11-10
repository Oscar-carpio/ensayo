package com.example.ensayo.domain.model

import java.util.UUID

data class Song(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val artist: String = "",
    val musicalKey: String,
    val bpm: Int? = null,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
