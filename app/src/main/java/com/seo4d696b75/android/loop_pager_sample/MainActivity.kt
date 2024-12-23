package com.seo4d696b75.android.loop_pager_sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.seo4d696b75.android.loop_pager_sample.ui.sample.SampleScreen
import com.seo4d696b75.android.loop_pager_sample.ui.theme.MyTheme

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
