# compose-loop-pager

Sample of loop pager implementation in Jetpack Compose.

## Motivation

Now [accompanist-pager](https://github.com/google/accompanist/tree/main/pager) is deprecated, then `androidx.compose.foundation.pager` should be used instead. 
But conventional implementation of pseudo-infinite scrolling, such as setting `pageCount = { Int.MAX_VALUE }`, results in ANR as of 1.6.4 (Bom 2024.03.00).

## Implementation

- `LazyLayout` for infinite scrolling
- `AnchoredDraggableState` for user-interaction and animation like orizinal `HorizontalPager`

## Features

[HorizontalLoopPager](./app/src/main/java/com/seo4d696b75/android/loop_pager_sample/ui/pager/HorizontalLoopPager.kt)

- ✅ Infinite horizontal scrolling　 (not pseudo implementation!)
- ✅ Works properly without ANR
- ✅ Implemented without accompanist library
- ✅ Same user interaction and animation as `HorizontalPager`


<img src="https://github.com/Seo-4d696b75/compose-loop-pager/assets/25225028/992603df-93b3-4d29-bf8b-cf0f41d3f9ad" width="320">
