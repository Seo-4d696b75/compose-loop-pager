# Compose Loop Pager

Android library of a loop pager implemented with Jetpack Compose.

## Motivation

Now [accompanist-pager](https://github.com/google/accompanist/tree/main/pager) is deprecated, then `androidx.compose.foundation.pager` should be used instead. 
But conventional implementation of pseudo-infinite scrolling, such as setting `pageCount = { Int.MAX_VALUE }`, results in ANR as of 1.6.4 (BOM 2024.03.00).

## Features

[Horizontal/VerticalLoopPager](./lib/src/main/java/com/seo4d696b75/compose/pager/LoopPager.kt)

- ✅ Infinite scrolling (not pseudo implementation)
- ✅ Works properly without ANR
- ✅ Implemented without accompanist library
- ✅ Same user interaction and snap animation as `Horizontal/VerticalPager`
- ✅ Page position is savable, can be restored


<img src="https://github.com/Seo-4d696b75/compose-loop-pager/assets/25225028/d449651d-b3cb-4f47-a1d6-f71092c94dd9" width="320">

## Usage

```kotlin
    val items = remember { (0..4).toPersistentList() }
val state = rememberLoopPagerState(pageCount = items.size)

HorizontalLoopPager(
    state = state,
    aspectRatio = 1f,
    contentPadding = PaddingValues(horizontal = 48.dp),
    pageSpacing = 24.dp,
) { page ->
    val item = items[page]
    // page composable
}
```
