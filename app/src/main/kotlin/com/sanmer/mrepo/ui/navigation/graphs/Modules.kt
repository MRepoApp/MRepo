package com.sanmer.mrepo.ui.navigation.graphs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.screens.modules.ModulesScreen
import com.sanmer.mrepo.ui.screens.modules.install.InstallScreen

enum class ModulesScreen(val route: String) {
    Home("Modules"),
    Install("Install/{path}")
}

fun NavGraphBuilder.modulesScreen(
    navController: NavController
) = navigation(
    startDestination = ModulesScreen.Home.route,
    route = MainScreen.Modules.route
) {
    composable(
        route = ModulesScreen.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        ModulesScreen(
            navController = navController
        )
    }

    composable(
        route = ModulesScreen.Install.route,
        arguments = listOf(navArgument("path") { type = NavType.StringType }),
        enterTransition = { scaleIn() + fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        InstallScreen(
            navController = navController
        )
    }
}