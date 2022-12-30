package com.sanmer.mrepo.ui.activity.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.sanmer.mrepo.ui.navigation.*
import com.sanmer.mrepo.ui.theme.AppTheme

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

@Preview(
    name = "Light Mode",
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode")
@Composable
fun MainPreview() {
    AppTheme {
        MainScreen()
    }
}