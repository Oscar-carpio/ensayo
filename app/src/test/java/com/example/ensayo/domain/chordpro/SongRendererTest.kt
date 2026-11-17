package com.example.ensayo.domain.chordpro

import org.junit.Assert.assertEquals
import org.junit.Test

class SongRendererTest {

    @Test
    fun `transpone y luego convierte la notacion`() {
        val lines = renderChordPro("[G]Hola", semitones = 2, notation = Notation.LATIN)
        assertEquals("La", lines.first().chords)
        assertEquals("Hola", lines.first().lyrics)
    }

    @Test
    fun `el tono tambien se transpone y convierte`() {
        assertEquals("La", renderKey("G", semitones = 2, notation = Notation.LATIN))
    }
}
