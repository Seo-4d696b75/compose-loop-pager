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

    private var pageCount = 0

    override var itemCount by mutableIntStateOf(0)
        private set

    private var content: (@Composable (Int) -> Unit) by mutableStateOf({})

    fun update(
        pageCount: Int,
        content: @Composable (Int) -> Unit,
    ) {
        if (this.pageCount != pageCount) {
            this.pageCount = pageCount
            this.itemCount = pageCount
        }
        this.content = content
    }

    /**
     * Updates visible page range and
     * gets item indices of LazyLayout to be measured and placed.
     */
    fun onLayout(visiblePages: List<Int>): List<Int> {
        if (visiblePages.isEmpty()) {
            return emptyList()
        }

        val firstPage = visiblePages.first()
        val indices = visiblePages.map {
            val normalized = it.mod(pageCount)
            val cycle = (it - firstPage) / pageCount
            normalized + cycle * pageCount
        }

        // may be > pageCount if same pages are displayed simultaneously
        itemCount = (indices.max() + 1).coerceAtLeast(itemCount)

        return indices
    }

    override fun getKey(index: Int) = index

    @Composable
    override fun Item(index: Int, key: Any) {
        val normalizedPage = index.mod(pageCount)
        content.invoke(normalizedPage)
    }
}
