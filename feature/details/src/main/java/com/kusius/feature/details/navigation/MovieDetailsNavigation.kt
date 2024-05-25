package com.kusius.feature.details.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kusius.feature.details.MovieDetailsRoute

const val movieIdArg = "movieId"
private const val movieDetailsRoute = "movie_details_route"

fun NavController.navigateToPopularMovies(movieIdArg: Int, navOptions: NavOptions? = null) {
    this.navigate("$movieDetailsRoute/$movieIdArg", navOptions)
}

fun NavGraphBuilder.movieDetailsScreen(navController: NavController) {
    composable(
        route = "$movieDetailsRoute/{$movieIdArg}",
        arguments = listOf(
            navArgument(movieIdArg) { type = NavType.IntType }
        ),
    ) { backStackEntry ->
        backStackEntry.arguments?.getInt(movieIdArg)?.let { movieId ->
            MovieDetailsRoute(movieId = movieId, navController = navController)
        }
    }
}