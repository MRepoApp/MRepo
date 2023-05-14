package com.sanmer.mrepo.ui.screens.modules

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.activity.install.InstallActivity
import com.sanmer.mrepo.ui.animate.slideInBottomToTop
import com.sanmer.mrepo.ui.animate.slideOutTopToBottom
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.component.SearchTopBar
import com.sanmer.mrepo.ui.navigation.navigateToRepo
import com.sanmer.mrepo.ui.utils.isScrollingUp
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun ModulesScreen(
    navController: NavController,
    viewModel: ModulesViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp = listState.isScrollingUp()
    val showFab by remember(isScrollingUp) {
        derivedStateOf {
            isScrollingUp && !viewModel.isSearch
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.progress,
        onRefresh = { viewModel.getLocalAll() }
    )

    BackHandler {
        if (viewModel.isSearch) {
            viewModel.closeSearch()
        } else {
            navController.navigateToRepo()
        }
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.closeSearch() }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                listState = listState
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
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .pullRefresh(
                state = pullRefreshState,
                enabled = !viewModel.isSearch
            )
        ) {
            if (viewModel.localValue.isEmpty()) {
                PageIndicator(
                    icon = R.drawable.command_outline,
                    text = if (viewModel.isSearch) R.string.search_empty else R.string.modules_empty,
                )
            }

            ModulesList(
                list = viewModel.localValue,
                state = listState
            )

            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                refreshing = viewModel.progress,
                state = pullRefreshState,
                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                contentColor = MaterialTheme.colorScheme.primary,
                scale = true
            )
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    listState: LazyListState,
    viewModel: ModulesViewModel = hiltViewModel()
) = if (viewModel.isSearch) {
    SearchTopBar(
        query = viewModel.key,
        onQueryChange = { viewModel.key = it },
        onClose = { viewModel.closeSearch() },
        scrollBehavior = scrollBehavior
    )
} else {
    NormalTopBar(
        scrollBehavior = scrollBehavior,
        listState = listState
    )
}

@Composable
private fun NormalTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    listState: LazyListState,
    viewModel: ModulesViewModel = hiltViewModel()
) = TopAppBar(
    title = {
        Text(text = stringResource(id = R.string.page_modules))
    },
    actions = {
        IconButton(
            onClick = { viewModel.isSearch = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_normal_outline),
                contentDescription = null
            )
        }

        val context = LocalContext.current
        IconButton(
            onClick = {
                Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sort_outline),
                contentDescription = null
            )
        }

        var expanded by remember { mutableStateOf(false) }
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )

            MenuItem(
                expanded = expanded,
                listState = listState,
                onClose = { expanded = false }
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun FloatingButton() {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        InstallActivity.start(context = context, uri = uri)
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                launcher.launch("application/zip")
            }
        }
    }

    ExtendedFloatingActionButton(
        interactionSource = interactionSource,
        onClick = {},
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary,
        icon = {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = R.drawable.add_outline),
                contentDescription = null
            )
        },
        text = {
            Text(text = stringResource(id = R.string.module_install))
        }
    )
}