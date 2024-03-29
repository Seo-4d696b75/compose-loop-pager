package com.seo4d696b75.android.loop_pager_sample.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.ImmutableList

@Composable
fun <T> rememberItemProvider(
    items: ImmutableList<T>,
    content: @Composable (item: T, index: Int) -> Unit,
) = remember { ItemProvider(items, content) }.apply {
    update(items, content)
}

@OptIn(ExperimentalFoundationApi::class)
class ItemProvider<T> internal constructor(
    initialItems: ImmutableList<T>,
    initialContent: @Composable (item: T, index: Int) -> Unit,
) : LazyLayoutItemProvider {

    private val itemsState = mutableStateOf(initialItems)
    private val contentState = mutableStateOf(initialContent)

    fun update(items: ImmutableList<T>, content: @Composable (T, Int) -> Unit) {
        itemsState.value = items
        contentState.value = content
    }

    override val itemCount
        get() = itemsState.value.size

    @Composable
    override fun Item(index: Int, key: Any) {
        itemsState.value.getOrNull(index)?.let {
            contentState.value.invoke(it, index)
        }
    }
}
