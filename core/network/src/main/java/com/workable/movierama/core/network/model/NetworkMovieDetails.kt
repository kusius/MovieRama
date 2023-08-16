package com.workable.movierama.core.network.model

import com.workable.movierama.core.model.MovieDetails
import com.workable.movierama.core.model.MovieGenre
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovieDetails(
    val id: Int,
    val genres: List<NetworkMovieGenre>,
    val title: String,

    @SerialName("vote_average")
    val rating: Float,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("backdrop_path")
    val backdropUrl: String,

    val overview: String,
    val credits: NetworkMovieCredits,
)

@Serializable
data class NetworkMovieGenre(
    val id: Int,
    val name: String
)

@Serializable
data class NetworkMovieCast(
    val name: String,
)

@Serializable
data class NetworkMovieCrew(
    val name: String,
    val job: String
)

@Serializable
data class NetworkMovieCredits(
    val cast: List<NetworkMovieCast>,
    val crew: List<NetworkMovieCrew>
)

fun NetworkMovieGenre.asExternalModel(): MovieGenre =
    MovieGenre(id = id, name = name)