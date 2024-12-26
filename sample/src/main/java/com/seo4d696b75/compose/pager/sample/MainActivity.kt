package com.seo4d696b75.compose.pager.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.seo4d696b75.compose.pager.sample.ui.sample.SampleScreen
import com.seo4d696b75.compose.pager.sample.ui.theme.MyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                SampleScreen()
            }
        }
    }
}
