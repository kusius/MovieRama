package com.workable.movierama.core.network.model

import com.workable.movierama.core.model.MovieReview
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovieReview(
    val author: String,
    val content: String,
)

fun NetworkMovieReview.asExternalModel(): MovieReview {
    return MovieReview(
        authorName = author,
        content = content
    )
}