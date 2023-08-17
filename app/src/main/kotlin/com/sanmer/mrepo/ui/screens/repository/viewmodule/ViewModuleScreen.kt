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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanmer.mrepo.ui.component.CollapsingTopAppBarDefaults
import com.sanmer.mrepo.ui.providable.LocalSuState
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.screens.repository.viewmodule.pages.AboutPage
import com.sanmer.mrepo.ui.screens.repository.viewmodule.pages.OverviewPage
import com.sanmer.mrepo.ui.screens.repository.viewmodule.pages.VersionsPage
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.ModuleViewModel

@Composable
fun ViewModuleScreen(
    navController: NavController,
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val userPreferences = LocalUserPreferences.current
    val suState = LocalSuState.current

    val localState = viewModel.rememberLocalState(suState = suState)
    val (versions, tracks) = viewModel.getVersionsAndTracks()

    val scrollBehavior = CollapsingTopAppBarDefaults.scrollBehavior()
    val pagerState = rememberPagerState { pages.size }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ViewModuleTopBar(
                online = viewModel.online,
                tracks = tracks,
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ViewModuleTab(state = pagerState)

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> OverviewPage(
                        online = viewModel.online,
                        item = versions.firstOrNull()?.second,
                        local = viewModel.local,
                        localState = localState,
                        downloader = viewModel::downloader
                    )
                    1 -> VersionsPage(
                        versions = versions,
                        isRoot = userPreferences.isRoot,
                        getProgress = { viewModel.rememberProgress(it) },
                        downloader = viewModel::downloader
                    )
                    2 -> AboutPage(
                        online = viewModel.online,
                        isEmpty = viewModel.isEmptyAbout
                    )
                }
            }
        }
    }
}