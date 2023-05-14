package com.sanmer.mrepo.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.utils.navigatePopUpTo

enum class MainGraph(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int
) {
    Repo(
        route = "RepoGraph",
        label = R.string.page_repository,
        icon = R.drawable.box_outline,
        iconSelected = R.drawable.box_bold
    ),

    Modules(
        route = "ModulesGraph",
        label = R.string.page_modules,
        icon = R.drawable.command_outline,
        iconSelected = R.drawable.command_bold
    ),

    Settings(
        route = "SettingsGraph",
        label = R.string.page_settings,
        icon = R.drawable.setting_outline,
        iconSelected = R.drawable.setting_bold
    )
}

val mainGraphs = listOf(
    MainGraph.Repo,
    MainGraph.Modules,
    MainGraph.Settings
)

fun NavController.navigateToRepo() = navigatePopUpTo(MainGraph.Repo.route)
fun NavController.navigateToModules() = navigatePopUpTo(MainGraph.Modules.route)
fun NavController.navigateToSettings() = navigatePopUpTo(MainGraph.Settings.route)