package com.seo4d696b75.compose.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
internal fun rememberPagerItemProvider(
    itemCount: Int,
    content: @Composable (index: Int) -> Unit,
): PagerItemProvider = remember { PagerItemProvider(itemCount, content) }.apply {
    itemCountState.intValue = itemCount
    contentState.value = content
}


@OptIn(ExperimentalFoundationApi::class)
internal class PagerItemProvider(
    itemCount: Int,
    content: @Composable (Int) -> Unit,
) : LazyLayoutItemProvider {

    val itemCountState = mutableIntStateOf(itemCount)
    val contentState = mutableStateOf(content)

    override val itemCount by itemCountState

    @Composable
    override fun Item(index: Int, key: Any) {
        contentState.value.invoke(index)
    }
}
