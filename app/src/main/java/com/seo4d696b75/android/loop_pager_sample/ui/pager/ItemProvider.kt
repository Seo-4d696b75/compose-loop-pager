package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberItemProvider(
    itemCount: Int,
    content: @Composable (index: Int) -> Unit,
) = remember { ItemProvider(itemCount, content) }.apply {
    itemCountState.intValue = itemCount
    contentState.value = content
}

@OptIn(ExperimentalFoundationApi::class)
class ItemProvider internal constructor(
    itemCount: Int,
    content: @Composable (Int) -> Unit,
) : LazyLayoutItemProvider {

    internal val itemCountState = mutableIntStateOf(itemCount)
    internal val contentState = mutableStateOf(content)

    override val itemCount by itemCountState

    @Composable
    override fun Item(index: Int, key: Any) {
        contentState.value.invoke(index)
    }
}
