package com.workable.movierama.core.network.ktor

import io.ktor.resources.Resource

@Resource("/popular")
class NetworkPopularResource(
    val page: Int,
    val language: String = "en-US",
    val region: String? = null,
)
