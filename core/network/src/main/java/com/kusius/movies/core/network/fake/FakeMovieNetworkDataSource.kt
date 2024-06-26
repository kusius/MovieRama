package com.kusius.movies.core.network.fake

import com.kusius.movies.core.network.MovieNetworkDataSource
import com.kusius.movies.core.network.model.NetworkMovie
import com.kusius.movies.core.network.model.NetworkMovieCredits
import com.kusius.movies.core.network.model.NetworkMovieDetails
import com.kusius.movies.core.network.model.NetworkMovieReview

class FakeMovieNetworkDataSource : MovieNetworkDataSource {
    override suspend fun getPopularMovies(page: Int): List<NetworkMovie> {
        return buildList {
            repeat(10) { index ->
                val id = page * 10 + index
                add(
                    NetworkMovie(
                        id = id,
                        backdropUrl = "www.example.com/image.jpg",
                        title = "Movie ${id}",
                        rating = 5.0f,
                        releaseDate = "11/11/11",
                        popularity = 1f,
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
                        title = "Movie ${id}",
                        rating = 5.0f,
                        releaseDate = "22/11/11",
                        popularity = 1f,
                    )
                )
            }
        }
    }

    override suspend fun getMovieDetails(id: Int): NetworkMovieDetails {
        return NetworkMovieDetails(
            id = id,
            genres = emptyList(),
            title = "Movie Title $id",
            rating = 4.4f,
            releaseDate = "33/11/11",
            backdropUrl = "www.example.com",
            overview = "Nice Movie",
            credits = NetworkMovieCredits(emptyList(), emptyList()),
        )
    }


    override suspend fun getMovieReviews(id: Int, page: Int): List<NetworkMovieReview> {
        return listOf(
            NetworkMovieReview(
                author = "First Author",
                content = "First Content"
            ),
            NetworkMovieReview(
                author = "Second Author",
                content = "Second Content"
            ),
            NetworkMovieReview(
                author = "Third Author",
                content = "Third Content"
            )
        )
    }

    override suspend fun getMovieSimilar(id: Int, page: Int): List<NetworkMovie> {
        return getPopularMovies(page = page)
    }
}