package com.workable.movierama.feature.popular

import androidx.paging.testing.asSnapshot
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.feature.popular.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class PopularMoviesViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    val testData = buildList {
        repeat(50) {
            add(
                MovieSummary(
                id = it,
                title = "Movie$it",
                posterUrl = "url$it",
                isFavourite = false,
                releaseDate = "date$it",
                ratingOutOf10 = 4.4f
            )
            )
        }
    }

    private lateinit var viewModel: PopularMoviesViewmodel

    @Before
    fun setup() {
        viewModel = PopularMoviesViewmodel(
            com.workable.movierama.feature.popular.fake.FakeMoviesRepository(
                testData
            )
        )
    }

    @Test
    fun test_items_contain_all() = runTest {
        val items = viewModel.uiState

        val itemsSnapshot = items.asSnapshot {
            scrollTo(50)
        }

        assertContentEquals(
            expected = testData,
            actual = itemsSnapshot
        )
    }

    @Test
    fun item_marked_favourite_correct_data() = runTest {
        val items = viewModel.uiState

        viewModel.markFavourite(testData[42].id, true)

        val itemsSnapshot = items.asSnapshot {
            scrollTo(50)
        }

        assertEquals(
            expected = true,
            actual = itemsSnapshot[42].isFavourite
        )

    }
}