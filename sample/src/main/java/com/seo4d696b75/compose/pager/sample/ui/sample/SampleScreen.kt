package com.seo4d696b75.compose.pager.sample.ui.sample

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.seo4d696b75.compose.pager.sample.ui.sample.section.LoopPagerSection
import com.seo4d696b75.compose.pager.sample.ui.sample.section.PagerSection
import com.seo4d696b75.compose.pager.sample.ui.theme.MyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleScreen(
    modifier: Modifier = Modifier,
) {
    var loop by remember { mutableStateOf(true) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = loop,
                    onClick = { loop = true },
                    icon = { Icon(Icons.Outlined.Refresh, null) },
                    label = { Text("Loop") },
                )
                NavigationBarItem(
                    selected = !loop,
                    onClick = { loop = false },
                    icon = { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) },
                    label = { Text("Normal") },
                )
            }
        },
    ) {
        Crossfade(
            targetState = loop,
            label = "loop",
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) { isLoop ->
            if (isLoop) {
                LoopPagerSection()
            } else {
                PagerSection()
            }
        }
    }
}

@Composable
@Preview
private fun SampleScreenPreview() {
    MyTheme {
        SampleScreen()
    }
}
