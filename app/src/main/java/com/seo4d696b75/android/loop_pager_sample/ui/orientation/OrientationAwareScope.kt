package com.seo4d696b75.android.loop_pager_sample.ui.orientation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Constraints.Companion.Infinity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity

interface OrientationAwareScope {
    val Constraints.minSizeInMainAxis: Int
    val Constraints.maxSizeInMainAxis: Int
    val Constraints.minSizeInCrossAxis: Int
    val Constraints.maxSizeInCrossAxis: Int
    fun PaddingValues.calculateStartPaddingInMainAxis(direction: LayoutDirection): Dp
    fun PaddingValues.calculateEndPaddingInMainAxis(direction: LayoutDirection): Dp
    fun Int.toCrossAxis(aspectRatio: Float): Float
    fun orientedConstraints(
        minSizeInMainAxis: Int = 0,
        maxSizeInMainAxis: Int = Infinity,
        minSizeInCrossAxis: Int = 0,
        maxSizeInCrossAxis: Int = Infinity,
    ): Constraints

    fun horizontalOf(mainAxis: Int, crossAxis: Int): Int
    fun verticalOf(mainAxis: Int, crossAxis: Int): Int
    val Offset.mainAxis: Float
    val Offset.crossAxis: Float
    fun Float.toOffsetAsMainAxis(): Offset
    fun Float.toOffsetAsCrossAxis(): Offset
    val Velocity.mainAxis: Float
    fun Float.toVelocityAsMainAxis(): Velocity
}

fun Orientation.asScope(): OrientationAwareScope = OrientationAwareScopeImpl(this)

private class OrientationAwareScopeImpl(
    private val orientation: Orientation,
) : OrientationAwareScope {
    override val Constraints.minSizeInMainAxis: Int
        get() = when (orientation) {
            Orientation.Horizontal -> minWidth
            Orientation.Vertical -> minHeight
        }

    override val Constraints.maxSizeInMainAxis: Int
        get() = when (orientation) {
            Orientation.Horizontal -> maxWidth
            Orientation.Vertical -> maxHeight
        }

    override val Constraints.minSizeInCrossAxis: Int
        get() = when (orientation) {
            Orientation.Horizontal -> minHeight
            Orientation.Vertical -> minWidth
        }

    override val Constraints.maxSizeInCrossAxis: Int
        get() = when (orientation) {
            Orientation.Horizontal -> maxHeight
            Orientation.Vertical -> maxWidth
        }

    override fun PaddingValues.calculateStartPaddingInMainAxis(
        direction: LayoutDirection,
    ): Dp = when (orientation) {
        Orientation.Horizontal -> calculateStartPadding(direction)
        Orientation.Vertical -> calculateTopPadding()
    }

    override fun PaddingValues.calculateEndPaddingInMainAxis(
        direction: LayoutDirection,
    ): Dp = when (orientation) {
        Orientation.Horizontal -> calculateEndPadding(direction)
        Orientation.Vertical -> calculateBottomPadding()
    }

    override fun Int.toCrossAxis(
        aspectRatio: Float,
    ): Float = when (orientation) {
        Orientation.Horizontal -> this / aspectRatio
        Orientation.Vertical -> this * aspectRatio
    }

    override fun orientedConstraints(
        minSizeInMainAxis: Int,
        maxSizeInMainAxis: Int,
        minSizeInCrossAxis: Int,
        maxSizeInCrossAxis: Int,
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

    override fun horizontalOf(
        mainAxis: Int,
        crossAxis: Int,
    ) = when (orientation) {
        Orientation.Horizontal -> mainAxis
        Orientation.Vertical -> crossAxis
    }

    override fun verticalOf(
        mainAxis: Int,
        crossAxis: Int,
    ) = when (orientation) {
        Orientation.Horizontal -> crossAxis
        Orientation.Vertical -> mainAxis
    }

    override val Offset.mainAxis: Float
        get() = when (orientation) {
            Orientation.Horizontal -> x
            Orientation.Vertical -> y
        }

    override val Offset.crossAxis: Float
        get() = when (orientation) {
            Orientation.Horizontal -> y
            Orientation.Vertical -> x
        }

    override fun Float.toOffsetAsMainAxis(): Offset = when (orientation) {
        Orientation.Horizontal -> Offset(this, 0f)
        Orientation.Vertical -> Offset(0f, this)
    }

    override fun Float.toOffsetAsCrossAxis(): Offset = when (orientation) {
        Orientation.Horizontal -> Offset(0f, this)
        Orientation.Vertical -> Offset(this, 0f)
    }

    override val Velocity.mainAxis: Float
        get() = when (orientation) {
            Orientation.Horizontal -> x
            Orientation.Vertical -> y
        }

    override fun Float.toVelocityAsMainAxis(): Velocity =
        when (orientation) {
            Orientation.Horizontal -> Velocity(this, 0f)
            Orientation.Vertical -> Velocity(0f, this)
        }
}
