package com.workable.movierama.core.network.model

import com.workable.movierama.core.model.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovie(
    val id: Int,
    @SerialName("poster_path")
    val posterUrl: String,
    @SerialName("original_title")
    val originalTitle: String,
    @SerialName("vote_average")
    val rating: Float,
    @SerialName("release_date")
    val releaseDate: String,
)

fun NetworkMovie.asExternalModel(): Movie =
    Movie(
        posterUrl = this.posterUrl,
        title = this.originalTitle,
        releaseDate = this.releaseDate,
        rating = this.rating,
        isFavourite = false
)