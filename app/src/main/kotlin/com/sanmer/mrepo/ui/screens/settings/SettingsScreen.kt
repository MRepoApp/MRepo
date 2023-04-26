package com.sanmer.mrepo.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.ui.activity.log.LogActivity
import com.sanmer.mrepo.ui.component.EditItemForSetting
import com.sanmer.mrepo.ui.component.MenuItemForSetting
import com.sanmer.mrepo.ui.component.NormalItemForSetting
import com.sanmer.mrepo.ui.component.TitleItemForSetting
import com.sanmer.mrepo.ui.navigation.graph.SettingsGraph
import com.sanmer.mrepo.ui.navigation.navigateToHome
import com.sanmer.mrepo.ui.utils.navigatePopUpTo
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.utils.expansion.openUrl
import com.sanmer.mrepo.viewmodel.HomeViewModel

@Composable
fun SettingsScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current

    BackHandler { navController.navigateToHome() }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsTopBar(
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            TitleItemForSetting(text = stringResource(id = R.string.settings_title_normal))

            NormalItemForSetting(
                iconRes = R.drawable.brush_outline,
                text = stringResource(id = R.string.settings_app_theme),
                subText = stringResource(id = R.string.settings_app_theme_desc),
                onClick = {
                    navController.navigatePopUpTo(SettingsGraph.AppTheme.route)
                }
            )

            NormalItemForSetting(
                iconRes = R.drawable.health_outline,
                text = stringResource(id = R.string.settings_log_viewer),
                subText = stringResource(id = R.string.settings_log_viewer_desc),
                onClick = {
                    LogActivity.start(context)
                }
            )

            TitleItemForSetting(text = stringResource(id = R.string.settings_title_app))

            EditItemForSetting(
                iconRes = R.drawable.cube_scan_outline,
                title = stringResource(id = R.string.settings_download_path),
                text = userData.downloadPath.absolutePath,
                supportingText = { Text(text = stringResource(id = R.string.dialog_empty_default)) },
                onChange = {
                    val value = it.ifEmpty { Const.DIR_PUBLIC_DOWNLOADS.absolutePath }
                    viewModel.setDownloadPath(value)
                }
            )

            NormalItemForSetting(
                iconRes = R.drawable.hierarchy_outline,
                text = stringResource(id = R.string.settings_repo),
                subText = stringResource(id = R.string.settings_repo_desc),
                onClick = {
                    navController.navigatePopUpTo(SettingsGraph.Repo.route)
                }
            )

            MenuItemForSetting(
                iconRes = R.drawable.main_component_outline,
                title = stringResource(id = R.string.settings_mode),
                items = mapOf(
                    WorkingMode.MODE_ROOT to stringResource(id = R.string.settings_mode_root),
                    WorkingMode.MODE_NON_ROOT to stringResource(id = R.string.settings_mode_non_root)
                ),
                selected = userData.workingMode,
                onChange = { value, _ ->
                    viewModel.setWorkingMode(value)
                }
            )

            TitleItemForSetting(text = stringResource(id = R.string.settings_title_others))

            NormalItemForSetting(
                iconRes = R.drawable.translate_outline,
                text = stringResource(id = R.string.settings_translate),
                subText = stringResource(id = R.string.settings_translate_desc),
                onClick = {
                    context.openUrl(Const.TRANSLATE_URL)
                }
            )

            NormalItemForSetting(
                iconRes = R.drawable.flag_outline,
                text = stringResource(id = R.string.settings_bug_tracker),
                subText = Const.ISSUES_URL,
                onClick = {
                    context.openUrl(Const.ISSUES_URL)
                }
            )

            NormalItemForSetting(
                iconRes = R.drawable.star_outline,
                text = stringResource(id = R.string.settings_follow_updates),
                subText = stringResource(id = R.string.settings_follow_updates_desc),
                onClick = {
                    context.openUrl(Const.TELEGRAM_CHANNEL_URL)
                }
            )
        }
    }
}

@Composable
private fun SettingsTopBar(
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(
            text = stringResource(id = R.string.page_settings),
            style = MaterialTheme.typography.titleLarge
        )
    },
    scrollBehavior = scrollBehavior
)