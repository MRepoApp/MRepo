package com.sanmer.mrepo.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sanmer.mrepo.R

enum class MainScreen(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int
) {
    Repository(
        route = "RepositoryScreen",
        label = R.string.page_repository,
        icon = R.drawable.box_outline,
        iconSelected = R.drawable.box_bold
    ),

    Modules(
        route = "ModulesScreen",
        label = R.string.page_modules,
        icon = R.drawable.command_outline,
        iconSelected = R.drawable.command_bold
    ),

    Settings(
        route = "SettingsScreen",
        label = R.string.page_settings,
        icon = R.drawable.setting_outline,
        iconSelected = R.drawable.setting_bold
    )
}