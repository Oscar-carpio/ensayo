package com.example.ensayo.domain.chordpro

fun renderChordPro(content: String, semitones: Int, notation: Notation): List<ChordLine> {
    val transposed = Transposer.transposeChordPro(content, semitones)
    val converted = ChordNotation.convertChordPro(transposed, notation)
    return parseChordPro(converted)
}

fun renderKey(key: String, semitones: Int, notation: Notation): String =
    ChordNotation.convertChord(Transposer.transposeChord(key, semitones), notation)
