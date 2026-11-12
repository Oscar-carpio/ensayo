package com.example.ensayo.ui.repertorio

import com.example.ensayo.domain.model.Song
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterSongsTest {

    private val amazing =
        Song(title = "Amazing Grace", artist = "John Newton", musicalKey = "G", content = "")
    private val holy =
        Song(title = "Holy Holy Holy", artist = "Reginald Heber", musicalKey = "D", content = "")
    private val vision = Song(title = "Be Thou My Vision", musicalKey = "G", content = "")
    private val songs = listOf(amazing, holy, vision)

    @Test
    fun `sin filtros devuelve todas`() {
        assertEquals(songs, filterSongs(songs, query = "", key = null))
    }

    @Test
    fun `busca por titulo sin distinguir mayusculas`() {
        assertEquals(listOf(amazing), filterSongs(songs, query = "amazing", key = null))
    }

    @Test
    fun `busca tambien por autor`() {
        assertEquals(listOf(holy), filterSongs(songs, query = "heber", key = null))
    }

    @Test
    fun `filtra por tono`() {
        assertEquals(listOf(amazing, vision), filterSongs(songs, query = "", key = "G"))
    }

    @Test
    fun `combina busqueda y tono`() {
        assertEquals(listOf(vision), filterSongs(songs, query = "vision", key = "G"))
    }

    @Test
    fun `devuelve lista vacia si nada coincide`() {
        assertEquals(emptyList<Song>(), filterSongs(songs, query = "xyz", key = null))
    }
}
