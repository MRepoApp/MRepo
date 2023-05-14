package com.sanmer.mrepo.ui.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.ui.animate.slideInLeftToRight
import com.sanmer.mrepo.ui.animate.slideInRightToLeft
import com.sanmer.mrepo.ui.animate.slideOutLeftToRight
import com.sanmer.mrepo.ui.animate.slideOutRightToLeft
import com.sanmer.mrepo.ui.navigation.MainGraph
import com.sanmer.mrepo.ui.screens.repository.RepositoryScreen
import com.sanmer.mrepo.ui.screens.repository.viewmodule.ViewModuleScreen

enum class RepoGraph(val route: String) {
    Repo("Repository"),
    View("ViewOnlineModule")
}

private val subScreens = listOf(
    RepoGraph.View.route
)

fun NavGraphBuilder.repoGraph(
    navController: NavController
) = navigation(
    startDestination = RepoGraph.Repo.route,
    route = MainGraph.Repo.route
) {
    composable(
        route = RepoGraph.Repo.route,
        enterTransition = {
            if (initialState.destination.route in subScreens) {
                slideInRightToLeft()
            } else {
                null
            }
        },
        exitTransition = {
            if (initialState.destination.route in subScreens) {
                slideOutLeftToRight()
            } else {
                null
            }
        }
    ) {
        RepositoryScreen(
            navController = navController
        )
    }

    composable(
        route = RepoGraph.View.route,
        enterTransition = { slideInLeftToRight() },
        exitTransition = { slideOutRightToLeft() }
    ) {
        ViewModuleScreen(
            navController = navController
        )
    }
}