package com.sanmer.mrepo.ui.utils

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.ui.component.ScrollbarState

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun LazyListState.scrollbarState(
    itemsAvailable: Int = layoutInfo.totalItemsCount,
    itemIndex: (LazyListItemInfo) -> Int = LazyListItemInfo::index,
): ScrollbarState = scrollbarState(
    itemsAvailable = itemsAvailable,
    visibleItems = { layoutInfo.visibleItemsInfo },
    firstVisibleItemIndex = { visibleItems ->
        interpolateFirstItemIndex(
            visibleItems = visibleItems,
            itemSize = { it.size },
            offset = { it.offset },
            nextItemOnMainAxis = { first -> visibleItems.find { it != first } },
            itemIndex = itemIndex,
        )
    },
    itemPercentVisible = itemPercentVisible@{ itemInfo ->
        itemVisibilityPercentage(
            itemSize = itemInfo.size,
            itemStartOffset = itemInfo.offset,
            viewportStartOffset = layoutInfo.viewportStartOffset,
            viewportEndOffset = layoutInfo.viewportEndOffset,
        )
    },
    reverseLayout = { layoutInfo.reverseLayout },
)

@Composable
fun LazyListState.rememberFastScroller(
    itemsAvailable: Int = layoutInfo.totalItemsCount,
): (Float) -> Unit = rememberFastScroller(
    itemsAvailable = itemsAvailable,
    scroll = ::scrollToItem,
)

@Composable
private inline fun rememberFastScroller(
    itemsAvailable: Int,
    crossinline scroll: suspend (index: Int) -> Unit,
): (Float) -> Unit {
    var percentage by remember { mutableFloatStateOf(Float.NaN) }
    val itemCount by rememberUpdatedState(itemsAvailable)

    LaunchedEffect(percentage) {
        if (percentage.isNaN()) return@LaunchedEffect
        val indexToFind = (itemCount * percentage).toInt()
        scroll(indexToFind)
    }
    return remember {
        { newPercentage -> percentage = newPercentage }
    }
}