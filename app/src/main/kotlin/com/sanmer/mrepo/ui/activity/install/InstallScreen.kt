package com.sanmer.mrepo.ui.activity.install

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.sanmer.mrepo.ui.utils.NavigateUpTopBar
import com.sanmer.mrepo.ui.utils.fabPadding
import com.sanmer.mrepo.utils.SvcPower
import com.sanmer.mrepo.viewmodel.InstallViewModel

@Composable
fun InstallScreen(
    viewModel: InstallViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val focusRequester = remember { FocusRequester() }
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
            InstallTopBar(
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (viewModel.state.isSucceeded) {
                RebootButton()
            }
        },
    ) {
        ConsoleList(
            list = viewModel.console,
            contentPadding = it
        )
    }
}

@Composable
private fun InstallTopBar(
    viewModel: InstallViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior
) = NavigateUpTopBar(
    title = R.string.install_title,
    subtitle = when (viewModel.state.event) {
        Event.LOADING -> R.string.install_flashing
        Event.FAILED -> R.string.install_failure
        else -> R.string.install_done
    },
    scrollBehavior = scrollBehavior,
    enable = viewModel.state.isSucceeded,
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
private fun RebootButton() = ExtendedFloatingActionButton(
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

@Composable
private fun ConsoleList(
    contentPadding: PaddingValues,
    list: SnapshotStateList<String>
) {
    val state = rememberLazyListState()
    LaunchedEffect(list.toList()) {
        state.animateScrollToItem(list.size)
    }

    LazyColumn(
        state = state,
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(contentPadding),
        contentPadding = fabPadding(2.dp),
    ) {
        items(list) {
            ConsoleItem(text = it)
        }
    }
}

@Composable
private fun ConsoleItem(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}
