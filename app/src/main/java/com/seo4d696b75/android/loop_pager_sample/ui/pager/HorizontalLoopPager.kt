package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.rememberPagerState

@Composable
fun HorizontalLoopPager(
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable PagerScope.(page: Int) -> Unit,
) {
    val state = rememberPagerState(
        initialPage = (Int.MAX_VALUE / 2 / count) * count
    )
    Box(
        contentAlignment = Alignment.TopCenter,
    ) {
        HorizontalPager(
            count = Int.MAX_VALUE,
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
