package com.sanmer.mrepo.ui.screens.repository.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.ui.component.CollapsingTopAppBarDefaults
import com.sanmer.mrepo.ui.screens.repository.view.pages.AboutPage
import com.sanmer.mrepo.ui.screens.repository.view.pages.OverviewPage
import com.sanmer.mrepo.ui.screens.repository.view.pages.VersionsPage
import com.sanmer.mrepo.ui.utils.navigateSingleTopTo
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.InstallViewModel
import com.sanmer.mrepo.viewmodel.ModuleViewModel
import kotlinx.coroutines.launch

@Composable
fun ViewScreen(
    navController: NavController,
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val scrollBehavior = CollapsingTopAppBarDefaults.scrollBehavior()
    val pagerState = rememberPagerState { if (viewModel.isEmptyAbout) 2 else 3 }

    var zipFile by remember { mutableStateOf(context.cacheDir) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        scope.launch {
            viewModel.saveZipFile(context, zipFile, uri)
        }
    }

    val download: (VersionItem, Boolean) -> Unit = { item, install ->
        viewModel.downloader(context, item) {
            zipFile = context.cacheDir.resolve(it)
            if (install) {
                navController.navigateSingleTopTo(
                    InstallViewModel.putPath(zipFile)
                )
            } else {
                launcher.launch(zipFile.nameWithoutExtension)
            }
        }
    }

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
                        item = viewModel.versions.firstOrNull()?.second,
                        local = viewModel.local,
                        localState = viewModel.localState,
                        onInstall = { download(it, true) },
                        setIgnoreUpdates = viewModel::setIgnoreUpdates
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