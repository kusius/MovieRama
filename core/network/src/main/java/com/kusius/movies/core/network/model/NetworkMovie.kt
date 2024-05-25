package com.kusius.movies.core.network.model

import com.kusius.movies.core.model.MovieSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovie(
    val id: Int,
    @SerialName("backdrop_path")
    private val backdropPath: String? = "",
    val backdropUrl: String = "${IMAGE_BASE_URL}${backdropPath}" ,
    @SerialName("title")
    val title: String,
    @SerialName("vote_average")
    val rating: Float,
    @SerialName("release_date")
    val releaseDate: String,
    val popularity: Float,
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