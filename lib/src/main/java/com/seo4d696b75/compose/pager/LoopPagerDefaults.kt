package com.seo4d696b75.compose.pager

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sign

@Stable
object LoopPagerDefaults {
    @Composable
    fun flingBehavior(
        state: LoopPagerState,
        snapAnimationSpec: AnimationSpec<Float> = spring(),
        decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
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
        return when (val info = state.layoutInfo) {
            is LoopPagerLayoutInfo.Measured -> {
                state.targetPage = snapPage
                -(snapPage - state.page) * info.pageInterval
            }

            LoopPagerLayoutInfo.Zero -> 0f
        }
    }

    override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float {
        // snap to the closed page position around the decayOffset
        return when (val info = state.layoutInfo) {
            is LoopPagerLayoutInfo.Measured -> {
                val currentPage = state.page
                val decayPage = currentPage - decayOffset / info.pageInterval
                val snapPage = decayPage.roundToInt()
                val distance = (snapPage - currentPage).absoluteValue

                // We'd like the approach animation to finish right before the last page so we can
                // use a snapping animation for the rest.
                return if (distance <= 1f) {
                    0f
                } else {
                    state.targetPage = snapPage
                    distance * info.pageInterval * velocity.sign
                }
            }

            LoopPagerLayoutInfo.Zero -> 0f
        }
    }
}
