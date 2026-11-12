package com.example.ensayo.domain.chordpro

import org.junit.Assert.assertEquals
import org.junit.Test

class ChordNotationTest {

    @Test
    fun `convierte un acorde simple`() {
        assertEquals("Do", ChordNotation.convertChord("C", Notation.LATIN))
    }

    @Test
    fun `mantiene sufijos y alteraciones`() {
        assertEquals("Lam7", ChordNotation.convertChord("Am7", Notation.LATIN))
        assertEquals("Fa#m", ChordNotation.convertChord("F#m", Notation.LATIN))
        assertEquals("Sib", ChordNotation.convertChord("Bb", Notation.LATIN))
    }

    @Test
    fun `convierte los dos lados de un acorde con barra`() {
        assertEquals("Do/Mi", ChordNotation.convertChord("C/E", Notation.LATIN))
    }

    @Test
    fun `no toca lo que no es un acorde`() {
        assertEquals("N.C.", ChordNotation.convertChord("N.C.", Notation.LATIN))
    }

    @Test
    fun `en notacion americana no cambia nada`() {
        assertEquals("[G]Amazing", ChordNotation.convertChordPro("[G]Amazing", Notation.AMERICAN))
    }

    @Test
    fun `convierte solo lo que va entre corchetes`() {
        val result = ChordNotation.convertChordPro("[G]Amazing [C]grace", Notation.LATIN)
        assertEquals("[Sol]Amazing [Do]grace", result)
    }
}
