package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seo4d696b75.android.loop_pager_sample.ui.orientation.asScope
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun HorizontalLoopPager(
    state: LoopPagerState,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    pageSpacing: Dp = 0.dp,
    flingBehavior: TargetedFlingBehavior = LoopPagerDefaults.flingBehavior(state),
    nestedScrollConnection: NestedScrollConnection =
        LoopPagerDefaults.nestedScrollConnection(state, Orientation.Horizontal),
    content: @Composable (page: Int) -> Unit,
) {
    LoopPager(
        orientation = Orientation.Horizontal,
        state = state,
        aspectRatio = aspectRatio,
        modifier = modifier,
        contentPadding = contentPadding,
        pageSpacing = pageSpacing,
        flingBehavior = flingBehavior,
        nestedScrollConnection = nestedScrollConnection,
        content = content,
    )
}

@Composable
fun VerticalLoopPager(
    state: LoopPagerState,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    pageSpacing: Dp = 0.dp,
    flingBehavior: TargetedFlingBehavior = LoopPagerDefaults.flingBehavior(state),
    nestedScrollConnection: NestedScrollConnection =
        LoopPagerDefaults.nestedScrollConnection(state, Orientation.Vertical),
    content: @Composable (page: Int) -> Unit,
) {
    LoopPager(
        orientation = Orientation.Vertical,
        state = state,
        aspectRatio = aspectRatio,
        modifier = modifier,
        contentPadding = contentPadding,
        pageSpacing = pageSpacing,
        flingBehavior = flingBehavior,
        nestedScrollConnection = nestedScrollConnection,
        content = content,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoopPager(
    orientation: Orientation,
    state: LoopPagerState,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    pageSpacing: Dp = 0.dp,
    flingBehavior: TargetedFlingBehavior = LoopPagerDefaults.flingBehavior(state),
    nestedScrollConnection: NestedScrollConnection =
        LoopPagerDefaults.nestedScrollConnection(state, orientation),
    content: @Composable (page: Int) -> Unit,
) {
    val itemProvider = rememberPagerItemProvider(state.pageCount, content)
    val itemProviderLambda: () -> LazyLayoutItemProvider = remember(itemProvider) {
        { itemProvider }
    }

    if (itemProvider.itemCount < 2) {
        // crash when scrolling
        return
    }

    LazyLayout(
        itemProvider = itemProviderLambda,
        prefetchState = null,
        measurePolicy = { constraints ->
            with(orientation.asScope()) {
                // max width from constraints and contentPadding
                val containerSize = constraints.maxSizeInMainAxis
                val pageSpacingPx = pageSpacing.roundToPx()
                require(pageSpacingPx >= 0) {
                    "pageSpacing must be >= 0"
                }

                val startPadding = max(
                    pageSpacingPx,
                    contentPadding.calculateStartPaddingInMainAxis(layoutDirection).roundToPx(),
                )
                val endPadding = max(
                    pageSpacingPx,
                    contentPadding.calculateEndPaddingInMainAxis(layoutDirection).roundToPx(),
                )
                val pageSize = containerSize - startPadding - endPadding
                require(pageSize > 0 && startPadding < pageSize / 2 && endPadding < pageSize / 2) {
                    "contentPadding or pageSpacing too large against constraints!"
                }
                require(itemProvider.itemCount >= 3 || startPadding <= pageSpacingPx || endPadding <= pageSpacingPx) {
                    "more than 3 items required when both start and end contentPadding set"
                }

                // calculate cross axis size from aspectRation
                val sizeInCrossAxis = pageSize.toCrossAxis(aspectRatio).roundToInt().coerceIn(
                    minimumValue = constraints.minSizeInCrossAxis,
                    maximumValue = constraints.maxSizeInCrossAxis,
                )

                // page indices to be drawn (may be negative!)
                val indices = state.onLayout(containerSize, pageSize, startPadding)

                // constraints for drawing each page
                val pageConstraints = orientedConstraints(
                    minSizeInMainAxis = pageSize,
                    maxSizeInMainAxis = pageSize,
                    minSizeInCrossAxis = sizeInCrossAxis,
                    maxSizeInCrossAxis = sizeInCrossAxis,
                )

                val placeableMap = indices.associateWith {
                    // index must be normalized in [0, size)
                    val index = it.mod(itemProvider.itemCount)
                    measure(index, pageConstraints)
                }

                layout(
                    width = horizontalOf(containerSize, sizeInCrossAxis),
                    height = verticalOf(containerSize, sizeInCrossAxis),
                ) {
                    placeableMap.forEach { (index, placeables) ->
                        // calculate position to be drawn at
                        val positionInMainAxis = state.calculatePosition(index)
                        placeables.forEach { placeable ->
                            placeable.placeRelative(
                                x = horizontalOf(positionInMainAxis, 0),
                                y = verticalOf(positionInMainAxis, 0),
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
            .clipToBounds()
            .scrollable(
                state = state,
                orientation = orientation,
                enabled = true,
                flingBehavior = flingBehavior,
                interactionSource = state.interactionSource,
            ),
    )
}
