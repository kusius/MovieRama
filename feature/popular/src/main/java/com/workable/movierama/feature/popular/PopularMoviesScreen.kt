package com.workable.movierama.feature.popular

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.workable.movierama.core.designsystem.theme.MovieRamaTheme
import com.workable.movierama.core.model.Movie
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PopularMoviesRoute(
    onMovieClick: (Int) -> Unit,
    viewModel: PopularMoviesViewmodel = koinViewModel()
) {
    val lazyPagingItems = viewModel.uiState.collectAsLazyPagingItems()
    MoviesScreen(
        onMovieClick = onMovieClick,
        lazyPagingItems = lazyPagingItems
    )
}

@Composable
fun MoviesScreen(onMovieClick: (Int) -> Unit, lazyPagingItems: LazyPagingItems<Movie>) {
    LazyColumn {
        if(lazyPagingItems.loadState.refresh == LoadState.Loading) {
            item {
                Text(
                    text = stringResource(R.string.waiting_for_movies),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        items(count = lazyPagingItems.itemCount) { index ->
            val item = lazyPagingItems[index]
            if (item != null)
                MovieItem(movie = item)
        }

        if (lazyPagingItems.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, modifier: Modifier = Modifier) {
    Card(modifier = modifier){
        Column(modifier = modifier){
            AsyncImage(
                model = movie.posterUrl,
                placeholder = painterResource(id = com.workable.movierama.core.designsystem.R.drawable.placeholder),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Row(modifier = modifier
                .fillMaxWidth()
            ) {
                MovieInformation(movie = movie, onFavouriteChange = {}, modifier = modifier)
            }
        }
    }
}

@Composable
fun MovieInformation(movie: Movie, onFavouriteChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(com.workable.movierama.core.designsystem.R.dimen.padding_small))
    ) {
        Column {
            Text(text = movie.title)
            Row {
                Text(text = movie.rating.toString())
                Text(text = movie.releaseDate, modifier = Modifier.padding(start = dimensionResource(
                    id = com.workable.movierama.core.designsystem.R.dimen.padding_small
                )))
            }
        }
        Spacer(modifier = modifier.weight(1f))
        FavouriteImageButton(isFavourite = movie.isFavourite, onCheckedChange = onFavouriteChange)
    }
}

@Composable
fun FavouriteImageButton(isFavourite: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    IconToggleButton(checked = isFavourite, onCheckedChange = onCheckedChange, modifier = modifier) {
        Icon(
            imageVector = if (isFavourite) {
                Icons.Filled.Favorite
            } else {
                Icons.Default.Favorite
            },
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PopularMoviesScreenPreview() {
    MovieRamaTheme {
//        MoviesScreen()
        MovieItem(
            movie = testMovie,
            modifier = Modifier
        )
    }
}