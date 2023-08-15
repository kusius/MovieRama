package com.workable.movierama.core.network.ktor

import io.ktor.resources.Resource

@Resource("/movie/popular")
class NetworkPopularResource(
    val page: Int,
    val language: String = "en-US",
    val region: String? = null,
)

@Resource("/search/movie")
class NetworkSearchResource(
    val query: String,
    val page: Int = 1,
    val language: String = "en-US"
)
