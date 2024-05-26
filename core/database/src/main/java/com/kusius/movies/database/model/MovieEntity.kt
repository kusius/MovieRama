package com.kusius.movies.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kusius.movies.core.model.MovieSummary

@Entity(
    tableName = "movies"
)
data class MovieEntity(
    @PrimaryKey
    val movieId: Int,
    val posterUrl: String,
    val title: String,
    val releaseDate: String,
    val ratingOutOf10: Float,
    @ColumnInfo(defaultValue = "false")
    val isFavourite: Boolean,
    val popularity: Float,
    val isSearchResult: Boolean,
    val overview: String = ""
)

fun MovieEntity.asExternalModel() = MovieSummary(
    id = movieId,
    posterUrl = posterUrl,
    title = title,
    releaseDate = releaseDate,
    ratingOutOf10 = ratingOutOf10,
    isFavourite = isFavourite
)
