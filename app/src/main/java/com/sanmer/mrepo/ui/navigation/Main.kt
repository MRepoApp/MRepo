package com.sanmer.mrepo.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
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
        icon = R.drawable.ic_home_outline,
        iconSelected = R.drawable.ic_home_bold
    )
    object Modules : MainGraph(
        route = "modulesGraph",
        label = R.string.page_modules,
        icon = R.drawable.ic_box_outline,
        iconSelected = R.drawable.ic_box_bold
    )
    object Settings : MainGraph(
        route = "settingsGraph",
        label = R.string.page_settings,
        icon = R.drawable.ic_setting_outline,
        iconSelected = R.drawable.ic_setting_bold
    )
}

private val mainGraph = listOf(
    MainGraph.Modules,
    MainGraph.Home,
    MainGraph.Settings
)

private val homeGraph = listOf(
    HomeGraph.Home.route
)

private val modulesGraph = listOf(
    ModulesGraph.Modules.route
)

private val settingsGraph = listOf(
    SettingsGraph.Settings.route,
    SettingsGraph.AppTheme.route
)

@Composable
fun BottomNavigation(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        mainGraph.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            val enable = when(screen) {
                is MainGraph.Home -> currentDestination?.route !in homeGraph
                is MainGraph.Modules -> currentDestination?.route !in modulesGraph
                is MainGraph.Settings -> currentDestination?.route !in settingsGraph
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = if (selected) {
                            screen.iconSelected
                        } else {
                            screen.icon
                        }),
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = screen.label),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                alwaysShowLabel = true,
                selected = selected,
                onClick = {
                    if (enable) {
                        navController.navigatePopUpTo(screen.route)
                    }
                }
            )
        }
    }
}
