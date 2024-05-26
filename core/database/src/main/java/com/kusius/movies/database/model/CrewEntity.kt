package com.kusius.movies.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "crew",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = arrayOf("movieId"),
            childColumns = arrayOf("crewMovieId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CrewEntity(
    @PrimaryKey(autoGenerate = true)
    val crewId: Int = 0,
    val crewMovieId: Int,
    val name: String,
    val job: String
)
