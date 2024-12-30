package com.seo4d696b75.compose.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun rememberPagerItemProvider(
    pageCount: Int,
    content: @Composable (index: Int) -> Unit,
): PagerItemProvider = remember { PagerItemProvider() }.apply {
    update(pageCount, content)
}

@OptIn(ExperimentalFoundationApi::class)
internal class PagerItemProvider : LazyLayoutItemProvider {

    var pageCount = 0
        private set

    private var visiblePageCount = 0

    private var itemCountState = mutableIntStateOf(0)
    private var content: (@Composable (Int) -> Unit) by mutableStateOf({})

    fun update(
        pageCount: Int,
        content: @Composable (Int) -> Unit,
    ) {
        if (this.pageCount != pageCount) {
            this.pageCount = pageCount
            this.visiblePageCount = pageCount
            itemCountState.intValue = pageCount
        }
        this.content = content
    }

    fun updateVisiblePageCount(count: Int) {
        // update if needed
        visiblePageCount = count.coerceAtLeast(visiblePageCount)
        // may be > pageCount if same pages are displayed simultaneously
        itemCountState.intValue = visiblePageCount.coerceAtLeast(pageCount)
    }

    override val itemCount by itemCountState

    @Composable
    override fun Item(index: Int, key: Any) {
        val normalized = index.mod(pageCount)
        content.invoke(normalized)
    }
}
