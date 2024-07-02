package dev.sanmer.mrepo.ui.activity

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.ui.component.NavigateUpTopBar
import dev.sanmer.mrepo.ui.utils.isScrollingUp
import dev.sanmer.mrepo.viewmodel.InstallViewModel
import dev.sanmer.mrepo.viewmodel.InstallViewModel.Event
import dev.sanmer.mrepo.viewmodel.InstallViewModel.Event.Companion.isFinished
import dev.sanmer.mrepo.viewmodel.InstallViewModel.Event.Companion.isInstalling
import dev.sanmer.mrepo.viewmodel.InstallViewModel.Event.Companion.isSucceeded
import kotlinx.coroutines.launch

@Composable
fun InstallScreen(
    viewModel: InstallViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()
    val showFab by remember {
        derivedStateOf {
            isScrollingUp && viewModel.event.isSucceeded
        }
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    BackHandler(
        enabled = viewModel.event.isInstalling,
        onBack = {}
    )

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        scope.launch {
            viewModel.writeLogsTo(context, uri)
                .onSuccess {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.install_logs_saved)
                    )
                }.onFailure {
                    val message = it.message ?: context.getString(R.string.unknown_error)
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.install_logs_save_failed, message)
                    )
                }
        }
    }

    Scaffold(
        modifier = Modifier
            .onKeyEvent {
                when (it.key) {
                    Key.VolumeUp,
                    Key.VolumeDown -> viewModel.event.isInstalling

                    else -> false
                }
            }
            .focusRequester(focusRequester)
            .focusable()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                exportLog = { launcher.launch(viewModel.logfile) },
                event = viewModel.event,
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
                FloatingButton(
                    reboot = viewModel::reboot
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Console(
            list = viewModel.console,
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
) {
    LazyColumn(
        state = state,
        modifier = modifier
    ) {
        items(list) {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }

    LaunchedEffect(list.size) {
        state.scrollToItem(list.size)
    }
}

@Composable
private fun TopBar(
    exportLog: () -> Unit,
    event: Event,
    scrollBehavior: TopAppBarScrollBehavior
) = NavigateUpTopBar(
    title = stringResource(id = R.string.install_screen_title),
    subtitle = stringResource(id = when (event) {
        Event.Installing -> R.string.install_flashing
        Event.Failed -> R.string.install_failure
        else -> R.string.install_done
    }),
    scrollBehavior = scrollBehavior,
    enable = event.isFinished,
    actions = {
        if (event.isFinished) {
            IconButton(
                onClick = exportLog
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.device_floppy),
                    contentDescription = null
                )
            }
        }
    }
)

@Composable
private fun FloatingButton(
    reboot: () -> Unit,
) = FloatingActionButton(
    onClick = reboot
) {
    Icon(
        painter = painterResource(id = R.drawable.reload),
        contentDescription = null
    )
}
