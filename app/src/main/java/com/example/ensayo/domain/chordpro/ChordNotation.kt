package com.example.ensayo.domain.chordpro

object ChordNotation {
    private val LATIN_NAMES = mapOf(
        "C" to "Do",
        "D" to "Re",
        "E" to "Mi",
        "F" to "Fa",
        "G" to "Sol",
        "A" to "La",
        "B" to "Si"
    )
    private val ROOT = Regex("(^|/)([A-G])")
    private val CHORD_TAG = Regex("\\[([^\\]]+)]")

    fun convertChord(chord: String, notation: Notation): String {
        if (notation == Notation.AMERICAN) return chord
        return ROOT.replace(chord) { match ->
            match.groupValues[1] + LATIN_NAMES.getValue(match.groupValues[2])
        }
    }

    fun convertChordPro(content: String, notation: Notation): String {
        if (notation == Notation.AMERICAN) return content
        return CHORD_TAG.replace(content) { match ->
            "[" + convertChord(match.groupValues[1], notation) + "]"
        }
    }
}
