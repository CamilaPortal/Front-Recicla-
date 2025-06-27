package com.example.appreciclaje.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.appreciclaje.core.navigation.AppScreens

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavItem("Inicio", Icons.Default.Home, AppScreens.Home),
        NavItem("Ranking", Icons.Default.Leaderboard, AppScreens.Ranking),
        NavItem("QR", Icons.Default.QrCode, AppScreens.QR),
        NavItem("Canjes", Icons.Default.CardGiftcard, AppScreens.Canjes),
        NavItem("Actividad", Icons.Default.History, AppScreens.Activity)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF4CAE50)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route.route,
                onClick = {
                    if (currentRoute != item.route.route) {
                        navController.navigate(item.route.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color(0xFFE0E0E0),
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color(0xFFE0E0E0),
                    indicatorColor = Color(0xFF339136)
                )
            )
        }
    }
}

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val route: AppScreens
)