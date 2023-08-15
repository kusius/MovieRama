package com.workable.movierama.core.network

import com.workable.movierama.core.network.di.networkModule
import com.workable.movierama.core.network.fake.FakeMovieNetworkDataSource
import com.workable.movierama.core.network.model.NetworkMovie
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertContentEquals

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