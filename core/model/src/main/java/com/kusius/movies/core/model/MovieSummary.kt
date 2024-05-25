package com.kusius.movies.core.model

data class MovieSummary(
    val id: Int,
    val posterUrl: String,
    val title: String,
    val releaseDate: String,
    val ratingOutOf10: Float,
    val isFavourite: Boolean,
)
