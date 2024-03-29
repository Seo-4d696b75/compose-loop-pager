package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalLoopPager(
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable PagerScope.(page: Int) -> Unit,
) {
    val state = rememberPagerState(
        initialPage = count,
        pageCount = { count * 3 },
    )

    LaunchedEffect(state) {
        // observe current page skipping while animation or user-interaction
        snapshotFlow { state.settledPage to state.isScrollInProgress }
            .filter { !it.second }
            .map { it.first }
            .collectLatest {
                // jump without animation if needed
                when {
                    it < count -> it + count
                    it >= count * 2 -> it - count
                    else -> null
                }?.let { idx ->
                    launch {
                        state.scrollToPage(idx)
                    }
                }
            }
    }

    Box(
        contentAlignment = Alignment.TopCenter,
    ) {
        HorizontalPager(
            state = state,
        ) {
            // normalize dummy index
            val index = it.mod(count)
            content(index)
        }
        Text(
            text = "dummyIndex: ${state.currentPage}\nnormalizedIndex: ${state.currentPage.mod(count)}",
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
