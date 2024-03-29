package com.seo4d696b75.android.loop_pager_sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seo4d696b75.android.loop_pager_sample.ui.pager.HorizontalLoopPager
import com.seo4d696b75.android.loop_pager_sample.ui.theme.MyTheme
import kotlinx.collections.immutable.toPersistentList

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
            items = items,
            aspectRatio = 1f,
            contentPadding = PaddingValues(horizontal = 48.dp),
        ) { item, _ ->
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
    }
}

@Preview(showBackground = true)
@Composable
fun PagerSectionPreview() {
    MyTheme {
        PagerSection()
    }
}
