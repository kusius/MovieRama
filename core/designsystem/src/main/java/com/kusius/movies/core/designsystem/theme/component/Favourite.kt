package com.kusius.movies.core.designsystem.theme.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.kusius.movies.core.designsystem.R


@Composable
fun MovieRamaFavouriteButton(isFavourite: Boolean, onFavouriteChanged: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    var favourite by remember { mutableStateOf(isFavourite) }
    IconToggleButton(
        checked = favourite,
        onCheckedChange = {
            favourite = !favourite
            onFavouriteChanged(favourite)
        },
        modifier = modifier) {
        val tint by animateColorAsState(
            if (favourite) colorResource(id = R.color.favourite) else colorResource(
                id = R.color.not_favourite
            )
        )
        Icon(
            imageVector = if (favourite) {
                Icons.Filled.Favorite
            } else {
                Icons.Default.Favorite
            },
            tint = tint,
            contentDescription = null
        )
    }
}