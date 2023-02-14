package com.sanmer.mrepo.ui.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.ui.animate.SlideIn
import com.sanmer.mrepo.ui.animate.SlideOut
import com.sanmer.mrepo.ui.navigation.MainGraph
import com.sanmer.mrepo.ui.screens.modules.ModulesScreen
import com.sanmer.mrepo.ui.screens.viewmodule.ViewModuleScreen

sealed class ModulesGraph(val route: String) {
    object Modules : ModulesGraph("modules")
    object View : ModulesGraph("view") {
        val way: String = "${route}/{id}"
        fun String.toRoute() = "${route}/${this}"
    }
}

fun NavGraphBuilder.modulesGraph(
    navController: NavController
) = navigation(
    startDestination = ModulesGraph.Modules.route,
    route = MainGraph.Modules.route
) {
    composable(
        route = ModulesGraph.Modules.route,
        enterTransition = {
            when (initialState.destination.route) {
                ModulesGraph.View.way -> SlideIn.rightToLeft
                else -> null
            }
        },
        exitTransition = {
            when (initialState.destination.route) {
                ModulesGraph.View.way -> SlideOut.leftToRight
                else -> null
            }
        }
    ) {
        ModulesScreen(
            navController = navController
        )
    }

    composable(
        route = ModulesGraph.View.way,
        arguments = listOf(navArgument("id") { type = NavType.StringType }),
        enterTransition = { SlideIn.leftToRight },
        exitTransition = { SlideOut.rightToLeft }
    ) {
        ViewModuleScreen(
            navController = navController
        )
    }
}