package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import kotlin.math.floor

@Composable
fun rememberLoopPagerState(): LoopPagerState {
    return remember { LoopPagerState() }
}

@Stable
class LoopPagerState {

    private val _offsetState = mutableIntStateOf(0)
    val offset: Int
        get() = _offsetState.intValue

    fun onDrag(delta: Int) {
        _offsetState.intValue = this.offset + delta
    }

    fun getVisiblePages(pageSize: Int): Iterable<Int> {
        val start = floor(-offset.toFloat() / pageSize).toInt()
        val end = floor((-offset + pageSize).toFloat() / pageSize).toInt()

        return start..end
    }
}
