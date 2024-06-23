package dev.sanmer.mrepo.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.sanmer.mrepo.R

enum class MainScreen(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    Repository(
        route = "RepositoryScreen",
        label = R.string.page_repository,
        icon = R.drawable.cloud
    ),

    Modules(
        route = "ModulesScreen",
        label = R.string.page_modules,
        icon = R.drawable.hexagons
    ),

    Settings(
        route = "SettingsScreen",
        label = R.string.page_settings,
        icon = R.drawable.settings
    )
}