package com.workable.movierama.core.network.fake

import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.core.network.model.NetworkMovie

class FakeMovieNetworkDataSource : MovieNetworkDataSource {
    override suspend fun getPopularMovies(page: Int): List<NetworkMovie> {
        return buildList {
            repeat(10) { index ->
                val id = page * 10 + index
                add(
                    NetworkMovie(
                        id = id,
                        backdropUrl = "www.example.com/image.jpg",
                        originalTitle = "Movie ${id}",
                        rating = 5.0f,
                        releaseDate = "11/11/11",
                    )
                )
            }
        }
    }

    override suspend fun searchMovies(query: String, page: Int): List<NetworkMovie> {
        return buildList {
            repeat(10) { index ->
                val id = page * 10 + index
                add(
                    NetworkMovie(
                        id = id,
                        backdropUrl = "www.example.com/image.jpg",
                        originalTitle = "Movie ${id}",
                        rating = 5.0f,
                        releaseDate = "11/11/11",
                    )
                )
            }
        }
    }
}