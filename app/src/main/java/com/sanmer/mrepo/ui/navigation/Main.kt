package com.sanmer.mrepo.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.expansion.navigatePopUpTo

sealed class MainGraph(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int
    ) {
    object Home : MainGraph(
        route = "homeGraph",
        label = R.string.page_home,
        icon = R.drawable.home_outline,
        iconSelected = R.drawable.home_bold
    )
    object Modules : MainGraph(
        route = "modulesGraph",
        label = R.string.page_modules,
        icon = R.drawable.box_outline,
        iconSelected = R.drawable.box_bold
    )
    object Settings : MainGraph(
        route = "settingsGraph",
        label = R.string.page_settings,
        icon = R.drawable.setting_outline,
        iconSelected = R.drawable.setting_bold
    )
}

fun NavController.navigateToHome() = navigatePopUpTo(MainGraph.Home.route)
fun NavController.navigateToModules() = navigatePopUpTo(MainGraph.Modules.route)
fun NavController.navigateToSettings() = navigatePopUpTo(MainGraph.Settings.route)