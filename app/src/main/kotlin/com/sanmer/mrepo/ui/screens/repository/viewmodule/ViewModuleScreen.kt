package com.sanmer.mrepo.ui.screens.repository.viewmodule

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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.ui.component.CollapsingTopAppBarDefaults
import com.sanmer.mrepo.ui.providable.LocalSuState
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.screens.repository.viewmodule.pages.AboutPage
import com.sanmer.mrepo.ui.screens.repository.viewmodule.pages.OverviewPage
import com.sanmer.mrepo.ui.screens.repository.viewmodule.pages.VersionsPage
import com.sanmer.mrepo.ui.utils.navigateSingleTopTo
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.InstallViewModel
import com.sanmer.mrepo.viewmodel.ModuleViewModel

@Composable
fun ViewModuleScreen(
    navController: NavController,
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userPreferences = LocalUserPreferences.current
    val suState = LocalSuState.current

    val localState = viewModel.rememberLocalState(suState = suState)

    val scrollBehavior = CollapsingTopAppBarDefaults.scrollBehavior()
    val pagerState = rememberPagerState { if (viewModel.isEmptyAbout) 2 else 3 }

    val download: (VersionItem, Boolean) -> Unit = { item, install ->
        viewModel.downloader(context, item) {
            if (install) {
                val path = userPreferences.downloadPath.resolve(it)
                navController.navigateSingleTopTo(
                    InstallViewModel.putPath(path)
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ViewModuleTopBar(
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
            ViewModuleTab(
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
                        item = viewModel.versions.firstOrNull()?.second,
                        local = viewModel.local,
                        localState = localState,
                        onInstall = { download(it, true) }
                    )
                    1 -> VersionsPage(
                        versions = viewModel.versions,
                        localVersionCode = viewModel.localVersionCode,
                        getProgress = { viewModel.rememberProgress(it) },
                        onDownload = download
                    )
                    2 -> AboutPage(
                        online = viewModel.online
                    )
                }
            }
        }
    }
}