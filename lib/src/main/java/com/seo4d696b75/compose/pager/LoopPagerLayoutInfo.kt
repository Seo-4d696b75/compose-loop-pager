package com.seo4d696b75.compose.pager

import androidx.compose.runtime.Immutable

/**
 * Collection of measured dimensions in pixels about a pager layout.
 *
 * Almost all the values are oriented in main axis of the pager.
 */
@Immutable
sealed interface LoopPagerLayoutInfo {
    val viewportSize: Int
    val viewportSizeInCrossAxis: Int
    val pageSize: Int
    val pageSpacing: Int
    val beforeContentPadding: Int
    val afterContentPadding: Int

    /**
     * Not layout yet.
     */
    data object Zero : LoopPagerLayoutInfo {
        override val viewportSize = 0
        override val viewportSizeInCrossAxis = 0
        override val pageSize = 0
        override val pageSpacing = 0
        override val beforeContentPadding = 0
        override val afterContentPadding = 0
    }

    /**
     * Already measured on layout.
     */
    data class Measured(
        override val viewportSize: Int,
        override val viewportSizeInCrossAxis: Int,
        override val pageSize: Int,
        override val pageSpacing: Int,
        override val beforeContentPadding: Int,
        override val afterContentPadding: Int,
    ) : LoopPagerLayoutInfo

    val pageInterval: Int
        get() = pageSize + pageSpacing
}
