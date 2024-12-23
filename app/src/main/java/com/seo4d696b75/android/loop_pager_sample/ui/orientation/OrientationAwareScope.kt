package com.seo4d696b75.android.loop_pager_sample.ui.orientation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Constraints.Companion.Infinity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

fun <T> withOrientation(
    orientation: Orientation,
    block: OrientationAwareScope.() -> T,
): T {
    val scope = OrientationAwareScope(orientation)
    return with(scope) { block() }
}

class OrientationAwareScope(
    private val orientation: Orientation,
) {
    val Constraints.minSizeInMainAxis: Int
        get() = when (orientation) {
            Orientation.Horizontal -> minWidth
            Orientation.Vertical -> minHeight
        }

    val Constraints.maxSizeInMainAxis: Int
        get() = when (orientation) {
            Orientation.Horizontal -> maxWidth
            Orientation.Vertical -> maxHeight
        }

    val Constraints.minSizeInCrossAxis: Int
        get() = when (orientation) {
            Orientation.Horizontal -> minHeight
            Orientation.Vertical -> minWidth
        }

    val Constraints.maxSizeInCrossAxis: Int
        get() = when (orientation) {
            Orientation.Horizontal -> maxHeight
            Orientation.Vertical -> maxWidth
        }

    fun PaddingValues.calculateStartPaddingInMainAxis(
        direction: LayoutDirection,
    ): Dp = when (orientation) {
        Orientation.Horizontal -> calculateStartPadding(direction)
        Orientation.Vertical -> calculateTopPadding()
    }

    fun PaddingValues.calculateEndPaddingInMainAxis(
        direction: LayoutDirection,
    ): Dp = when (orientation) {
        Orientation.Horizontal -> calculateEndPadding(direction)
        Orientation.Vertical -> calculateBottomPadding()
    }

    fun Int.toCrossAxis(
        aspectRatio: Float,
    ): Float = when (orientation) {
        Orientation.Horizontal -> this / aspectRatio
        Orientation.Vertical -> this * aspectRatio
    }

    fun orientedConstraints(
        minSizeInMainAxis: Int = 0,
        maxSizeInMainAxis: Int = Infinity,
        minSizeInCrossAxis: Int = 0,
        maxSizeInCrossAxis: Int = Infinity
    ): Constraints = when (orientation) {
        Orientation.Horizontal ->
            Constraints(
                minWidth = minSizeInMainAxis,
                maxWidth = maxSizeInMainAxis,
                minHeight = minSizeInCrossAxis,
                maxHeight = maxSizeInCrossAxis,
            )

        Orientation.Vertical ->
            Constraints(
                minWidth = minSizeInCrossAxis,
                maxWidth = maxSizeInCrossAxis,
                minHeight = minSizeInMainAxis,
                maxHeight = maxSizeInMainAxis,
            )
    }

    fun horizontalOf(
        mainAxis: Int,
        crossAxis: Int,
    ) = when (orientation) {
        Orientation.Horizontal -> mainAxis
        Orientation.Vertical -> crossAxis
    }

    fun verticalOf(
        mainAxis: Int,
        crossAxis: Int,
    ) = when (orientation) {
        Orientation.Horizontal -> crossAxis
        Orientation.Vertical -> mainAxis
    }
}
