package com.sanmer.mrepo.ui.activity.install

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.ui.animate.slideInBottomToTop
import com.sanmer.mrepo.ui.animate.slideOutTopToBottom
import com.sanmer.mrepo.ui.component.NavigateUpTopBar
import com.sanmer.mrepo.ui.utils.isScrollingUp
import com.sanmer.mrepo.utils.SvcPower
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

    LaunchedEffect(viewModel.console.toList()) {
        listState.scrollToItem(viewModel.console.size)
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
                    KeyEvent.KEYCODE_VOLUME_UP -> viewModel.state.isLoading
                    KeyEvent.KEYCODE_VOLUME_DOWN -> viewModel.state.isLoading
                    else -> false
                }
            }
            .focusRequester(focusRequester)
            .focusable()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                enter = fadeIn() + slideInBottomToTop(),
                exit = fadeOut() + slideOutTopToBottom()
            ) {
                FloatingButton()
            }
        }
    ) {
        Console(
            list = viewModel.console,
            contentPadding = it,
            state = listState
        )
    }
}

@Composable
private fun Console(
    list: SnapshotStateList<String>,
    contentPadding: PaddingValues,
    state: LazyListState
) = LazyColumn(
    state = state,
    modifier = Modifier
        .horizontalScroll(rememberScrollState())
        .fillMaxWidth()
        .padding(contentPadding),
    contentPadding = PaddingValues(all = 5.dp)
) {
    items(list) {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: InstallViewModel = hiltViewModel()
) = NavigateUpTopBar(
    title = R.string.install_title,
    subtitle = when (viewModel.state.event) {
        Event.LOADING -> R.string.install_flashing
        Event.FAILED -> R.string.install_failure
        else -> R.string.install_done
    },
    scrollBehavior = scrollBehavior,
    enable = viewModel.state.isFinished,
    actions = {
        val context = LocalContext.current
        if (viewModel.state.isFinished) {
            IconButton(
                onClick = { viewModel.sendLogFile(context) }
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
    onClick = {
        SvcPower.reboot()
    },
    text = {
        Text(text = stringResource(id = R.string.install_reboot))
    },
    icon = {
        Icon(
            painter = painterResource(id = R.drawable.refresh_outline),
            contentDescription = null
        )
    }
)
