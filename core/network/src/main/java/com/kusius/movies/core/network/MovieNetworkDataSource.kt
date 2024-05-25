package com.kusius.movies.core.network

import com.kusius.movies.core.network.model.NetworkMovie
import com.kusius.movies.core.network.model.NetworkMovieDetails
import com.kusius.movies.core.network.model.NetworkMovieReview

interface MovieNetworkDataSource {
    suspend fun getPopularMovies(page: Int): List<NetworkMovie>

    suspend fun searchMovies(query: String, page: Int): List<NetworkMovie>

    suspend fun getMovieDetails(id: Int): NetworkMovieDetails

    suspend fun getMovieReviews(id: Int, page: Int): List<NetworkMovieReview>

    suspend fun getMovieSimilar(id: Int, page: Int): List<NetworkMovie>
}