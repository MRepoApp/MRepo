package com.sanmer.mrepo.ui.navigation.normal

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.navigation.animated.RepositoryScreen
import com.sanmer.mrepo.ui.screens.repository.RepositoryScreen
import com.sanmer.mrepo.ui.screens.repository.viewmodule.ViewModuleScreen

fun NavGraphBuilder.repositoryScreen(
    navController: NavController
) = navigation(
    startDestination = RepositoryScreen.Home.route,
    route = MainScreen.Repository.route
) {
    composable(
        route = RepositoryScreen.Home.route
    ) {
        RepositoryScreen(
            navController = navController
        )
    }

    composable(
        route = RepositoryScreen.View.route,
        arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
    ) {
        ViewModuleScreen(
            navController = navController
        )
    }
}