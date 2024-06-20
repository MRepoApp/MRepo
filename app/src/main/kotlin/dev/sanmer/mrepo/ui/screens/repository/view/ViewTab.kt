package dev.sanmer.mrepo.ui.screens.repository.view

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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.ui.component.Tab
import kotlinx.coroutines.launch

@Composable
internal fun ViewTab(
    state: PagerState,
    updatableSize: Int,
    hasAbout: Boolean,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val pages by remember(hasAbout) {
        derivedStateOf {
            when {
                hasAbout -> listOf(
                    R.string.view_module_page_overview,
                    R.string.view_module_page_versions,
                    R.string.view_module_page_about
                )
                else -> listOf(
                    R.string.view_module_page_overview,
                    R.string.view_module_page_versions
                )
            }
        }
    }

    TabRow(
        modifier = modifier,
        selectedTabIndex = state.currentPage,
        indicator = { tabPositions: List<TabPosition> ->
            AnimatedIndicator(
                tabPositions = tabPositions,
                selectedTabIndex = state.currentPage
            )
        },
        divider = {
            HorizontalDivider(
                thickness = 0.3.dp,
                modifier = Modifier.shadow(6.dp)
            )
        }
    ) {
        pages.forEachIndexed { index, text ->
            Tab(
                modifier = Modifier.padding(vertical = 12.dp),
                selected = state.currentPage == index,
                onClick = {
                    scope.launch {
                        state.animateScrollToPage(index)
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                BadgedBox(
                    badge = {
                        if (index == 1 && updatableSize != 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error
                            ) {
                                Text(
                                    text = updatableSize.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = text),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
        val width = size.width / 4

        drawLine(
            color = color,
            start = Offset(width, size.height),
            end = Offset(width * 3, size.height),
            strokeWidth = size.height * 2,
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