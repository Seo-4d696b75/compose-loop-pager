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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sign

@Stable
object LoopPagerDefaults {
    /**
     * Gets a [snapFlingBehavior] for loop pager.
     *
     * @param state loop pager state
     * @param snapAnimationSpec The animation spec used to finally snap to the position.
     * @param decayAnimationSpec The animation spec used to approach the target offset.
     *   May not be used when the final snap position is close enough.
     * @param velocityThreshold The threshold in pixels to detect a fling,
     *   meaning whether the scroll velocity is large enough or not.
     * @param pagerSnapDistance A way to control the snapping destination.
     *   Default behavior: any fling with large velocity results in snapping to the next page
     *   in the same direction of the fling.
     */
    @Composable
    fun flingBehavior(
        state: LoopPagerState,
        snapAnimationSpec: AnimationSpec<Float> = spring(),
        decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
        velocityThreshold: Density.() -> Float = { 400.dp.toPx() },
        pagerSnapDistance: LoopPagerSnapDistance = LoopPagerSnapDistance.atMost(1),
    ): TargetedFlingBehavior {
        val density = LocalDensity.current
        val velocityThresholdPx = with(density) { velocityThreshold() }
        return remember(
            state,
            snapAnimationSpec,
            decayAnimationSpec,
            velocityThresholdPx,
            pagerSnapDistance,
        ) {
            snapFlingBehavior(
                snapLayoutInfoProvider = LoopPagerSnapLayoutProvider(
                    state = state,
                    velocityThreshold = velocityThresholdPx,
                    pagerSnapDistance = pagerSnapDistance,
                ),
                decayAnimationSpec = decayAnimationSpec,
                snapAnimationSpec = snapAnimationSpec,
            )
        }
    }
}

internal class LoopPagerSnapLayoutProvider(
    private val state: LoopPagerState,
    private val velocityThreshold: Float,
    private val pagerSnapDistance: LoopPagerSnapDistance,
) : SnapLayoutInfoProvider {
    override fun calculateSnapOffset(velocity: Float): Float {
        return when (val info = state.layoutInfo) {
            is LoopPagerLayoutInfo.Measured -> {
                val currentPage = state.page
                val snapPage = if (velocity.absoluteValue < velocityThreshold) {
                    // snap to the closest page
                    currentPage.roundToInt()
                } else {
                    // snap to the neighbor page in same direction as velocity
                    if (velocity > 0) {
                        floor(currentPage).roundToInt()
                    } else {
                        ceil(currentPage).roundToInt()
                    }
                }
                state.targetPage = snapPage
                -(snapPage - state.page) * info.pageInterval
            }

            LoopPagerLayoutInfo.Zero -> 0f
        }
    }

    override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float {
        // snap to the closed page position around the decayOffset
        // Note: decay animation is only used when the actual scroll offset > pageInterval
        return when (val info = state.layoutInfo) {
            is LoopPagerLayoutInfo.Measured -> {
                val currentPage = state.page
                val startPage = if (velocity > 0) {
                    ceil(currentPage).roundToInt()
                } else {
                    floor(currentPage).roundToInt()
                }
                val decayPage = currentPage - decayOffset / info.pageInterval
                val snapPage = pagerSnapDistance.calculateTargetPage(
                    startPage = startPage,
                    suggestedTargetPage = decayPage.roundToInt(),
                    velocity = velocityThreshold,
                    pageSize = info.pageSize,
                    pageSpacing = info.pageSpacing,
                )
                val distance = (snapPage - currentPage).absoluteValue

                // We'd like the approach animation to finish right before the last page so we can
                // use a snapping animation for the rest (<= pageInterval).
                val decayDistance = (distance - 1f).coerceAtLeast(0f)

                return if (decayDistance == 0f) {
                    0f
                } else {
                    state.targetPage = snapPage
                    decayDistance * info.pageInterval * velocity.sign
                }
            }

            LoopPagerLayoutInfo.Zero -> 0f
        }
    }
}
