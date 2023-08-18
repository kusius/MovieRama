package com.workable.core.data

import androidx.paging.PagingSource
import com.workable.core.data.di.repositoryModule
import com.workable.core.data.paging.SearchPagingKey
import com.workable.core.data.paging.SimilarMoviesPagingKey
import com.workable.core.data.repository.OnlineFirstMoviesRepository
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.core.network.di.networkModule
import com.workable.movierama.core.network.fake.FakeMovieNetworkDataSource
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OnlineFirstMoviesRepositoryTest : KoinTest {
    private lateinit var subject : OnlineFirstMoviesRepository
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        startKoin {
            modules(
                networkModule,
                repositoryModule
            )
        }

        subject = OnlineFirstMoviesRepository(get<FakeMovieNetworkDataSource>(), testDispatcher)
    }

    @After
    fun cleanup() {
        stopKoin()
    }

    @Test
    fun repository_provides_paged_popular_movies() = runTest(testDispatcher) {
        val pagingSource = subject.getPopularMoviesPagingSource()
        val loadResult = pagingSource.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 5, true))
        assertIs<PagingSource.LoadResult.Page<Int, MovieSummary>>(loadResult)

        var previousList = loadResult.data
        repeat(10) { index ->
            val nextResult = pagingSource.load(params = PagingSource.LoadParams.Append(key = index, loadSize = 5, true))
            assertIs<PagingSource.LoadResult.Page<Int, MovieSummary>>(nextResult)
            val nextList = nextResult.data
            assert(!previousList.containsAll(nextList))
            println(nextList)
            previousList = nextList
        }
    }

    @Test
    fun repository_provides_paged_search_movies() = runTest(testDispatcher) {
        val key = SearchPagingKey(query = "Leon", page = 1)
        val pagingSource = subject.getSearchResultsPagingSource(key.query)
        val loadResult = pagingSource.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 5, true))
        assertIs<PagingSource.LoadResult.Page<Int, MovieSummary>>(loadResult)

        var previousList = loadResult.data
        repeat(10) { index ->
            val nextResult = pagingSource.load(
                params = PagingSource.LoadParams.Append(
                    key = key.copy(page = index),
                    loadSize = 5,
                    true
                )
            )
            assertIs<PagingSource.LoadResult.Page<SearchPagingKey, MovieSummary>>(nextResult)
            val nextList = nextResult.data
            assert(!previousList.containsAll(nextList))
            println(nextList)
            previousList = nextList
        }
    }

    @Test
    fun repository_provides_paged_similar_movies() = runTest(testDispatcher) {
        val movieId = 500
        val pagingSource = subject.getSimilarMoviesPagingSource(movieId)
        val loadResult = pagingSource.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 5, true))
        assertIs<PagingSource.LoadResult.Page<Int, MovieSummary>>(loadResult)

        var previousList = loadResult.data
        repeat(10) { index ->
            val nextResult = pagingSource.load(
                params = PagingSource.LoadParams.Append(
                    key = SimilarMoviesPagingKey(
                        movieId = movieId,
                        page = index
                    ), loadSize = 5, true
                )
            )
            assertIs<PagingSource.LoadResult.Page<SimilarMoviesPagingKey, MovieSummary>>(nextResult)
            val nextList = nextResult.data
            assert(!previousList.containsAll(nextList))
            println(nextList)
            previousList = nextList
        }
    }

    @Test
    fun repository_combines_movie_details() = runTest(testDispatcher) {
        val movieId = 42
        val movieDetails = subject.getMovieDetails(movieId = movieId)
        assertEquals(movieId, movieDetails.summary.id)
    }
}