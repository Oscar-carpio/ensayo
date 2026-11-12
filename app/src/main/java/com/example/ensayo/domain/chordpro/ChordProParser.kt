package com.example.ensayo.domain.chordpro

fun parseChordPro(content: String): List<ChordLine> = content.lines().map(::parseChordProLine)

fun parseChordProLine(line: String): ChordLine {
    val chords = StringBuilder()
    val lyrics = StringBuilder()
    var i = 0
    while (i < line.length) {
        if (line[i] == '[') {
            val end = line.indexOf(']', startIndex = i)
            if (end == -1) {
                lyrics.append(line.substring(i))
                break
            }
            if (chords.isNotEmpty() && chords.length >= lyrics.length) chords.append(' ')
            while (chords.length < lyrics.length) chords.append(' ')
            chords.append(line.substring(i + 1, end))
            i = end + 1
        } else {
            lyrics.append(line[i])
            i++
        }
    }
    return ChordLine(chords = chords.toString(), lyrics = lyrics.toString())
}
