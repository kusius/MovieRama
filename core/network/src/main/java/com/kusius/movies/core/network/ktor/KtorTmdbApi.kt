package com.kusius.movies.core.network.ktor

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

@Resource("/movie")
class NetworkMovieDetailResource() {
    @Resource("{id}")
    class Id(val parent: NetworkMovieDetailResource = NetworkMovieDetailResource(), val id: Int) {
        @Resource("/reviews")
        class Reviews(val parent: Id, val page: Int)

        @Resource("/similar")
        class Similar(val parent: Id, val page: Int)
    }
}