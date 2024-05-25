package com.kusius.movies.core.domain.usecase

import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.Locale

/**
 * Formats date as given by the movie DB api into the display format of our application which is
 * Day(Number) Month(String) Year(Number) (ex 6 May 2007)
 */
class FormatDateUseCase() {
    operator fun invoke(apiDateString: String): String {
        return try {
            // the api gives date in almos iso format. We append the hour 0 to convert to iso format
            // we only care about month day and year either way.
            val isoTime = "${apiDateString}T00:00:00"
            val localDateTime = LocalDateTime.parse(isoTime)
            "${localDateTime.dayOfMonth} ${localDateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${localDateTime.year}"
        } catch (e: DateTimeParseException) {
            println("FormatDateUseCase Error: ${e.message}")
            // leave it unaltered
            return apiDateString
        }
    }
}