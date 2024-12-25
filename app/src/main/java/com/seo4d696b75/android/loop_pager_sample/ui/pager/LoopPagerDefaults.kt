package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
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
