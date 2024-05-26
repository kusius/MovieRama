package com.kusius.movies.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "genre"
)
data class GenreEntity(
    @PrimaryKey
    val genreId: Int,
    val name: String
)