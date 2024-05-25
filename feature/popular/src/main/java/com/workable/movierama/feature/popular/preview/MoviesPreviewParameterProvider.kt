package com.workable.movierama.feature.popular.preview

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.feature.popular.testMovieSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class MoviesPreviewParameterProvider : PreviewParameterProvider<MutableStateFlow<PagingData<MovieSummary>>> {
    override val values: Sequence<MutableStateFlow<PagingData<MovieSummary>>>
        get() {
            val fakeData = listOf(
                testMovieSummary,
                testMovieSummary,
                testMovieSummary
            )
            val pagingData = PagingData.from(fakeData)
            val flow = MutableStateFlow(pagingData)

            return sequenceOf(
                flow
            )
        }
}