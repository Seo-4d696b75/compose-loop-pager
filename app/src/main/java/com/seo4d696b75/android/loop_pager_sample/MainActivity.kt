package com.seo4d696b75.android.loop_pager_sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seo4d696b75.android.loop_pager_sample.ui.pager.HorizontalLoopPager
import com.seo4d696b75.android.loop_pager_sample.ui.pager.rememberLoopPagerState
import com.seo4d696b75.android.loop_pager_sample.ui.theme.MyTheme
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PagerSection()
                }
            }
        }
    }
}

@Composable
fun PagerSection(modifier: Modifier = Modifier) {
    val items = remember {
        (0..4).toPersistentList()
    }
    val pagerState = rememberLoopPagerState(pageCount = items.size)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(24.dp),
            text = "Horizontal Loop Pager",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalLoopPager(
            state = pagerState,
            aspectRatio = 1f,
            contentPadding = PaddingValues(horizontal = 48.dp),
        ) { page ->
            val item = items[page]
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
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

        val scope = rememberCoroutineScope()
        val animate = remember(scope, pagerState) {
            { diff: Int ->
                scope.launch {
                    val page = pagerState.currentPage + diff
                    pagerState.animateScrollToPage(page)
                }
            }
        }
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
    }
}

@Preview(showBackground = true)
@Composable
fun PagerSectionPreview() {
    MyTheme {
        PagerSection()
    }
}
