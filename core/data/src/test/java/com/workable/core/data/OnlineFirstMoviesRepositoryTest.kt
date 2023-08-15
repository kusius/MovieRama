package com.workable.core.data

import androidx.paging.PagingSource
import com.workable.core.data.di.repositoryModule
import com.workable.core.data.repository.MoviesRepository
import com.workable.core.data.repository.OnlineFirstMoviesRepository
import com.workable.movierama.core.model.Movie
import com.workable.movierama.core.network.di.networkModule
import com.workable.movierama.core.network.fake.FakeMovieNetworkDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import kotlin.test.assertIs

class OnlineFirstMoviesRepositoryTest : KoinTest {
    private lateinit var subject : OnlineFirstMoviesRepository

    @Before
    fun setup() {
        startKoin {
            modules(
                networkModule,
                repositoryModule
            )
        }

        subject = OnlineFirstMoviesRepository(get<FakeMovieNetworkDataSource>())
    }

    @Test
    fun repositoryProvidesPagedData() = runTest {
        val pagingSource = subject.getPopularMoviesPagingSource()
        val loadResult = pagingSource.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 5, true))
        assertIs<PagingSource.LoadResult.Page<Int, Movie>>(loadResult)

        var previousList = loadResult.data
        repeat(10) { index ->
            val nextResult = pagingSource.load(params = PagingSource.LoadParams.Append(key = index, loadSize = 5, true))
            assertIs<PagingSource.LoadResult.Page<Int, Movie>>(nextResult)
            val nextList = nextResult.data
            assert(!previousList.containsAll(nextList))
            println(nextList)
            previousList = nextList
        }
    }
}