package com.workable.movierama.core.model

data class Movie(
    val id: Int,
    val posterUrl: String,
    val title: String,
    val releaseDate: String,
    val ratingOutOf10: Float,
    val isFavourite: Boolean,
)
