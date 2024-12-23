package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.seo4d696b75.android.loop_pager_sample.ui.orientation.withOrientation
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.roundToInt

@Composable
fun HorizontalLoopPager(
    state: LoopPagerState,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable (page: Int) -> Unit,
) {
    LoopPager(
        orientation = Orientation.Horizontal,
        state = state,
        aspectRatio = aspectRatio,
        modifier = modifier,
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
fun VerticalLoopPager(
    state: LoopPagerState,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable (page: Int) -> Unit,
) {
    LoopPager(
        orientation = Orientation.Vertical,
        state = state,
        aspectRatio = aspectRatio,
        modifier = modifier,
        contentPadding = contentPadding,
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

    LaunchedEffect(state) {
        snapshotFlow { state.settlePage }
            .distinctUntilChanged()
            .collect {
                state.updateAnchorsOnSettle()
            }
    }

    LazyLayout(
        itemProvider = itemProviderLambda,
        prefetchState = null,
        measurePolicy = { constraints ->
            withOrientation(orientation) {
                // max width from constraints and contentPadding
                val containerSize = constraints.maxSizeInMainAxis
                val startPadding =
                    contentPadding.calculateStartPaddingInMainAxis(layoutDirection).roundToPx()
                val endPadding =
                    contentPadding.calculateEndPaddingInMainAxis(layoutDirection).roundToPx()
                val pageSize = containerSize - startPadding - endPadding
                require(pageSize > 0 && startPadding < pageSize / 2 && endPadding < pageSize / 2) {
                    "contentPadding too large against constraints!"
                }
                require(itemProvider.itemCount >= 3 || startPadding <= 0 || endPadding <= 0) {
                    "more than 3 items required when both start and end contentPadding set!"
                }

                state.updateAnchorsOnLayout(pageSize, startPadding)

                // calculate cross axis size from aspectRation
                val sizeInCrossAxis = pageSize.toCrossAxis(aspectRatio).roundToInt().coerceIn(
                    minimumValue = constraints.minSizeInCrossAxis,
                    maximumValue = constraints.maxSizeInCrossAxis,
                )

                // page indices to be drawn (may be negative!)
                val indices = state.getVisiblePages(containerSize, pageSize)

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
                        val offsetInMainAxis = pageSize * index + state.offset
                        placeables.forEach { placeable ->
                            placeable.placeRelative(
                                x = horizontalOf(offsetInMainAxis, 0),
                                y = verticalOf(offsetInMainAxis, 0),
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
            .clipToBounds()
            .anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = orientation,
            ),
    )
}
