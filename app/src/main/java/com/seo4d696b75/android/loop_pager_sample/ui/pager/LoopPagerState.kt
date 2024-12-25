package com.seo4d696b75.android.loop_pager_sample.ui.pager

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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.math.absoluteValue
import kotlin.math.ceil
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
     */
    val currentPage: Int by derivedStateOf {
        /*val pageSize = this.pageSize
        val pageSpacing = this.pageSpacing
        val adjustedPage = if (pageSize == 0 || pageSpacing == 0) {
            currentPage
        } else {
            val pageInterval = pageSize + pageSpacing
            val diff = pageSpacing * 0.5f / pageInterval
            currentPage + diff
        }
        adjustedPage.roundToInt()*/
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
    val settlePage: Int by derivedStateOf {
        val current = this.page
        val target = this.targetPage
        if ((target - current).absoluteValue < 1e-6) {
            previousSettlePage = target
            target
        } else {
            previousSettlePage
        }
    }

    internal var pageSize: Int = 0
    private var startPadding: Int = 0

    /**
     * Updates layout size and get page indices to be shown
     */
    internal fun onLayout(
        containerSize: Int,
        pageSize: Int,
        startPadding: Int,
    ): Iterable<Int> {
        this.pageSize = pageSize
        this.startPadding = startPadding
        val start = -startPadding.toFloat() / pageSize + page
        val end = start + containerSize / pageSize

        return floor(start).roundToInt()..ceil(end).roundToInt()
    }

    /**
     * Gets main axis offset of the specified page in pixels.
     *
     * This offset value is relative to the start (horizontal) or
     * the top (vertical) of the pager container.
     */
    fun offset(page: Int): Int {
        return (startPadding + pageSize * (page - this.page)).roundToInt()
    }

    // scroll logic
    private fun performScroll(delta: Float): Float {
        val interval = pageSize
        return if (interval > 0) {
            page -= (delta / interval)
            // consume all scroll amount
            delta
        } else {
            0f
        }
    }

    internal val interactionSource = MutableInteractionSource()

    override val isScrollInProgress by derivedStateOf {
        (page - settlePage).absoluteValue > 1e-6
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
        val interval = pageSize
        if (interval > 0) {
            scroll {
                targetPage = page
                val scrollAmount = -(page - this@LoopPagerState.page) * interval
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
            }
        } else {
            scrollToPage(page)
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
