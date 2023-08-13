package com.workable.movierama.core.model

data class Movie(
    val posterUrl: String,
    val title: String,
    val releaseDate: String,
    val rating: Float,
    val isFavourite: Boolean,
)
