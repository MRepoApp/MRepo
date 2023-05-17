package com.sanmer.mrepo.ui.navigation.normal

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.navigation.animated.ModulesScreen
import com.sanmer.mrepo.ui.screens.modules.ModulesScreen
import com.sanmer.mrepo.ui.screens.repository.viewmodule.ViewModuleScreen

fun NavGraphBuilder.modulesScreen(
    navController: NavController
) = navigation(
    startDestination = ModulesScreen.Home.route,
    route = MainScreen.Modules.route
) {
    composable(
        route = ModulesScreen.Home.route
    ) {
        ModulesScreen(
            navController = navController
        )
    }

    composable(
        route = ModulesScreen.View.route,
        arguments = listOf(navArgument("moduleId") { type = NavType.StringType }),
    ) {
        ViewModuleScreen(
            navController = navController
        )
    }
}