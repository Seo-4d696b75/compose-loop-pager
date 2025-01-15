package com.seo4d696b75.compose.pager

import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.runtime.Stable

/**
 * Almost same interface as [PagerSnapDistance],
 * but the calculated page index has no boundary (can be negative).
 */
@Stable
interface LoopPagerSnapDistance : PagerSnapDistance {
    companion object {
        /**
         * Limits the maximum number of pages that can be flung per fling gesture.
         * @param pages The maximum number of extra pages that can be flung at once.
         */
        fun atMost(pages: Int): LoopPagerSnapDistance {
            require(pages >= 0) {
                "pages should be greater than or equal to 0. You have used $pages."
            }
            return LoopPagerSnapDistanceMaxPages(pages)
        }

        /**
         * No limit for snap distance.
         *
         * The suggest page index calculated from scroll position and velocity
         * is returned without change.
         */
        val NoLimit = object : LoopPagerSnapDistance {
            override fun calculateTargetPage(
                startPage: Int,
                suggestedTargetPage: Int,
                velocity: Float,
                pageSize: Int,
                pageSpacing: Int
            ) = suggestedTargetPage
        }
    }
}

internal class LoopPagerSnapDistanceMaxPages(
    private val maxPages: Int,
) : LoopPagerSnapDistance {
    override fun calculateTargetPage(
        startPage: Int,
        suggestedTargetPage: Int,
        velocity: Float,
        pageSize: Int,
        pageSpacing: Int
    ): Int {
        val startPageLong = startPage.toLong()
        val min = (startPageLong - maxPages).coerceAtLeast(Int.MIN_VALUE.toLong()).toInt()
        val max = (startPageLong + maxPages).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        return suggestedTargetPage.coerceIn(min, max)
    }

    override fun equals(other: Any?): Boolean {
        return other is LoopPagerSnapDistanceMaxPages &&
                this.maxPages == other.maxPages
    }

    override fun hashCode(): Int {
        return maxPages.hashCode()
    }
}
