package com.workable.movierama.core.network.model

import com.workable.movierama.core.model.MovieSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original"
@Serializable
data class NetworkMovie(
    val id: Int,
    @SerialName("backdrop_path")
    val backdropUrl: String?,
    @SerialName("title")
    val title: String,
    @SerialName("vote_average")
    val rating: Float,
    @SerialName("release_date")
    val releaseDate: String,
)

fun NetworkMovie.asExternalModel(): MovieSummary =
    MovieSummary(
        id = this.id,
        posterUrl = "${IMAGE_BASE_URL}${this.backdropUrl}",
        title = this.title,
        releaseDate = this.releaseDate,
        ratingOutOf10 = this.rating,
        isFavourite = false
)