package com.kusius.movies.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cast",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = arrayOf("movieId"),
            childColumns = arrayOf("castMovieId"),
            onDelete = ForeignKey.CASCADE
        )
    ])
data class CastEntity(
    @PrimaryKey(autoGenerate = true)
    val castId: Int = 0,
    val castMovieId: Int,
    val name: String,
)
