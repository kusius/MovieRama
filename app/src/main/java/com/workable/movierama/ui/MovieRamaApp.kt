package com.workable.movierama.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.workable.movierama.R
import com.workable.movierama.core.designsystem.theme.MovieRamaTheme
import com.workable.movierama.core.designsystem.theme.component.MovieRamaNavigationBar
import com.workable.movierama.navigation.MovieRamaNavHost
import com.workable.movierama.navigation.TopLevelDestination


@Composable
fun MovieRamaApp(appState: MovieRamaAppState = rememberMovieRamaAppState()) {
    Scaffold(
        bottomBar = {
            MovieRamaBottomBar(
                destinations = appState.topLevelDestinations,
                onNavigateToDestination = appState::navigateToTopLevelDestination,
                currentDestination = appState.currentDestination,
            )
        }
    ) { paddingValues ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(Modifier.fillMaxSize()) {
                val destination= appState.currentTopLevelDestination
                // todo: set top app bar title etc

                MovieRamaNavHost(appState = appState)
            }
        }
    }
}

@Composable
fun MovieRamaBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    MovieRamaNavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestination(destination)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(painter = painterResource(id = R.drawable.ic_launcher_background), contentDescription = null)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovieRamaTheme {
        MovieRamaApp()
    }
}

private fun NavDestination?.isTopLevelDestination(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false