package com.sanmer.mrepo.ui.screens.modules

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.component.AppBarContainerColor
import com.sanmer.mrepo.ui.component.Segment
import com.sanmer.mrepo.ui.component.SegmentedButtons
import kotlinx.coroutines.launch

@Composable
fun SegmentedButtonsItem(
    state: PagerState,
    userData: UserData,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) = AppBarContainerColor(scrollBehavior) { containerColor ->
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor
    ) {
        SegmentedButtonsItem(
            modifier = Modifier
                .padding(bottom = 10.dp, start = 20.dp, end = 20.dp),
            state = state,
            userData = userData
        )
    }
}

@Composable
private fun SegmentedButtonsItem(
    state: PagerState,
    userData: UserData,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    SegmentedButtons(
        modifier = modifier
    ) {
        pages.forEachIndexed { id, page ->
            Segment(
                selected = state.currentPage == id,
                onClick = {
                    scope.launch {
                        state.animateScrollToPage(id)
                    }
                },
                enabled = if (page !is Pages.Cloud) userData.isRoot else true
            ) {
                Text(
                    text = stringResource(id = page.label),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}