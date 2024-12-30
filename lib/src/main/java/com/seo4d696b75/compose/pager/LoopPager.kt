package com.seo4d696b75.compose.pager

import androidx.collection.mutableIntSetOf
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * A pager that can be scrolled in horizontal direction infinitely.
 *
 * All the pages in this pager have the same size,
 * defined by max horizontal constraints and [aspectRatio].
 *
 * @param state The state to control this pager
 * @param aspectRatio a ratio `width / height` of each page size.
 * @param contentPadding a padding around the whole content.
 *   Only horizontal sides of this padding are applied.
 * @param pageSpacing The space to separate the pages in this pager.
 * @param flingBehavior Used to control snap or fling animation after user scrolling.
 * @param userScrollEnabled whether the scrolling via the user gestures is allowed.
 *   You can still scroll programmatically via [LoopPagerState] even when it is disabled.
 * @param content composable of each page.
 */
@Composable
fun HorizontalLoopPager(
    state: LoopPagerState,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    pageSpacing: Dp = 0.dp,
    flingBehavior: TargetedFlingBehavior = LoopPagerDefaults.flingBehavior(state),
    userScrollEnabled: Boolean = true,
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
        userScrollEnabled = userScrollEnabled,
        content = content,
    )
}

/**
 * A pager that can be scrolled in vertical direction infinitely.
 *
 * All the pages in this pager have the same size,
 * defined by max vertical constraints and [aspectRatio].
 *
 * @param state The state to control this pager
 * @param aspectRatio a ratio `width / height` of each page size.
 * @param contentPadding a padding around the whole content.
 *   Only vertical sides of this padding are applied.
 * @param pageSpacing The space to separate the pages in this pager.
 * @param flingBehavior Used to control snap or fling animation after user scrolling.
 * @param userScrollEnabled whether the scrolling via the user gestures is allowed.
 *   You can still scroll programmatically via [LoopPagerState] even when it is disabled.
 * @param content composable of each page.
 */
@Composable
fun VerticalLoopPager(
    state: LoopPagerState,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    pageSpacing: Dp = 0.dp,
    flingBehavior: TargetedFlingBehavior = LoopPagerDefaults.flingBehavior(state),
    userScrollEnabled: Boolean = true,
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
        userScrollEnabled = userScrollEnabled,
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
    userScrollEnabled: Boolean = true,
    content: @Composable (page: Int) -> Unit,
) {
    val itemProvider = rememberPagerItemProvider(state.pageCount, content)
    val itemProviderLambda: () -> LazyLayoutItemProvider = remember(itemProvider) {
        { itemProvider }
    }

    LazyLayout(
        itemProvider = itemProviderLambda,
        prefetchState = null,
        measurePolicy = { constraints ->
            with(orientation.asScope()) {
                // max width from constraints and contentPadding
                val viewportSize = constraints.maxSizeInMainAxis
                val pageSpacingPx = pageSpacing.roundToPx()
                require(pageSpacingPx >= 0) {
                    "pageSpacing must be >= 0"
                }

                val beforePadding =
                    contentPadding.calculateStartPaddingInMainAxis(layoutDirection).roundToPx()
                val afterPadding =
                    contentPadding.calculateEndPaddingInMainAxis(layoutDirection).roundToPx()
                val pageSize = viewportSize - beforePadding - afterPadding
                require(pageSize > 0 && beforePadding < pageSize / 2 && afterPadding < pageSize / 2) {
                    "contentPadding or pageSpacing too large against constraints!"
                }

                // calculate cross axis size from aspectRation
                val sizeInCrossAxis = pageSize.toCrossAxis(aspectRatio).roundToInt().coerceIn(
                    minimumValue = constraints.minSizeInCrossAxis,
                    maximumValue = constraints.maxSizeInCrossAxis,
                )

                val layoutInfo = LoopPagerLayoutInfo.Measured(
                    viewportSize = viewportSize,
                    viewportSizeInCrossAxis = sizeInCrossAxis,
                    pageSize = pageSize,
                    pageSpacing = pageSpacingPx,
                    beforeContentPadding = beforePadding,
                    afterContentPadding = afterPadding,
                )

                // page indices to be drawn (may be negative!)
                val indices = state.onLayout(layoutInfo).toList()
                itemProvider.updateVisiblePageCount(indices.size)

                // constraints for drawing each page
                val pageConstraints = orientedConstraints(
                    minSizeInMainAxis = pageSize,
                    maxSizeInMainAxis = pageSize,
                    minSizeInCrossAxis = sizeInCrossAxis,
                    maxSizeInCrossAxis = sizeInCrossAxis,
                )

                // measure
                val measuredIndices = mutableIntSetOf()
                val placeableMap = indices.associateWith {
                    // index must be normalized in [0, size)
                    var index = it.mod(itemProvider.pageCount)
                    // a page with the same index may already be measured
                    while (true) {
                        if (measuredIndices.add(index)) {
                            break
                        }
                        index += itemProvider.pageCount
                    }
                    measure(index, pageConstraints)
                }

                layout(
                    width = horizontalOf(viewportSize, sizeInCrossAxis),
                    height = verticalOf(viewportSize, sizeInCrossAxis),
                ) {
                    placeableMap.forEach { (index, placeables) ->
                        // calculate position to be drawn at
                        val offsetInMainAxis = state.offset(index)
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
            .scrollable(
                state = state,
                orientation = orientation,
                enabled = userScrollEnabled,
                flingBehavior = flingBehavior,
                interactionSource = state.interactionSource,
            ),
    )
}
