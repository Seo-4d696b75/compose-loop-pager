package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalLoopPager(
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable PagerScope.(page: Int) -> Unit,
) {
    val state = rememberPagerState(
        initialPage = (Int.MAX_VALUE / 2 / count) * count,
        pageCount = { Int.MAX_VALUE },
    )
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
