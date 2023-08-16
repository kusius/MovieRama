package com.workable.movierama.core.network

import com.workable.movierama.core.network.ktor.KtorMovieNetworkDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KtorMovieNetworkDatasourceTest {
    private val dataSource = KtorMovieNetworkDataSource()
    @Test
    fun test_fetch_popular_movies() = runTest {
        val result = dataSource.getPopularMovies(1)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun fetch_search_movie() = runTest {
        val result = dataSource.searchMovies(query = "Leon", page = 1)
        // we expect at least 1 ("Leon the professional")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun fetch_movie_details() = runTest {
        val movieId = 500
        val result = dataSource.getMovieDetails(id = movieId)
        assertNotNull(result)
        assertTrue(result.id == movieId)
    }

    @Test
    fun fetch_movie_reviews() = runTest {
        val movieId = 500
        val result = dataSource.getMovieReviews(id = movieId, page = 1)
        assertNotNull(result)
    }

    @Test
    fun fetch_movie_similar() = runTest {
        val movieId = 500
        val result = dataSource.getMovieSimilar(id = movieId, page = 1)
        assertNotNull(result)
    }
}