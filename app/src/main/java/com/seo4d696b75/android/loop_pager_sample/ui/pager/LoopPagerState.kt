package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.annotation.IntRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * @param pageCount number of repeating page cycles
 * @param initialPage first page to be displayed
 * @param positionalThreshold Threshold for the amount of drag
 *  to determine whether to snap to the next page in a user's swipe operation
 * @param velocityThreshold Speed threshold for determining whether to snap to the next page
 *   in a user's fling operation (pixels/sec)
 * @param snapAnimationSpec default animation of page snapping
 * @param decayAnimationSpec animation used for a fling operation
 * @param anchorSize anchors to be places on both sides relative to the current page position.
 */
@Composable
fun rememberLoopPagerState(
    pageCount: Int,
    initialPage: Int = 0,
    positionalThreshold: (totalDistance: Float) -> Float = { it * 0.5f },
    velocityThreshold: Density.() -> Float = { 125.dp.toPx() },
    snapAnimationSpec: AnimationSpec<Float> = spring(),
    decayAnimationSpec: DecayAnimationSpec<Float> = exponentialDecay(),
    @IntRange(from = 1) anchorSize: Int = max(pageCount / 2, 3),
): LoopPagerState {
    val density = LocalDensity.current
    return rememberSaveable(
        saver = LoopPagerState.Saver(
            pageCount = pageCount,
            positionalThreshold = positionalThreshold,
            velocityThreshold = { velocityThreshold.invoke(density) },
            snapAnimationSpec = snapAnimationSpec,
            decayAnimationSpec = decayAnimationSpec,
            anchorSize = anchorSize,
        )
    ) {
        LoopPagerState(
            pageCount = pageCount,
            initialPage = initialPage,
            positionalThreshold = positionalThreshold,
            velocityThreshold = { velocityThreshold.invoke(density) },
            snapAnimationSpec = snapAnimationSpec,
            decayAnimationSpec = decayAnimationSpec,
            anchorSize = anchorSize,
        )
    }.also {
        it.pageCount = pageCount
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
class LoopPagerState(
    pageCount: Int,
    initialPage: Int,
    positionalThreshold: (totalDistance: Float) -> Float,
    velocityThreshold: () -> Float,
    snapAnimationSpec: AnimationSpec<Float>,
    decayAnimationSpec: DecayAnimationSpec<Float>,
    // due to limitation of AnchoredDraggableState,
    // anchor size can not be changed dynamically
    private val anchorSize: Int,
) {
    var pageCount by mutableIntStateOf(pageCount)
        internal set

    private var pageOffset = initialPage

    // pageIndex = pageOffset + anchoredDraggableState.value
    internal val anchoredDraggableState = AnchoredDraggableState(
        initialValue = 0,
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold,
        snapAnimationSpec = snapAnimationSpec,
        decayAnimationSpec = decayAnimationSpec,
    )

    /**
     * drawing position offset in pixels
     */
    val offset: Int
        get() = anchoredDraggableState.requireOffset().roundToInt()

    /**
     * current page index. **may be negative value**
     */
    val currentPage: Int
        get() = anchoredDraggableState.currentValue + pageOffset

    val settlePage: Int
        get() = anchoredDraggableState.settledValue + pageOffset

    /**
     * target page index.
     *
     * - When a user is swiping the pager and snapping to the next page is detected,
     *   [currentPage] ± 1 will be returned.
     * - Otherwise, the same value of [currentPage] will be returned.
     */
    val targetPage: Int
        get() = anchoredDraggableState.targetValue + pageOffset

    internal fun getVisiblePages(
        containerSize: Int,
        pageSize: Int,
        pageSpacing: Int,
    ): Iterable<Int> {
        val pageInterval = pageSize + pageSpacing
        val start = floor((-offset + pageSpacing).toFloat() / pageInterval).toInt()
        val end = floor((-offset + containerSize).toFloat() / pageInterval).toInt()

        return start..end
    }

    private var latestPageInterval: Int? = null
    private var latestStartPadding: Int? = null

    // update anchor positions if needed
    internal fun updateAnchorsOnLayout(pageInterval: Int, startPadding: Int) {
        if (latestPageInterval != pageInterval && latestStartPadding != startPadding) {
            latestPageInterval = pageInterval
            latestStartPadding = startPadding
            anchoredDraggableState.updateAnchors(
                newAnchors = calculateAnchors(pageInterval, startPadding),
            )
        }
    }

    // update anchor positions if current page is changed
    internal fun updateAnchorsOnSettle() {
        val pageInterval = latestPageInterval
        val startPadding = latestStartPadding
        val diff = anchoredDraggableState.settledValue
        if (pageInterval != null && startPadding != null && diff != 0) {
            // anchoredDraggableState.**Value = relative page index must be set 0
            pageOffset += diff
            anchoredDraggableState.updateAnchors(
                newAnchors = calculateAnchors(pageInterval, startPadding),
                newTarget = 0,
            )
        }
    }

    private fun calculateAnchors(pageInterval: Int, startPadding: Int) = DraggableAnchors {
        (-anchorSize..anchorSize).forEach { index ->
            val page = index + pageOffset
            index at -page * pageInterval.toFloat() + startPadding
        }
    }

    suspend fun animateScrollToPage(page: Int) {
        val index = page - pageOffset
        if (index in -anchorSize..anchorSize) {
            anchoredDraggableState.animateTo(index)
        }
    }

    suspend fun scrollToPage(page: Int) {
        val index = page - pageOffset
        if (index in -anchorSize..anchorSize) {
            anchoredDraggableState.snapTo(index)
        }
    }

    companion object {
        // current page is saved and will be restored
        fun Saver(
            pageCount: Int,
            positionalThreshold: (totalDistance: Float) -> Float,
            velocityThreshold: () -> Float,
            snapAnimationSpec: AnimationSpec<Float>,
            decayAnimationSpec: DecayAnimationSpec<Float>,
            anchorSize: Int,
        ): Saver<LoopPagerState, Int> = Saver(
            save = { it.currentPage },
            restore = {
                LoopPagerState(
                    pageCount = pageCount,
                    initialPage = it,
                    positionalThreshold = positionalThreshold,
                    velocityThreshold = velocityThreshold,
                    snapAnimationSpec = snapAnimationSpec,
                    decayAnimationSpec = decayAnimationSpec,
                    anchorSize = anchorSize,
                )
            },
        )
    }
}
