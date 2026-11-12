package com.example.ensayo.domain.chordpro

import org.junit.Assert.assertEquals
import org.junit.Test

class TransposerTest {

    @Test
    fun `sube una nota dos semitonos`() {
        assertEquals("D", Transposer.transposeNote("C", 2))
    }

    @Test
    fun `da la vuelta de B a C`() {
        assertEquals("C", Transposer.transposeNote("B", 1))
    }

    @Test
    fun `baja semitonos con valores negativos`() {
        assertEquals("B", Transposer.transposeNote("C", -1))
    }

    @Test
    fun `usa bemoles si se pide`() {
        assertEquals("Db", Transposer.transposeNote("C", 1, useFlats = true))
    }

    @Test
    fun `acepta bemoles como entrada`() {
        assertEquals("C", Transposer.transposeNote("Bb", 2))
    }

    @Test
    fun `mantiene el sufijo del acorde`() {
        assertEquals("Bm7", Transposer.transposeChord("Am7", 2))
    }

    @Test
    fun `transpone los dos lados de un acorde con barra`() {
        assertEquals("D/F#", Transposer.transposeChord("C/E", 2))
    }

    @Test
    fun `no toca lo que no es un acorde`() {
        assertEquals("N.C.", Transposer.transposeChord("N.C.", 2))
    }

    @Test
    fun `transpone solo lo que va entre corchetes`() {
        val result = Transposer.transposeChordPro("[G]Amazing [C]grace", 2)
        assertEquals("[A]Amazing [D]grace", result)
    }
}
