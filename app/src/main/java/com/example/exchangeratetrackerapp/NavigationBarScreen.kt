package com.example.exchangeratetrackerapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.exchangeratetrackerapp.features.home.presentation.HomeScreen

@Composable
fun NavigationBarScreen() {
    val rootNavController = rememberNavController()
    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = rootNavController,
            startDestination = items[0].screen.route,
            modifier = Modifier.padding(bottom = 80.dp)
        ) {
            items.forEach { item ->
                composable(item.screen.route) {
                    when (item.screen) {
                        Screen.HOME -> HomeScreen()
                        else -> MockScreen(text = item.screen.title)
                    }
                }
            }
        }
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            items.forEach { item ->
                val isSelected = item.screen.route == navBackStackEntry?.destination?.route
                NavigationBarItem(
                    selected = isSelected,
                    label = {
                        Text(text = item.screen.title)
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.screen.title
                        )
                    },
                    onClick = {
                        rootNavController.navigate(item.screen.route) {
                            popUpTo(rootNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

private enum class Screen(
    val route: String,
    val title: String
) {
    HOME("/home", "Home"),
    FAVORITES("/favorites", "Favorites"),
    MARKETS("/markets", "Markets"),
    SETTINGS("/settings", "Settings")
}

private data class BottomNavigationItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val items = listOf(
    BottomNavigationItem(
        screen = Screen.HOME,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavigationItem(
        screen = Screen.FAVORITES,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.Favorite
    ),
    BottomNavigationItem(
        screen = Screen.MARKETS,
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.Email
    ),
    BottomNavigationItem(
        screen = Screen.SETTINGS,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

@Composable
private fun MockScreen(
    text: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text)
    }
}