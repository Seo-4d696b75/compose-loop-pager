package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import com.seo4d696b75.android.loop_pager_sample.ui.orientation.OrientationAwareScope
import com.seo4d696b75.android.loop_pager_sample.ui.orientation.asScope
import kotlin.math.roundToInt

@Stable
object LoopPagerDefaults {
    @Composable
    fun flingBehavior(
        state: LoopPagerState,
        snapAnimationSpec: AnimationSpec<Float> = spring(),
        decayAnimationSpec: DecayAnimationSpec<Float> = exponentialDecay(),
    ): TargetedFlingBehavior = remember(state, snapAnimationSpec, decayAnimationSpec) {
        snapFlingBehavior(
            snapLayoutInfoProvider = LoopPagerSnapLayoutProvider(state),
            decayAnimationSpec = decayAnimationSpec,
            snapAnimationSpec = snapAnimationSpec,
        )
    }

    @Composable
    fun nestedScrollConnection(
        state: LoopPagerState,
        orientation: Orientation
    ): NestedScrollConnection {
        return remember(state, orientation) {
            LoopPagerNestedScrollConnection(state, orientation)
        }
    }
}

internal class LoopPagerSnapLayoutProvider(
    private val state: LoopPagerState,
) : SnapLayoutInfoProvider {
    override fun calculateSnapOffset(velocity: Float): Float {
        // snap to the closest page
        val snapPage = state.currentPage
        val interval = state.pageSize
        return if (interval > 0) {
            state.targetPage = snapPage
            -(snapPage - state.page) * interval
        } else {
            0f
        }
    }

    override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float {
        // snap to the closed page position around the decayOffset
        val interval = state.pageSize
        return if (interval > 0) {
            val currentPage = state.page
            val decayPage = currentPage - decayOffset / interval
            val snapPage = decayPage.roundToInt()
            state.targetPage = snapPage
            -(snapPage - currentPage) * interval
        } else {
            0f
        }
    }
}

internal class LoopPagerNestedScrollConnection(
    private val state: LoopPagerState,
    private val orientation: Orientation,
) : NestedScrollConnection,
    OrientationAwareScope by orientation.asScope() {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return if (source == NestedScrollSource.UserInput && state.isScrollInProgress) {
            // when this pager is being scrolled,
            // 1. consume scroll delta in main axis with priority
            // 2. consume all the scroll delta in cross axis for preventing vertical vs. horizontal scroll conflicts.
            val consumedInMainAxis =
                -state.dispatchRawDelta(-available.mainAxis).toOffsetAsMainAxis()
            val consumedInCrossAxis = available.crossAxis.toOffsetAsCrossAxis()
            consumedInMainAxis + consumedInCrossAxis
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        return available.mainAxis.toVelocityAsMainAxis()
    }
}
