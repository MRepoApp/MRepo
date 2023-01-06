package com.sanmer.mrepo.ui.activity.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.sanmer.mrepo.ui.navigation.*

@Composable
fun MainScreen() {
    val navController = rememberAnimatedNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation(
                navController = navController
            )
        },
    ) {
        AnimatedNavHost(
            modifier = Modifier
                .padding(bottom = it.calculateBottomPadding()),
            navController = navController,
            startDestination = MainGraph.Home.route
        ) {
            homeGraph(
                navController = navController
            )
            modulesGraph(
                navController = navController
            )
            settingsGraph(
                navController = navController
            )
        }
    }
}
