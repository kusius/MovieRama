package com.kusius.movies.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kusius.movies.core.model.MovieGenre

@Entity(
    tableName = "genre"
)
data class GenreEntity(
    @PrimaryKey
    val genreId: Int,
    val name: String
)

fun GenreEntity.asExternalModel(): MovieGenre {
    return MovieGenre(
        id = this.genreId,
        name = this.name
    )
}