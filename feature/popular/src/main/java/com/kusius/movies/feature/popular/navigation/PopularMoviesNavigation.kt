package com.kusius.movies.feature.popular.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.kusius.movies.feature.popular.PopularMoviesRoute

const val POPULAR_MOVIES_RESOURCE_ID = "linkedNewsResourceId"
const val popularMoviesNavigationRoute = "for_you_route/{$POPULAR_MOVIES_RESOURCE_ID}"
private const val DEEP_LINK_URI_PATTERN =
    "https://www.nowinandroid.apps.samples.google.com/foryou/{$POPULAR_MOVIES_RESOURCE_ID}"

fun NavController.navigateToPopularMovies(navOptions: NavOptions? = null) {
    this.navigate(popularMoviesNavigationRoute, navOptions)
}

fun NavGraphBuilder.popularMoviesScreen(onMovieClick: (Int) -> Unit) {
    composable(
        route = popularMoviesNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
        arguments = listOf(
            navArgument(POPULAR_MOVIES_RESOURCE_ID) { type = NavType.StringType },
        ),
    ) {
        PopularMoviesRoute(onMovieClick)
    }
}