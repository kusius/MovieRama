package com.kusius.movies.core.domain.usecase

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DateTimeTest {
    @Test
    fun datetime_correctly_formatted() {
        val expected = "6 May 2021"
        val apiString = "2021-05-06"
        val actual = FormatDateUseCase().invoke(apiString)
        assertEquals(expected, actual)
    }

    @Test
    fun returns_input_when_cannot_parse() {
        val expected = "invalid input"
        val apiString = "invalid input"
        val actual = FormatDateUseCase().invoke(apiString)
        assertEquals(expected, actual)
    }
}