package com.seo4d696b75.compose.pager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

/**
 * Gets an adapter of this [NestedScrollConnection]
 * which will only consume scroll from [NestedScrollSource.UserInput].
 *
 * This is useful for a case in which both collapsing `TopAppBar` and `VerticalPager` are placed,
 * and scroll amount dispatched from snap animations of the pager should NOT be consumed.
 */
@Composable
fun NestedScrollConnection.filterUserInput() = remember(this) {
    val original = this
    object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource) =
            if (source == NestedScrollSource.UserInput) {
                original.onPreScroll(available, source)
            } else {
                Offset.Zero
            }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset = if (source == NestedScrollSource.UserInput) {
            original.onPostScroll(consumed, available, source)
        } else {
            Offset.Zero
        }
    }
}
