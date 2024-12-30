package com.seo4d696b75.compose.pager.sample.ui.sample.section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seo4d696b75.compose.pager.HorizontalLoopPager
import com.seo4d696b75.compose.pager.rememberLoopPagerState
import com.seo4d696b75.compose.pager.sample.ui.theme.MyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoopPagerFewPagesSection(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "LoopPager with few pages") },
            )
        },
    ) {
        val state = rememberLoopPagerState(pageCount = 1)
        val state2 = rememberLoopPagerState(pageCount = 2)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState()),
        ) {
            HorizontalLoopPager(
                state = state,
                aspectRatio = 1f,
                contentPadding = PaddingValues(horizontal = 48.dp),
                pageSpacing = 48.dp,
            ) { _ ->
                Card(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "only one page",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            HorizontalLoopPager(
                state = state2,
                aspectRatio = 1f,
                contentPadding = PaddingValues(horizontal = 48.dp),
                pageSpacing = 24.dp,
                modifier = Modifier.fillMaxWidth(),
            ) { page ->
                Card(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${page + 1}/2",
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
private fun LoopPagerFewPagesSectionPreview() {
    MyTheme {
        LoopPagerFewPagesSection()
    }
}
