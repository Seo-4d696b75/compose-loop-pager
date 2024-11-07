package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun rememberLoopPagerState(
    positionalThreshold: (totalDistance: Float) -> Float = { it * 0.5f },
    velocityThreshold: Density.() -> Float = { 125.dp.toPx() },
    snapAnimationSpec: AnimationSpec<Float> = spring(),
    decayAnimationSpec: DecayAnimationSpec<Float> = exponentialDecay(),
): LoopPagerState {
    val density = LocalDensity.current
    return remember {
        LoopPagerState(
            positionalThreshold = positionalThreshold,
            velocityThreshold = { velocityThreshold.invoke(density) },
            snapAnimationSpec = snapAnimationSpec,
            decayAnimationSpec = decayAnimationSpec,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
class LoopPagerState(
    positionalThreshold: (totalDistance: Float) -> Float,
    velocityThreshold: () -> Float,
    snapAnimationSpec: AnimationSpec<Float>,
    decayAnimationSpec: DecayAnimationSpec<Float>,
) {
    internal val anchoredDraggableState = AnchoredDraggableState(
        initialValue = 0,
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold,
        snapAnimationSpec = snapAnimationSpec,
        decayAnimationSpec = decayAnimationSpec,
    )

    val offset: Int
        get() = anchoredDraggableState.requireOffset().roundToInt()

    val currentPage: Int
        get() = anchoredDraggableState.currentValue

    val targetPage: Int
        get() = anchoredDraggableState.targetValue

    fun getVisiblePages(containerSize: Int, pageSize: Int): Iterable<Int> {
        val start = floor(-offset.toFloat() / pageSize).toInt()
        val end = floor((-offset + containerSize).toFloat() / pageSize).toInt()

        return start..end
    }

    fun updateAnchors(pageSize: Int, padding: Int) {
        // set anchors for current and neighbor pages
        anchoredDraggableState.updateAnchors(
            DraggableAnchors {
                listOf(
                    currentPage - 1,
                    currentPage,
                    currentPage + 1,
                ).forEach { index ->
                    index at -index * pageSize.toFloat() + padding
                }
            }
        )
    }
}
