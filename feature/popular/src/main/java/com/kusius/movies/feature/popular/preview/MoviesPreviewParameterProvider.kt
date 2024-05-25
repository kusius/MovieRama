package com.kusius.movies.feature.popular.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import com.kusius.movies.core.model.MovieSummary
import com.kusius.movies.feature.popular.testMovieSummary
import kotlinx.coroutines.flow.MutableStateFlow

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