package com.sanmer.mrepo.ui.screens.modules

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.utils.MediaStoreUtils
import com.sanmer.mrepo.datastore.modules.ModulesMenuExt
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.ui.component.Loading
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.component.SearchTopBar
import com.sanmer.mrepo.ui.component.TopAppBarTitle
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.utils.isScrollingUp
import com.sanmer.mrepo.ui.utils.navigateSingleTopTo
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.InstallViewModel
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun ModulesScreen(
    navController: NavController,
    viewModel: ModulesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userPreferences = LocalUserPreferences.current

    val list by viewModel.local.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val isScrollingUp = listState.isScrollingUp()
    val showFab by remember {
        derivedStateOf {
            isScrollingUp && !viewModel.isSearch && viewModel.isProviderAlive
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.getLocalAll() }
    )

    val download: (String, VersionItem, Boolean) -> Unit = { prefix, item, install ->
        viewModel.setFilePrefix(prefix)
        viewModel.downloader(context, item) {
            val zipFile = userPreferences.downloadPath.resolve(it)
            if (install) {
                navController.navigateSingleTopTo(
                    InstallViewModel.putPath(zipFile)
                )
            }
        }
    }

    BackHandler(
        enabled = viewModel.isSearch,
        onBack = viewModel::closeSearch
    )

    DisposableEffect(viewModel) {
        onDispose(viewModel::closeSearch)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                isSearch = viewModel.isSearch,
                onQueryChange = viewModel::search,
                onOpenSearch = viewModel::openSearch,
                onCloseSearch = viewModel::closeSearch,
                setMenu = viewModel::setModulesMenu,
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
                    navController = navController
                )
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
            if (viewModel.isLoading) {
                Loading()
            }

            if (list.isEmpty() && !viewModel.isLoading) {
                PageIndicator(
                    icon = R.drawable.keyframes,
                    text = if (viewModel.isSearch) R.string.search_empty else R.string.modules_empty,
                )
            }

            ModulesList(
                list = list,
                state = listState,
                isProviderAlive = viewModel.isProviderAlive,
                getUiState = { viewModel.rememberUiState(it) },
                getUpdateJson = { viewModel.rememberUpdateJson(it) },
                getProgress = { viewModel.rememberProgress(it) },
                onDownload = download
            )

            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                refreshing = viewModel.isRefreshing,
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
    isSearch: Boolean,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    setMenu: (ModulesMenuExt) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var query by remember { mutableStateOf("") }
    DisposableEffect(isSearch) {
        onDispose { query = "" }
    }

    SearchTopBar(
        isSearch = isSearch,
        query = query,
        onQueryChange = {
            onQueryChange(it)
            query = it
        },
        onClose = {
            onCloseSearch()
            query = ""
        },
        title = { TopAppBarTitle(text = stringResource(id = R.string.page_modules)) },
        scrollBehavior = scrollBehavior,
        actions = {
            if (!isSearch) {
                IconButton(
                    onClick = onOpenSearch
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = null
                    )
                }
            }

            ModulesMenu(
                setMenu = setMenu
            )
        }
    )
}

@Composable
private fun FloatingButton(
    navController: NavController
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        var path = MediaStoreUtils.getAbsoluteFileForUri(context, uri)
        if (path.startsWith("/mnt/appfuse")) {
            path = MediaStoreUtils.copyToTmp(context, uri)
        }

        navController.navigateSingleTopTo(InstallViewModel.putPath(path))
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                launcher.launch("application/zip")
            }
        }
    }

    FloatingActionButton(
        interactionSource = interactionSource,
        onClick = {},
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.package_import),
            contentDescription = null
        )
    }
}