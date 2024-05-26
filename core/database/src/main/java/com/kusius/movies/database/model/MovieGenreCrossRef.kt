package com.kusius.movies.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["movieId", "genreId"])
data class MovieGenreCrossRef(
    val genreId: Int,
    val movieId: Int
)
