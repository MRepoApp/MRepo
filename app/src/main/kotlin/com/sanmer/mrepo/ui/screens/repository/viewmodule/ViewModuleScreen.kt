package com.sanmer.mrepo.ui.screens.repository.viewmodule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.component.CollapsedTopAppBarDefaults
import com.sanmer.mrepo.ui.screens.repository.viewmodule.pages.AboutPage
import com.sanmer.mrepo.ui.screens.repository.viewmodule.pages.OverviewPage
import com.sanmer.mrepo.ui.screens.repository.viewmodule.pages.VersionsPage
import com.sanmer.mrepo.ui.utils.navigateBack
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.ModuleViewModel

@Composable
fun ViewModuleScreen(
    navController: NavController,
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())
    val scrollBehavior = CollapsedTopAppBarDefaults.scrollBehavior()
    val pagerState = rememberPagerState { pages.size }

    BackHandler { navController.navigateBack() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ViewModuleTopBar(
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
            ) {
                when (it) {
                    0 -> OverviewPage()
                    1 -> VersionsPage(isRoot = userData.isRoot)
                    2 -> AboutPage()
                }
            }
        }
    }
}