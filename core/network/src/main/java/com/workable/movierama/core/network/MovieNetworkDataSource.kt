package com.workable.movierama.core.network

import com.workable.movierama.core.network.model.NetworkMovie

interface MovieNetworkDataSource {
    suspend fun getPopularMovies(page: Int): List<NetworkMovie>

    suspend fun searchMovies(query: String, page: Int): List<NetworkMovie>
}