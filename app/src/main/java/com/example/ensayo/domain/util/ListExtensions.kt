package com.example.ensayo.domain.util

fun <T> List<T>.moved(from: Int, to: Int): List<T> {
    if (from !in indices || to !in indices || from == to) return this
    val result = toMutableList()
    result.add(to, result.removeAt(from))
    return result
}
