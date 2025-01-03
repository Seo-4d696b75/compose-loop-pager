package com.seo4d696b75.compose.pager

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * @param pageCount number of repeating page cycles
 * @param initialPage first page to be displayed
 */
@Composable
fun rememberLoopPagerState(
    pageCount: Int,
    initialPage: Int = 0,
): LoopPagerState {
    return rememberSaveable(
        saver = LoopPagerState.Saver(
            pageCount = pageCount,
        )
    ) {
        LoopPagerState(
            pageCount = pageCount,
            initialPage = initialPage,
        )
    }.also {
        it.pageCount = pageCount
    }
}

@Stable
class LoopPagerState(
    pageCount: Int,
    initialPage: Int,
) : ScrollableState {

    private val scrollableState = ScrollableState(::performScroll)

    var pageCount by mutableIntStateOf(pageCount)
        internal set

    /**
     * Current page index.
     *
     * This index may be
     * - negative value
     * - non-integer while scrolling or snap (fling) animation running.
     *
     * This index can also be interpreted as the scroll amount
     * normalized in page interval (page size + spacing).
     * Note: the sign is opposite to that of scroll value in pixels.
     *
     * The zero point of this index is defined as the first displayed state:
     *
     * ```
     *  viewport of pager
     * ┌────────────────────┬──────────┬───────────────────┐
     * │beforeContentPadding│ pageSize │afterContentPadding│
     * └────────────────────┴──────────┴───────────────────┘
     * ```
     */
    var page: Float by mutableFloatStateOf(initialPage.toFloat())
        internal set

    /**
     * An index of page to which the current pager will be snapped.
     *
     * This index must be the same value of [page]
     * when no scroll or snap (fling) animation is running ([isScrollInProgress] == `false`).
     * This snap position only takes account of the scroll offset,
     * not the current scroll (fling) velocity.
     *
     * The threshold of snap position is 50% of page interval (page size + spacing).
     */
    val currentPage: Int by derivedStateOf {
        page.roundToInt()
    }

    /**
     * An index of page to which the pager should be snapped.
     *
     * Unlike [currentPage], this index can only be updated when a user scrolling is completed
     * and the final snap position is determined.
     */
    var targetPage: Int by mutableIntStateOf(initialPage)
        internal set

    private var previousSettlePage = initialPage

    /**
     * An index of currently displayed page.
     *
     * Unlike [currentPage] or [targetPage],
     * this index is NOT changed while user scrolling or snap (fling) animation running
     * ([isScrollInProgress] == `true`).
     */
    val settledPage: Int by derivedStateOf {
        val current = this.page
        val target = this.targetPage
        if ((target - current).absoluteValue < 1e-6) {
            previousSettlePage = target
            target
        } else {
            previousSettlePage
        }
    }

    /**
     * A [LoopPagerLayoutInfo] that contains useful dimension values about the pager layout.
     */
    var layoutInfo: LoopPagerLayoutInfo by mutableStateOf(LoopPagerLayoutInfo.Zero)
        private set

    fun requireLayoutInfo() = layoutInfo as? LoopPagerLayoutInfo.Measured
        ?: throw IllegalStateException("pager not layout yet")

    /**
     * Updates layout size and get page indices to be shown
     */
    internal fun onLayout(layoutInfo: LoopPagerLayoutInfo.Measured): Iterable<Int> {
        this.layoutInfo = layoutInfo

        // normalized dimension
        val interval = layoutInfo.pageInterval.toFloat()
        val pageSize = layoutInfo.pageSize / interval

        /*  range of viewport measured in below coordinate
         *
         *    page interval
         *   ┌──────────┬─────────────┐
         *   │ pageSize │ pageSpacing │
         *   └──────────┴─────────────┘
         * ──┼────────────────────────┼───────────────➤ page
         *   0                        1
         */
        val start = page - layoutInfo.beforeContentPadding / interval
        val end = start + layoutInfo.viewportSize / interval

        val lower = floor(start).let {
            if (start - it >= pageSize) {
                // only pageSpacing after `it` page is visible
                it + 1
            } else {
                it
            }
        }.roundToInt()

        val upper = floor(end).roundToInt()

        require(lower <= upper)

        return lower..upper
    }

    /**
     * Gets main axis offset of the specified page in pixels.
     *
     * This offset value is relative to the start (horizontal) or
     * the top (vertical) of the pager container.
     */
    fun offset(page: Int): Int = with(requireLayoutInfo()) {
        (beforeContentPadding + pageInterval * (page - this@LoopPagerState.page)).roundToInt()
    }

    // scroll logic
    private fun performScroll(delta: Float): Float {
        return when (val info = layoutInfo) {
            is LoopPagerLayoutInfo.Measured -> {
                page -= (delta / info.pageInterval)
                // consume all scroll amount
                delta
            }

            LoopPagerLayoutInfo.Zero -> 0f
        }
    }

    internal val interactionSource = MutableInteractionSource()

    override val isScrollInProgress by derivedStateOf {
        (page - settledPage).absoluteValue > 1e-6
    }

    override fun dispatchRawDelta(delta: Float) = scrollableState.dispatchRawDelta(delta)

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) = scrollableState.scroll(scrollPriority, block)

    /**
     * Always returns `true` because this pager can be scrolled infinitely.
     */
    override val canScrollForward = true

    /**
     * Always returns `true` because this pager can be scrolled infinitely.
     */
    override val canScrollBackward = true

    /**
     * Scroll to a given [page] with animation.
     */
    suspend fun animateScrollToPage(
        page: Int,
        animationSpec: AnimationSpec<Float> = spring(),
    ) {
        when (val info = layoutInfo) {
            is LoopPagerLayoutInfo.Measured -> scroll {
                targetPage = page
                val scrollAmount = -(page - this@LoopPagerState.page) * info.pageInterval
                var previous = 0f
                animate(
                    initialValue = 0f,
                    targetValue = scrollAmount,
                    animationSpec = animationSpec,
                ) { current, _ ->
                    val delta = current - previous
                    val consumed = scrollBy(delta)
                    previous += consumed
                }
                // Cumulative error of delta may be impossible to be ignored
                this@LoopPagerState.page = page.toFloat()
            }

            LoopPagerLayoutInfo.Zero -> scrollToPage(page)
        }
    }

    /**
     * Scroll (jump immediately without animation) to a given [page].
     */
    fun scrollToPage(page: Int) {
        this.page = page.toFloat()
        targetPage = page
    }

    companion object {
        // current page is saved and will be restored
        fun Saver(
            pageCount: Int,
        ): Saver<LoopPagerState, Int> = Saver(
            save = { it.currentPage },
            restore = {
                LoopPagerState(
                    pageCount = pageCount,
                    initialPage = it,
                )
            },
        )
    }
}
