package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalLoopPager(
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
            // max width from constraints and contentPadding
            val containerWidth = constraints.maxWidth
            val startPadding = contentPadding.calculateStartPadding(layoutDirection).roundToPx()
            val endPadding = contentPadding.calculateEndPadding(layoutDirection).roundToPx()
            val pageWidth = containerWidth - startPadding - endPadding
            require(pageWidth > 0 && startPadding < pageWidth / 2 && endPadding < pageWidth / 2) {
                "contentPadding too large against constraints!"
            }
            require(itemProvider.itemCount >= 3 || startPadding <= 0 || endPadding <= 0) {
                "more than 3 items required when both start and end contentPadding set!"
            }

            state.updateAnchorsOnLayout(pageWidth, startPadding)

            // calculate height from width and aspectRation
            val height = min((pageWidth / aspectRatio).roundToInt(), constraints.maxHeight)

            // page indices to be drawn (may be negative!)
            val indices = state.getVisiblePages(containerWidth, pageWidth)

            // constraints for drawing each page
            val pageConstraints = Constraints(
                maxWidth = pageWidth,
                maxHeight = height,
            )
            val placeableMap = indices.associateWith {
                // index must be normalized in [0, size)
                val index = it.mod(itemProvider.itemCount)
                measure(index, pageConstraints)
            }

            layout(containerWidth, height) {
                placeableMap.forEach { (index, placeables) ->
                    // calculate position to be drawn at
                    val position = pageWidth * index + state.offset
                    placeables.forEach { placeable ->
                        placeable.placeRelative(position, 0)
                    }
                }
            }
        },
        modifier = modifier
            .clipToBounds()
            .anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = Orientation.Horizontal,
            ),
    )
}
