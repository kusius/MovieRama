package com.kusius.movies.core.model

data class MovieDetails(
    val summary: MovieSummary,
    val genres: List<MovieGenre>,
    val overview: String,
    val directorName: String,
    val castNames: List<String>,
    val reviews: List<MovieReview>,
)