package com.example.ensayo.domain.chordpro

object Transposer {
    private val SHARPS = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val FLATS = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")
    private val ROOT = Regex("(^|/)([A-G][#b]?)")
    private val CHORD_TAG = Regex("\\[([^\\]]+)]")

    fun transposeNote(note: String, semitones: Int, useFlats: Boolean = false): String {
        val index = SHARPS.indexOf(note).takeIf { it >= 0 } ?: FLATS.indexOf(note)
        if (index < 0) return note
        val newIndex = (index + semitones).mod(12)
        return if (useFlats) FLATS[newIndex] else SHARPS[newIndex]
    }

    fun transposeChord(chord: String, semitones: Int, useFlats: Boolean = false): String =
        ROOT.replace(chord) { match ->
            match.groupValues[1] + transposeNote(match.groupValues[2], semitones, useFlats)
        }

    fun transposeChordPro(content: String, semitones: Int, useFlats: Boolean = false): String =
        CHORD_TAG.replace(content) { match ->
            "[" + transposeChord(match.groupValues[1], semitones, useFlats) + "]"
        }
}
