package com.kusius.movies.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines the general structure of paged results given by the TMDB API endpoints
 */
@Serializable
data class ApiResourceList<T>(
    val page: Int,
    val results: List<T>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)