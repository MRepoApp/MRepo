package dev.sanmer.mrepo.ui.screens.repository.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.mrepo.ui.component.CollapsingTopAppBarDefaults
import dev.sanmer.mrepo.ui.screens.repository.view.pages.AboutPage
import dev.sanmer.mrepo.ui.screens.repository.view.pages.OverviewPage
import dev.sanmer.mrepo.ui.screens.repository.view.pages.VersionsPage
import dev.sanmer.mrepo.ui.utils.none
import dev.sanmer.mrepo.viewmodel.ModuleViewModel

@Composable
fun ViewScreen(
    navController: NavController,
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val scrollBehavior = CollapsingTopAppBarDefaults.scrollBehavior()
    val pagerState = rememberPagerState { if (viewModel.isEmptyAbout) 2 else 3 }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ViewTopBar(
                online = viewModel.online,
                tracks = viewModel.tracks,
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ViewTab(
                state = pagerState,
                updatableSize = viewModel.updatableSize,
                hasAbout = !viewModel.isEmptyAbout
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> OverviewPage(
                        online = viewModel.online,
                        item = viewModel.lastVersionItem,
                        local = viewModel.local,
                        notifyUpdates = viewModel.notifyUpdates,
                        isProviderAlive = viewModel.isProviderAlive,
                        setUpdatesTag = viewModel::setUpdatesTag,
                        onInstall = { context, item ->
                            viewModel.downloader(context, item, true)
                        }
                    )
                    1 -> VersionsPage(
                        versions = viewModel.versions,
                        localVersionCode = viewModel.localVersionCode,
                        isProviderAlive = viewModel.isProviderAlive,
                        getProgress = viewModel::getProgress,
                        onDownload = viewModel::downloader
                    )
                    2 -> AboutPage(
                        online = viewModel.online
                    )
                }
            }
        }
    }
}