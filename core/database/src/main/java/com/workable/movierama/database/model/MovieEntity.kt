package com.workable.movierama.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.workable.movierama.core.model.MovieSummary

@Entity(
    tableName = "movies"
)
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val posterUrl: String,
    val title: String,
    val releaseDate: String,
    val ratingOutOf10: Float,
    @ColumnInfo(defaultValue = "false")
    val isFavourite: Boolean,
    val popularity: Float,
    val isSearchResult: Boolean
)

fun MovieEntity.asExternalModel() = MovieSummary(
    id = id,
    posterUrl = posterUrl,
    title = title,
    releaseDate = releaseDate,
    ratingOutOf10 = ratingOutOf10,
    isFavourite = isFavourite
)
