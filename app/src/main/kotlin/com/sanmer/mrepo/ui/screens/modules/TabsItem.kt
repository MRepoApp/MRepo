package com.sanmer.mrepo.ui.screens.modules

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.component.AppBarContainerColor
import com.sanmer.mrepo.ui.component.Tab
import kotlinx.coroutines.launch

sealed class Pages(
    val id: Int,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    object Cloud : Pages(
        id = 0,
        label = R.string.modules_title_cloud,
        icon = R.drawable.cloud_connection_outline
    )
    object Installed : Pages(
        id = 1,
        label = R.string.modules_title_installed,
        icon = R.drawable.mobile_outline
    )
    object Updatable : Pages(
        id = 2,
        label = R.string.modules_title_updatable,
        icon = R.drawable.directbox_receive_outline
    )
}

val pages = listOf(
    Pages.Cloud,
    Pages.Installed,
    Pages.Updatable
)

@Composable
fun TabsItem(
    state: PagerState,
    userData: UserData,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) = AppBarContainerColor(scrollBehavior) { containerColor ->
    TabsItem(
        modifier = modifier,
        state = state,
        userData = userData,
        containerColor = containerColor
    )
}

@Composable
private fun TabsItem(
    state: PagerState,
    userData: UserData,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    TabRow(
        modifier = modifier,
        selectedTabIndex = state.currentPage,
        containerColor = containerColor,
        indicator = { tabPositions: List<TabPosition> ->
            AnimatedIndicator(
                tabPositions = tabPositions,
                selectedTabIndex = state.currentPage
            )
        },
        divider = {}
    ) {
        pages.forEachIndexed { id, page ->
            Tab(
                modifier = Modifier.padding(vertical = 10.dp),
                selected = state.currentPage == id,
                onClick = {
                    scope.launch {
                        state.animateScrollToPage(id)
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
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

@Composable
private fun Indicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.defaultMinSize(minHeight = 3.dp),
    ) {
        val width = size.width / 3

        drawLine(
            color = color,
            start = Offset(width, 0f),
            end = Offset(width * 2, 0f),
            strokeWidth = size.height,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun AnimatedIndicator(tabPositions: List<TabPosition>, selectedTabIndex: Int) {
    val transition = updateTransition(selectedTabIndex, label = "Indicator")
    val indicatorStart by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 50f)
            } else {
                spring(dampingRatio = 1f, stiffness = 1000f)
            }
        },
        label = "Indicator"
    ) {
        tabPositions[it].left
    }

    val indicatorEnd by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 1000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 50f)
            }
        },
        label = "Indicator"
    ) {
        tabPositions[it].right
    }

    Indicator(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorStart)
            .width(indicatorEnd - indicatorStart)
            .height(3.dp)
    )
}
