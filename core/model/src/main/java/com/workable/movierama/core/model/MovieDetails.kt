package com.workable.movierama.core.model

data class MovieDetails(
    val id: Int,
    val genres: List<MovieGenre>,
    val title: String,
    val posterUrl: String,
    val overview: String,
    val directorName: String,
    val castNames: List<String>,
    val reviews: List<MovieReview>,
    val isFavourite: Boolean
)