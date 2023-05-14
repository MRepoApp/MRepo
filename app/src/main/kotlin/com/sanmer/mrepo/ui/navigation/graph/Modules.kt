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
import com.sanmer.mrepo.ui.screens.modules.ModulesScreen
import com.sanmer.mrepo.ui.screens.repository.viewmodule.ViewModuleScreen

enum class ModulesGraph(val route: String) {
    Modules("Modules"),
    View("ViewLocalModule")
}

private val subScreens = listOf(
    ModulesGraph.View.route
)

fun NavGraphBuilder.modulesGraph(
    navController: NavController
) = navigation(
    startDestination = ModulesGraph.Modules.route,
    route = MainGraph.Modules.route
) {
    composable(
        route = ModulesGraph.Modules.route,
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
        ModulesScreen(
            navController = navController
        )
    }

    composable(
        route = ModulesGraph.View.route,
        enterTransition = { slideInLeftToRight() },
        exitTransition = { slideOutRightToLeft() }
    ) {
        ViewModuleScreen(
            navController = navController
        )
    }
}