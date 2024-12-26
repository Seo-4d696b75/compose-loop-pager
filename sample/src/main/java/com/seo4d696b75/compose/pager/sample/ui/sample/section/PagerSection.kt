package com.seo4d696b75.compose.pager.sample.ui.sample.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seo4d696b75.compose.pager.filterUserInput
import com.seo4d696b75.compose.pager.sample.ui.theme.MyTheme
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagerSection(
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection.filterUserInput()),
        topBar = {
            TopAppBar(
                title = { Text(text = "Pager") },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        val items = remember {
            (0..4).toPersistentList()
        }
        val horizontalPagerState = rememberPagerState { items.size }
        val verticalPagerState = rememberPagerState { items.size }

        val scope = rememberCoroutineScope()
        val animate = remember(scope) {
            { diff: Int ->
                scope.launch {
                    val page = horizontalPagerState.settledPage + diff
                    horizontalPagerState.animateScrollToPage(page)
                }
                scope.launch {
                    val page = verticalPagerState.settledPage + diff
                    verticalPagerState.animateScrollToPage(page)
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState()),
        ) {
            HorizontalPager(
                state = horizontalPagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 48.dp),
                pageSpacing = 24.dp,
            ) { page ->
                val item = items[page]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "item: $item",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "scroll with animation")

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = { animate(-2) },
                ) {
                    Text(text = "-2")
                }
                Button(
                    onClick = { animate(-1) },
                ) {
                    Text(text = "-1")
                }
                Button(
                    onClick = { animate(1) },
                ) {
                    Text(text = "+1")
                }
                Button(
                    onClick = { animate(2) },
                ) {
                    Text(text = "+2")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            VerticalPager(
                state = verticalPagerState,
                contentPadding = PaddingValues(top = 48.dp, bottom = 24.dp),
                pageSpacing = 24.dp,
                modifier = Modifier.height(360.dp),
            ) { page ->
                val item = items[page]
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "item: $item",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PagerSectionPreview() {
    MyTheme {
        PagerSection()
    }
}
