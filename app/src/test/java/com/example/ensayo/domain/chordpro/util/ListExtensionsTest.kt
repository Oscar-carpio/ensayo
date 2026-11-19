package com.example.ensayo.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ListExtensionsTest {

    private val list = listOf("A", "B", "C", "D")

    @Test
    fun `mueve un elemento hacia abajo`() {
        assertEquals(listOf("B", "A", "C", "D"), list.moved(0, 1))
    }

    @Test
    fun `mueve un elemento hacia arriba`() {
        assertEquals(listOf("A", "D", "B", "C"), list.moved(3, 1))
    }

    @Test
    fun `fuera de rango no cambia nada`() {
        assertEquals(list, list.moved(0, 4))
        assertEquals(list, list.moved(-1, 2))
    }

    @Test
    fun `misma posicion no cambia nada`() {
        assertEquals(list, list.moved(2, 2))
    }
}
