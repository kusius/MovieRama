package com.kusius.movies.core.designsystem.theme.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.kusius.movies.core.designsystem.R

@Composable
fun MovieRamaRating(rating: Float, modifier: Modifier = Modifier) {
    RatingBar(
        value = rating,
        style = RatingBarStyle.Stroke(),
        spaceBetween = dimensionResource(id = R.dimen.padding_tiny),
        size = dimensionResource(id = R.dimen.star_rating_size),
        onValueChange = {},
        onRatingChanged = {},
        modifier = modifier
    )
}