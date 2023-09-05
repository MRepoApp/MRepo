package com.sanmer.mrepo.ui.activity.install

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.State
import com.sanmer.mrepo.ui.component.NavigateUpTopBar
import com.sanmer.mrepo.ui.utils.isScrollingUp
import com.sanmer.mrepo.utils.ModuleUtils
import com.sanmer.mrepo.viewmodel.InstallViewModel

@Composable
fun InstallScreen(
    viewModel: InstallViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val isScrollingUp = listState.isScrollingUp()
    val showFab by remember(isScrollingUp) {
        derivedStateOf {
            isScrollingUp && viewModel.state.isSucceeded
        }
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    BackHandler(
        enabled = viewModel.state.isLoading,
        onBack = {}
    )

    Scaffold(
        modifier = Modifier
            .onKeyEvent {
                when (it.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_VOLUME_UP,
                    KeyEvent.KEYCODE_VOLUME_DOWN -> viewModel.state.isLoading
                    else -> false
                }
            }
            .focusRequester(focusRequester)
            .focusable()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                state = viewModel.state,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                enter = scaleIn(
                    animationSpec = tween(100),
                    initialScale = 0.8f
                ),
                exit = scaleOut(
                    animationSpec = tween(100),
                    targetScale = 0.8f
                )
            ) {
                FloatingButton()
            }
        }
    ) {
        Console(
            list = viewModel.console.asReversed(),
            state = listState,
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun Console(
    list: List<String>,
    state: LazyListState,
    modifier: Modifier = Modifier,
) = LazyColumn(
    state = state,
    modifier = modifier,
    reverseLayout = true
) {
    items(list) {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 5.dp)
        )
    }
}

@Composable
private fun TopBar(
    state: State,
    scrollBehavior: TopAppBarScrollBehavior
) = NavigateUpTopBar(
    title = stringResource(id = R.string.install_title),
    subtitle = stringResource(id = when (state.event) {
        Event.LOADING -> R.string.install_flashing
        Event.FAILED -> R.string.install_failure
        else -> R.string.install_done
    }),
    scrollBehavior = scrollBehavior,
    enable = state.isFinished,
    actions = {
        if (state.isFinished) {
            IconButton(
                onClick = {},
                enabled = false
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send_outline),
                    contentDescription = null
                )
            }
        }
    }
)

@Composable
private fun FloatingButton() = ExtendedFloatingActionButton(
    onClick = { ModuleUtils.reboot() },
    text = { Text(text = stringResource(id = R.string.settings_menu_reboot)) },
    icon = {
        Icon(
            painter = painterResource(id = R.drawable.refresh_outline),
            contentDescription = null
        )
    }
)
