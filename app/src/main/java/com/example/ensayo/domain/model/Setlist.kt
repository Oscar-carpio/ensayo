package com.example.ensayo.domain.model

import java.util.UUID

data class Setlist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
