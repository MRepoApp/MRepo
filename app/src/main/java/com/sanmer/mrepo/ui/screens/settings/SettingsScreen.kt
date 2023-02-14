package com.sanmer.mrepo.ui.screens.settings

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.Config.State
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.ui.activity.log.LogActivity
import com.sanmer.mrepo.ui.component.EditItemForSetting
import com.sanmer.mrepo.ui.component.NormalItemForSetting
import com.sanmer.mrepo.ui.component.SwitchItem
import com.sanmer.mrepo.ui.component.TitleItemForSetting
import com.sanmer.mrepo.ui.expansion.navigatePopUpTo
import com.sanmer.mrepo.ui.navigation.graph.SettingsGraph
import com.sanmer.mrepo.ui.navigation.navigateToHome
import com.sanmer.mrepo.works.Works

@Composable
fun SettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { navController.navigateToHome() }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsTopBar(
                scrollBehavior = scrollBehavior
            )
        }
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
                    val intent = Intent(context, LogActivity::class.java)
                    context.startActivity(intent)
                }
            )
            NormalItemForSetting(
                iconRes = R.drawable.translate_outline,
                text = stringResource(id = R.string.settings_translate),
                subText = stringResource(id = R.string.settings_translate_desc),
                enabled = BuildConfig.DEBUG,
                onClick = {

                }
            )

            TitleItemForSetting(text = stringResource(id = R.string.settings_title_app))
            EditItemForSetting(
                iconRes = R.drawable.cube_scan_outline,
                title = stringResource(id = R.string.settings_download_path),
                text = State.downloadPath,
                supportingText = { Text(text = stringResource(id = R.string.dialog_empty_default)) },
                onChange = {
                    State.downloadPath = it.ifEmpty { Const.DIR_PUBLIC_DOWNLOADS.absolutePath }
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

            SwitchItem(
                iconRes = R.drawable.timer_outline,
                text = stringResource(id = R.string.settings_check_modules_updates),
                subText = stringResource(id = R.string.settings_check_modules_updates_desc),
                checked = State.isChackModulesUpdate,
                onChange = {
                    State.isChackModulesUpdate = it
                    if (State.isChackModulesUpdate) {
                        Works.setPeriodTasks()
                    } else {
                        Works.cancelPeriodTasks()
                    }
                }
            )

            var period by remember { mutableStateOf(false) }
            if (period) TasksPeriodDialog { period = false }
            AnimatedVisibility(
                visible = State.isChackModulesUpdate,
                enter = fadeIn(tween(400)) + expandVertically(tween(400)),
                exit = fadeOut(tween(400)) + shrinkVertically(tween(400))
            ) {
                val count = Config.tasksPeriodCount
                val unit = getMTimeUnits(Config.tasksPeriodUnit).label
                NormalItemForSetting(
                    iconRes = R.drawable.calendar_edit_outline,
                    text = stringResource(id = R.string.settings_tasks_period),
                    subText = stringResource(id = R.string.settings_tasks_period_desc,
                        count, stringResource(id = unit)),
                    onClick = { period = true }
                )
            }
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