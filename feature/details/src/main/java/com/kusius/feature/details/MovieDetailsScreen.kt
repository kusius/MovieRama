package com.kusius.feature.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.kusius.feature.details.ui.MovieDetailsPreviewParameterProvider
import com.kusius.feature.details.ui.MovieDetailsScreenParams
import com.kusius.movies.core.designsystem.theme.component.MovieRamaCollapsibleTopBar
import com.kusius.movies.core.designsystem.theme.component.MovieRamaFavouriteButton
import com.kusius.movies.core.designsystem.theme.component.MovieRamaRating
import com.kusius.movies.core.designsystem.theme.component.MovieRamaSection
import com.kusius.movies.core.model.MovieDetails
import com.kusius.movies.core.model.MovieSummary
import com.kusius.movies.feature.details.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.kusius.movies.core.designsystem.R as designR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieDetailsRoute(
    movieId: Int,
    viewModel: MovieDetailsViewmodel = koinViewModel(),
    navController: NavController = rememberNavController()
) {
    // todo: delete this, i believe its not necessary (was it ever ?)
    viewModel.getMovieDetails(movieId)
    val topAppbarState = rememberTopAppBarState()
    val movieDetailsUiState by viewModel.movieDetails.collectAsState()
    val similarMovies = viewModel.similarMovies.collectAsLazyPagingItems()
    val canNavigateBack = navController.previousBackStackEntry != null
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppbarState)
    val snackBarHostState = remember { SnackbarHostState() }
    val state = movieDetailsUiState

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if(state is MovieDetailsUiState.Content) {
                MovieRamaCollapsibleTopBar(
                    titleText = state.movieDetails.summary.title,
                    scrollBehavior = scrollBehavior,
                    canNavigateBack = canNavigateBack,
                    onNavigationClick = { navController.navigateUp() }
                ) {
                    AsyncImage(
                        model = state.movieDetails.summary.posterUrl,
                        placeholder = painterResource(id = designR.drawable.placeholder),
                        error = painterResource(id = designR.drawable.placeholder),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            when(state) {
                is MovieDetailsUiState.Content -> {
                    MovieDetailsScreen(
                        movieDetails = state.movieDetails,
                        similarMovies = similarMovies,
                        onFavouriteChange = { isFavourite ->
                            viewModel.markFavourite(movieId, isFavourite)
                        }
                    )
                }

                is MovieDetailsUiState.Error -> {
                    val message = stringResource(id = state.message)
                    LaunchedEffect(null) {
                        launch {
                            snackBarHostState.showSnackbar(message)
                        }
                    }
                }
                MovieDetailsUiState.Loading -> {

                }
            }
        }
    }
}

@Composable
fun MovieDetailsScreen(
    movieDetails: MovieDetails,
    similarMovies: LazyPagingItems<MovieSummary>,
    onFavouriteChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        val paddingSmalll = dimensionResource(id = designR.dimen.padding_small)
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(
                modifier = modifier
                    .padding(paddingSmalll)
            ) {
                Text(
                    text = movieDetails.genres.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Row(modifier = modifier.padding(paddingSmalll)) {
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    text = movieDetails.summary.releaseDate,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = paddingSmalll)
                )
                MovieRamaRating(rating = movieDetails.summary.ratingOutOf10 / 2)
            }
            Spacer(modifier = modifier.weight(1f))
            MovieRamaFavouriteButton(
                isFavourite = movieDetails.summary.isFavourite,
                onFavouriteChanged = onFavouriteChange,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Column(modifier = modifier.padding(paddingSmalll), verticalArrangement = Arrangement.SpaceEvenly) {
            MovieRamaSection(
                heading = stringResource(id = R.string.description),
                modifier = modifier.padding(bottom = dimensionResource(id = designR.dimen.padding_medium))
            ) {
                Text(text = movieDetails.overview)
            }

            MovieRamaSection(
                heading = stringResource(id = R.string.director),
                modifier = modifier.padding(bottom = dimensionResource(id = designR.dimen.padding_medium))
            ) {
                Text(text = movieDetails.directorName)
            }

            MovieRamaSection(
                heading = stringResource(id = R.string.cast),
                modifier = modifier.padding(bottom = dimensionResource(id = designR.dimen.padding_medium))
            ) {
                Text(text = movieDetails.castNames.joinToString(", "))
            }

            MovieRamaSection(
                heading = stringResource(id = R.string.similar_movies),
                modifier = modifier.padding(bottom = dimensionResource(id = designR.dimen.padding_medium))
            ) {
                MovieDetailsCarousel(similarMovies = similarMovies)
            }
            
            MovieRamaSection(
                heading = stringResource(id = R.string.reviews),
                modifier = modifier.padding(bottom = dimensionResource(id = designR.dimen.padding_medium))
            ) {
                movieDetails.reviews.take(2).forEach { review ->
                    Column(modifier = Modifier.padding(bottom = paddingSmalll)) {
                        Text(text = review.authorName, style = MaterialTheme.typography.labelSmall)
                        Text(text = review.content)
                    }
                }
            }
        }
    }
}

@Composable
fun MovieDetailsCarousel(similarMovies: LazyPagingItems<MovieSummary>, modifier: Modifier = Modifier) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(similarMovies.loadState.refresh == LoadState.Loading) }
    LazyRow() {
        if (similarMovies.loadState.refresh == LoadState.Loading) {
            refreshing = true
        }

        items(count = similarMovies.itemCount) { index ->
            refreshing = false
            val item = similarMovies[index]
            if (item != null)
                Card(
                    elevation = CardDefaults.cardElevation(16.dp),
                    modifier = Modifier
                        .width(100.dp)
                        .height(150.dp)
                        .padding(
                            end = dimensionResource(designR.dimen.padding_small)
                        ),
                ){
                    AsyncImage(
                        model = item.posterUrl,
                        placeholder = painterResource(id = designR.drawable.placeholder),
                        error = painterResource(id = designR.drawable.placeholder),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
        }

        if (similarMovies.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesScreenPreview(
    @PreviewParameter(MovieDetailsPreviewParameterProvider::class)
    movieDetails: MovieDetailsScreenParams,
) {
    MaterialTheme {
        MovieDetailsScreen(
            movieDetails = movieDetails.movieDetails,
            similarMovies = movieDetails.similarMovies.collectAsLazyPagingItems(),
            onFavouriteChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesErrorPreview() {

}
