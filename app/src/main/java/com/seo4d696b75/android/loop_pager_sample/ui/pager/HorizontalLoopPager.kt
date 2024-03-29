package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Constraints
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> HorizontalLoopPager(
    items: ImmutableList<T>,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    state: LoopPagerState = rememberLoopPagerState(),
    content: @Composable (item: T, page: Int) -> Unit,
) {
    val itemProvider = rememberItemProvider(items, content)

    if (itemProvider.itemCount < 2) {
        // crash when scrolling
        return
    }

    LazyLayout(
        itemProvider = { itemProvider },
        prefetchState = null,
        measurePolicy = { constraints ->
            // max width of constraints
            val width = constraints.maxWidth
            // calculate height from width and aspectRation
            val height = (width / aspectRatio).roundToInt()

            // page indices to be drawn (may be negative!)
            val indices = state.getVisiblePages(width)

            // constraints for drawing each page
            val pageConstraints = Constraints(
                maxWidth = width,
                maxHeight = height,
            )
            val placeableMap = indices.associateWith {
                // index must be normalized in [0, size)
                val index = it.mod(itemProvider.itemCount)
                measure(index, pageConstraints)
            }

            layout(width, height) {
                placeableMap.forEach { (index, placeables) ->
                    // calculate position to be drawn at
                    val position = width * index + state.offset
                    placeables.forEach { placeable ->
                        placeable.placeRelative(position, 0)
                    }
                }
            }
        },
        modifier = modifier
            .clipToBounds()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, delta ->
                    change.consume()
                    state.onDrag(delta.roundToInt())
                }
            }
    )
}
