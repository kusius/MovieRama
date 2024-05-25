package com.kusius.movies.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    POPULAR(
        selectedIcon = Icons.Rounded.ThumbUp,
        unselectedIcon = Icons.Outlined.ThumbUp
    )
}