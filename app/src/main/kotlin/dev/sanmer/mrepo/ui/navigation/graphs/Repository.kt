package dev.sanmer.mrepo.ui.navigation.graphs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import dev.sanmer.mrepo.ui.navigation.MainScreen
import dev.sanmer.mrepo.ui.screens.repository.RepositoryScreen
import dev.sanmer.mrepo.ui.screens.repository.view.ViewScreen

enum class RepositoryScreen(val route: String) {
    Home("Repository"),
    View("View/{moduleId}")
}

fun NavGraphBuilder.repositoryScreen(
    navController: NavController
) = navigation(
    startDestination = RepositoryScreen.Home.route,
    route = MainScreen.Repository.route
) {
    composable(
        route = RepositoryScreen.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        RepositoryScreen(
            navController = navController
        )
    }

    composable(
        route = RepositoryScreen.View.route,
        arguments = listOf(navArgument("moduleId") { type = NavType.StringType }),
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        ViewScreen(
            navController = navController
        )
    }
}