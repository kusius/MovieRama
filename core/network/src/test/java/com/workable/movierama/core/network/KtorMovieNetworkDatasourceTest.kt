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
}