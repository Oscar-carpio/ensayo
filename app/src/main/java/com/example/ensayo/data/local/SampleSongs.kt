package com.example.ensayo.data.local

object SampleSongs {
    val all = listOf(
        SongEntity(
            title = "Amazing Grace",
            artist = "John Newton",
            musicalKey = "G",
            bpm = 72,
            content = """
                [G]Amazing [G7]grace, how [C]sweet the [G]sound
                That saved a wretch like [D]me
                I [G]once was [G7]lost, but [C]now am [G]found
                Was [Em]blind but [D]now I [G]see
            """.trimIndent()
        ),
        SongEntity(
            title = "Holy, Holy, Holy",
            artist = "Reginald Heber",
            musicalKey = "D",
            bpm = 84,
            content = """
                [D]Holy, holy, [Bm]holy! [G]Lord God Al[D]mighty!
                [A]Early in the [D]morning our [E]song shall rise to [A]Thee
            """.trimIndent()
        ),
        SongEntity(
            title = "Be Thou My Vision",
            artist = "Traditional Irish",
            musicalKey = "D",
            bpm = 90,
            content = """
                [D]Be Thou my [G]vision, O [D]Lord of my [A]heart
                [D]Naught be all [G]else to me, [A]save that Thou [D]art
            """.trimIndent()
        )
    )
}
