package com.example.ensayo.domain.chordpro

import org.junit.Assert.assertEquals
import org.junit.Test

class ChordProParserTest {

    @Test
    fun `coloca cada acorde encima de su silaba`() {
        val line = parseChordProLine("[G]Amazing [C]grace")
        assertEquals("G       C", line.chords)
        assertEquals("Amazing grace", line.lyrics)
    }

    @Test
    fun `una linea sin acordes solo tiene letra`() {
        val line = parseChordProLine("Hola")
        assertEquals("", line.chords)
        assertEquals("Hola", line.lyrics)
    }

    @Test
    fun `separa dos acordes seguidos con un espacio`() {
        val line = parseChordProLine("[G][D]Hola")
        assertEquals("G D", line.chords)
        assertEquals("Hola", line.lyrics)
    }

    @Test
    fun `acorde al final de la linea`() {
        val line = parseChordProLine("Hola[G]")
        assertEquals("    G", line.chords)
        assertEquals("Hola", line.lyrics)
    }

    @Test
    fun `un corchete sin cerrar se trata como letra`() {
        val line = parseChordProLine("Hola [G")
        assertEquals("", line.chords)
        assertEquals("Hola [G", line.lyrics)
    }

    @Test
    fun `parsea cada linea del texto`() {
        assertEquals(3, parseChordPro("[G]Uno\nDos\n[C]Tres").size)
    }
}
