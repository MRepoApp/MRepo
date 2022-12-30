package com.sanmer.mrepo.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.ui.page.modules.ModulesScreen

sealed class ModulesGraph(val route: String) {
    object Modules : ModulesGraph("modules")
}

fun NavGraphBuilder.modulesGraph(
    navController: NavController
) {
    navigation(
        startDestination = ModulesGraph.Modules.route,
        route = MainGraph.Modules.route
    ) {
        composable(ModulesGraph.Modules.route) {
            ModulesScreen()
        }
    }
}