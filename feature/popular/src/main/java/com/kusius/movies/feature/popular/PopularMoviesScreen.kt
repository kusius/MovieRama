package com.kusius.movies.feature.popular

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.kusius.movies.core.designsystem.theme.MovieRamaTheme
import com.kusius.movies.core.designsystem.theme.component.MovieRamaFavouriteButton
import com.kusius.movies.core.designsystem.theme.component.MovieRamaRating
import com.kusius.movies.core.designsystem.theme.component.MovieRamaSearchBar
import com.kusius.movies.core.designsystem.theme.component.pullrefresh.PullRefreshIndicator
import com.kusius.movies.core.designsystem.theme.component.pullrefresh.pullRefresh
import com.kusius.movies.core.designsystem.theme.component.pullrefresh.rememberPullRefreshState
import com.kusius.movies.core.model.MovieSummary
import com.kusius.movies.feature.popular.preview.MoviesPreviewParameterProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.kusius.movies.core.designsystem.R as designR

@Composable
internal fun PopularMoviesRoute(
    onMovieClick: (Int) -> Unit,
    viewModel: PopularMoviesViewmodel = koinViewModel()
) {
    val lazyPagingItems = viewModel.uiState.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()
    MoviesScreen(
        onMovieClick = onMovieClick,
        onFavouriteChanged = viewModel::markFavourite,
        onSearchQueryChanged = viewModel::searchMovies,
        lazyPagingItems = lazyPagingItems,
        searchQuery = searchQuery,
    )
}

@Composable
fun MoviesScreen(
    onMovieClick: (Int) -> Unit,
    onFavouriteChanged: (Int, Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    lazyPagingItems: LazyPagingItems<MovieSummary>,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(lazyPagingItems.loadState.refresh == LoadState.Loading) }
    fun refresh() = refreshScope.launch {
        lazyPagingItems.refresh()
    }
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = ::refresh)

    fun search(query: String) {
        onSearchQueryChanged(query)
    }

    Column(modifier = modifier.padding(dimensionResource(designR.dimen.padding_small))) {
        MovieRamaSearchBar(
            searchQuery = searchQuery,
            onQueryChanged = ::search,
            onSearch = ::search,
            placeholder = { Text(text = stringResource(R.string.search_hint)) },
        )
        Box(
            modifier = modifier
                .pullRefresh(state = pullRefreshState)
                .padding(top = dimensionResource(id = designR.dimen.padding_small))
        ) {
            LazyColumn {
                items(count = lazyPagingItems.itemCount) { index ->
                    refreshing = false
                    val item = lazyPagingItems[index]
                    if (item != null)
                        MovieItem(
                            movieSummary = item,
                            onFavouriteChanged = { isFavourite ->
                                onFavouriteChanged(item.id, isFavourite)
                            },
                            modifier = modifier
                                .padding(dimensionResource(designR.dimen.padding_small))
                                .clickable(onClick = { onMovieClick(item.id) })
                        )
                }

                if (lazyPagingItems.loadState.append == LoadState.Loading) {
                    item {
                        CircularProgressIndicator(
                            modifier = modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            PullRefreshIndicator(
                modifier = modifier
                    .testTag("PullRefreshIndicator")
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                refreshing = refreshing,
                state = pullRefreshState,
            )
        }

    }

}

@Composable
fun MovieInformation(movieSummary: MovieSummary, onFavouriteChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val smallPadding = dimensionResource(designR.dimen.padding_small)
    Row(modifier = Modifier
        .padding(
            start = smallPadding,
            top = smallPadding
        ),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movieSummary.title,
                style = MaterialTheme.typography.displayMedium,

            )
            Row() {
                MovieRamaRating(rating = movieSummary.ratingOutOf10 / 2, modifier = Modifier.align(
                    Alignment.CenterVertically))
                Text(
                    text = movieSummary.releaseDate,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(start = dimensionResource(id = com.kusius.movies.core.designsystem.R.dimen.padding_small))
                        .align(Alignment.CenterVertically)
                )
            }
        }
        MovieRamaFavouriteButton(
            isFavourite = movieSummary.isFavourite,
            onFavouriteChanged = onFavouriteChange,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.favourite_button_size))
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun MovieItem(movieSummary: MovieSummary, onFavouriteChanged: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Card(
        elevation = CardDefaults.cardElevation(24.dp),
        modifier = modifier.fillMaxWidth()
    ){
        Column(modifier = Modifier){
            AsyncImage(
                model = movieSummary.posterUrl,
                placeholder = painterResource(id = designR.drawable.placeholder),
                error = painterResource(id = designR.drawable.placeholder),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier.height(200.dp)
            )
            Row {
                MovieInformation(
                    movieSummary = movieSummary,
                    onFavouriteChange = onFavouriteChanged,
                    modifier = modifier)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PopularMoviesScreenPreview(
    @PreviewParameter(MoviesPreviewParameterProvider::class) movies: MutableStateFlow<PagingData<MovieSummary>>
) {
    MovieRamaTheme {
        MoviesScreen(
            onMovieClick = {},
            onFavouriteChanged = { _, _ -> },
            onSearchQueryChanged = {},
            lazyPagingItems = movies.collectAsLazyPagingItems(),
            searchQuery = ""
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MovieItemPreview() {
    MovieRamaTheme {
        MovieItem(
            movieSummary = testMovieSummary,
            onFavouriteChanged = {},
            modifier = Modifier
        )
    }
}