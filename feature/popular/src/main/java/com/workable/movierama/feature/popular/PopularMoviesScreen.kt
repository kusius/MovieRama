package com.workable.movierama.feature.popular

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.workable.movierama.core.designsystem.theme.MovieRamaTheme
import com.workable.movierama.core.designsystem.theme.component.MovieRamaFavouriteButton
import com.workable.movierama.core.designsystem.theme.component.MovieRamaRating
import com.workable.movierama.core.designsystem.theme.component.MovieRamaSearchBar
import com.workable.movierama.core.designsystem.theme.component.pullrefresh.PullRefreshIndicator
import com.workable.movierama.core.designsystem.theme.component.pullrefresh.pullRefresh
import com.workable.movierama.core.designsystem.theme.component.pullrefresh.rememberPullRefreshState
import com.workable.movierama.core.model.MovieSummary
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.workable.movierama.core.designsystem.R as designR

@Composable
internal fun PopularMoviesRoute(
    onMovieClick: (Int) -> Unit,
    viewModel: PopularMoviesViewmodel = koinViewModel()
) {
    val lazyPagingItems = viewModel.uiState.collectAsLazyPagingItems()
    MoviesScreen(
        onMovieClick = onMovieClick,
        onFavouriteChanged = viewModel::markFavourite,
        onSearchQueryChanged = viewModel::searchMovies,
        lazyPagingItems = lazyPagingItems,
        viewModel = viewModel
    )
}

@Composable
fun MoviesScreen(
    onMovieClick: (Int) -> Unit,
    onFavouriteChanged: (Int, Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    lazyPagingItems: LazyPagingItems<MovieSummary>,
    viewModel: PopularMoviesViewmodel,
    modifier: Modifier = Modifier
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(lazyPagingItems.loadState.refresh == LoadState.Loading) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    fun refresh() = refreshScope.launch {
        lazyPagingItems.refresh()
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)
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
                .pullRefresh(state = state)
                .padding(top = dimensionResource(id = designR.dimen.padding_small))
        ) {
            LazyColumn() {
                if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
                    refreshing = true
                }

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
                                .clickable(onClick = {onMovieClick(item.id)})
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
                refreshing = true,
                state = state,
            )
        }

    }

}

@Composable
fun MovieInformation(movieSummary: MovieSummary, onFavouriteChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = movieSummary.title,
                style = MaterialTheme.typography.displayMedium
            )
            Row() {
                MovieRamaRating(rating = movieSummary.ratingOutOf10 / 2, modifier = Modifier.align(
                    Alignment.CenterVertically))
                Text(
                    text = movieSummary.releaseDate,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(start = dimensionResource(id = com.workable.movierama.core.designsystem.R.dimen.padding_small))
                        .align(Alignment.CenterVertically)
                )
            }
        }
        Spacer(modifier = modifier.weight(1f))
        MovieRamaFavouriteButton(
            isFavourite = movieSummary.isFavourite,
            onFavouriteChanged = onFavouriteChange,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun MovieItem(movieSummary: MovieSummary, onFavouriteChanged: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
    ){
        Column(modifier = Modifier){
            AsyncImage(
                model = movieSummary.posterUrl,
                placeholder = painterResource(id = designR.drawable.placeholder),
                error = painterResource(id = designR.drawable.placeholder),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Row(modifier = modifier
                .fillMaxWidth()
            ) {
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
fun PopularMoviesScreenPreview() {
    MovieRamaTheme {
        MovieItem(
            movieSummary = testMovieSummary,
            onFavouriteChanged = {},
            modifier = Modifier
        )
    }
}