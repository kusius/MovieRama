package com.kusius.movies.core.designsystem.theme.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.Visibility
import com.kusius.movies.core.designsystem.R
import kotlin.math.abs

@OptIn(ExperimentalMotionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MovieRamaCollapsibleTopBar(
    titleText: String,
    scrollBehavior: TopAppBarScrollBehavior,
    canNavigateBack: Boolean = true,
    onNavigationClick: () -> Unit = {},
    backDropContent: @Composable (modifier: Modifier) -> Unit
) {
    val expandedToolBarHeightDp = 250.dp
    // Sets the app bar's height offset to collapse the entire bar's height when content is
    // scrolled.
    val heightOffsetLimit =
        with(LocalDensity.current) { -64.dp.toPx() }
    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior.state.heightOffsetLimit = heightOffsetLimit
        }
    }

    // Set up support for resizing the top app bar when vertically dragging the bar itself.
    val appBarDragModifier = if (!scrollBehavior.isPinned) {
        Modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                scrollBehavior.state.heightOffset += delta
            },
            onDragStopped = { velocity ->
                settleAppBar(
                    scrollBehavior.state,
                    velocity,
                    scrollBehavior.flingAnimationSpec,
                    scrollBehavior.snapAnimationSpec
                )
            }
        )
    } else {
        Modifier
    }
    val progress = scrollBehavior.state.collapsedFraction
    val totalToolbarHeight = (expandedToolBarHeightDp + scrollBehavior.state.heightOffset.dp)
    Surface(modifier = Modifier.then(appBarDragModifier).height(totalToolbarHeight)) {
        MotionLayout(
            start = expandedConstraintSet(),
            // todo: get this value more consistently, however 64dp is the size defined for the
            //  material top appbar as of the time of writing
            end = collapsedConstraintSet(64.dp),
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.layoutId("poster")) {
                backDropContent(Modifier)
            }
            Box(modifier = Modifier.layoutId("title")) {
                Text(
                    text = titleText,
                    modifier = Modifier
                        .wrapContentHeight(),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun expandedConstraintSet() = ConstraintSet {
    val poster = createRefFor("poster")
    val title = createRefFor("title")

    // todo: refactor to use centerVerticallyTo because text hides
    //  alternatively (or at the same time) put the two in the same box inside
    //  MotionLayout so that when animating they can overlap
    constrain(poster) {
        width = Dimension.fillToConstraints
        height = Dimension.value(200.dp)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
    }

    constrain(title) {
        // todo: get this from resources (this is not a composable function though ...)
        start.linkTo(parent.start, 16.dp)
        top.linkTo(poster.bottom, 16.dp)
    }
}

private fun collapsedConstraintSet(toolbarHeight: Dp) = ConstraintSet {
    val poster = createRefFor("poster")
    val title = createRefFor("title")

    constrain(poster) {
//        width = Dimension.fillToConstraints
//        start.linkTo(parent.start)
//        end.linkTo(parent.end)
//        top.linkTo(parent.top)
        visibility = Visibility.Gone
    }

    constrain(title) {
        start.linkTo(parent.start)
        top.linkTo(parent.top, 8.dp)
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom, 8.dp)
        scaleX = 0.7f
        scaleY = 0.7f
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewCollapsibleTopBar() {
    val animateToEnd = true
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(1000),
        label = "collapsing toolbar progress"
    )
    MaterialTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MovieRamaCollapsibleTopBar(
                    scrollBehavior = scrollBehavior,
                    titleText = "Mandalorian"
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder),
                        contentDescription = "poster",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary),
                        contentScale = ContentScale.FillWidth,
                        alpha = 1f - progress // Update alpha based on progress. Expanded -> 1f / Collapsed -> 0f (transparent)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                Text(text = "Hello")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    // Check if the app bar is completely collapsed/expanded. If so, no need to settle the app bar,
    // and just return Zero Velocity.
    // Note that we don't check for 0f due to float precision with the collapsedFraction
    // calculation.
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    // Snap if animation specs were provided.
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 &&
            state.heightOffset > state.heightOffsetLimit
        ) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec
            ) { state.heightOffset = value }
        }
    }

    return Velocity(0f, remainingVelocity)
}