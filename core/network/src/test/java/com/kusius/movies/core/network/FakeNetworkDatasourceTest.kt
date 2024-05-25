package com.kusius.movies.core.network

import com.kusius.movies.core.network.di.networkModule
import com.kusius.movies.core.network.fake.FakeMovieNetworkDataSource
import com.kusius.movies.core.network.model.NetworkMovie
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class FakeNetworkDatasourceTest : KoinTest {
    private val subject: FakeMovieNetworkDataSource by inject()

    @Before
    fun setup() {
        startKoin {
            modules(
                networkModule
            )
        }
    }

    @Test
    fun fakeNetworkProvidesDifferentPages() = runTest {
        var previousList = emptyList<NetworkMovie>()
        repeat(5) { index ->
            val nextList = subject.getPopularMovies(index)
            // assert
            assert(!previousList.containsAll(nextList))
            previousList = nextList
        }
    }
}