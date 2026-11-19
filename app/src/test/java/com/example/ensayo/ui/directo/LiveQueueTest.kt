package com.example.ensayo.ui.directo

import com.example.ensayo.domain.model.SetlistItem
import com.example.ensayo.domain.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LiveQueueTest {

    private val first = item("Amazing Grace", 0)
    private val second = item("Holy Holy Holy", 1)
    private val third = item("Be Thou My Vision", 2)
    private val queue = listOf(first, second, third)

    @Test
    fun `siguiente devuelve la cancion de despues`() {
        assertEquals(third.song.id, queue.songIdAt(second.song.id, 1))
    }

    @Test
    fun `anterior devuelve la cancion de antes`() {
        assertEquals(first.song.id, queue.songIdAt(second.song.id, -1))
    }

    @Test
    fun `no pasa de la ultima ni de la primera`() {
        assertNull(queue.songIdAt(third.song.id, 1))
        assertNull(queue.songIdAt(first.song.id, -1))
    }

    @Test
    fun `cancion que no esta en la cola devuelve null`() {
        assertNull(queue.songIdAt("no-existe", 1))
    }

    private fun item(title: String, position: Int) = SetlistItem(
        song = Song(title = title, musicalKey = "G", content = ""),
        position = position,
        semitones = 0
    )
}
