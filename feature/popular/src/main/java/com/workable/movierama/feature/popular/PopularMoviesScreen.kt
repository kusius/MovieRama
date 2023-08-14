package com.workable.movierama.feature.popular

import android.util.Log
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.workable.movierama.core.designsystem.theme.MovieRamaTheme
import com.workable.movierama.core.model.Movie
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
        lazyPagingItems = lazyPagingItems
    )
}

@Composable
fun MoviesScreen(
    onMovieClick: (Int) -> Unit,
    onFavouriteChanged: (Int,Boolean) -> Unit,
    lazyPagingItems: LazyPagingItems<Movie>
) {
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
                MovieItem(
                    movie = item,
                    onFavouriteChanged = { isFavourite ->
                        onFavouriteChanged(item.id, isFavourite)
                    },
                    modifier = Modifier.padding(dimensionResource(designR.dimen.padding_small))
                )
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
fun MovieItem(movie: Movie, onFavouriteChanged: (Boolean) -> kotlin.Unit, modifier: Modifier = Modifier) {
    Log.d("PopularMovieScreen", "MovieItem: loading movie $movie")
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
    ){
        Column(modifier = Modifier){
            AsyncImage(
                model = movie.posterUrl,
                placeholder = painterResource(id = com.workable.movierama.core.designsystem.R.drawable.placeholder),
                error = painterResource(id = com.workable.movierama.core.designsystem.R.drawable.placeholder),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Row(modifier = modifier
                .fillMaxWidth()
            ) {
                MovieInformation(
                    movie = movie,
                    onFavouriteChange = onFavouriteChanged,
                    modifier = modifier)
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
                Text(text = movie.ratingOutOf10.toString())
                Text(text = movie.releaseDate, modifier = Modifier.padding(start = dimensionResource(
                    id = com.workable.movierama.core.designsystem.R.dimen.padding_small
                )))
            }
        }
        Spacer(modifier = modifier.weight(1f))
        FavouriteImageButton(isFavourite = movie.isFavourite, onFavouriteChanged = onFavouriteChange)
    }
}

@Composable
fun FavouriteImageButton(isFavourite: Boolean, onFavouriteChanged: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    var favourite by remember { mutableStateOf(isFavourite) }
    IconToggleButton(
        checked = favourite,
        onCheckedChange = {
            favourite = !favourite
            onFavouriteChanged(favourite)
        },
        modifier = modifier) {
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
        MovieItem(
            movie = testMovie,
            onFavouriteChanged = {},
            modifier = Modifier
        )
    }
}