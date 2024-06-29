package dev.sanmer.mrepo.ui.activity

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
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
import dev.sanmer.mrepo.datastore.model.Homepage
import dev.sanmer.mrepo.datastore.model.WorkingMode.Companion.isRoot
import dev.sanmer.mrepo.ui.navigation.MainScreen
import dev.sanmer.mrepo.ui.navigation.graphs.modulesScreen
import dev.sanmer.mrepo.ui.navigation.graphs.repositoryScreen
import dev.sanmer.mrepo.ui.navigation.graphs.settingsScreen
import dev.sanmer.mrepo.ui.providable.LocalUserPreferences
import dev.sanmer.mrepo.ui.utils.navigatePopUpTo

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val userPreferences = LocalUserPreferences.current
    val startDestination by remember {
        derivedStateOf {
            when (userPreferences.currentHomepage) {
                Homepage.Repository -> MainScreen.Repository.route
                Homepage.Modules -> MainScreen.Modules.route
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNav(
                navController = navController,
                isRoot = userPreferences.workingMode.isRoot
            )
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) {
        NavHost(
            modifier = Modifier.padding(it),
            navController = navController,
            startDestination = startDestination
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
                        painter = painterResource(id = screen.icon),
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