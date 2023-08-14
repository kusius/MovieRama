package com.workable.core.domain

import com.workable.core.domain.usecase.FormatDateUseCase
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DateTimeTest {
    @Test
    fun dateTimeCorrectlyFormatted() {
        val expected = "6 May 2021"
        val apiString = "2021-05-06"
        val actual = FormatDateUseCase().invoke(apiString)
        assertEquals(expected, actual)
    }
}