package com.sanmer.mrepo.ui.activity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sanmer.mrepo.ui.animate.slideInBottomToTop
import com.sanmer.mrepo.ui.animate.slideOutTopToBottom
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.navigation.graphs.ModulesScreen
import com.sanmer.mrepo.ui.navigation.graphs.modulesScreen
import com.sanmer.mrepo.ui.navigation.graphs.repositoryScreen
import com.sanmer.mrepo.ui.navigation.graphs.settingsScreen
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.utils.navigatePopUpTo

@Composable
fun MainScreen() {
    val userPreferences = LocalUserPreferences.current
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showNav by remember(navBackStackEntry) {
        derivedStateOf {
            currentDestination?.route.toString() != ModulesScreen.Install.route
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showNav,
                enter = slideInBottomToTop(tween(100)),
                exit = slideOutTopToBottom(tween(100))
            ) {
                BottomNav(
                    navController = navController,
                    isRoot = userPreferences.isRoot
                )
            }
        }
    ) {
        NavHost(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
            navController = navController,
            startDestination = MainScreen.Repository.route
        ) {
            repositoryScreen(
                navController = navController
            )
            modulesScreen(
                navController = navController
            )
            settingsScreen(
                navController = navController
            )
        }
    }
}

@Composable
private fun BottomNav(
    navController: NavController,
    isRoot: Boolean
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val mainScreens by remember(isRoot) {
        derivedStateOf {
            if (isRoot) {
                listOf(MainScreen.Repository, MainScreen.Modules, MainScreen.Settings)
            } else {
                listOf(MainScreen.Repository, MainScreen.Settings)
            }
        }
    }

    NavigationBar(
        modifier = Modifier.imePadding()
    ) {
        mainScreens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = if (selected) {
                            screen.iconFilled
                        } else {
                            screen.icon
                        }),
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = screen.label),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                alwaysShowLabel = true,
                selected = selected,
                onClick = { if (!selected) navController.navigatePopUpTo(screen.route) }
            )
        }
    }
}