package com.example.appreciclaje.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.appreciclaje.ui.components.BottomNavigationBar
import com.example.appreciclaje.ui.screens.LoginScreen
import com.example.appreciclaje.ui.screens.RegisterScreen
import com.example.appreciclaje.ui.screens.HomeScreen
import com.example.appreciclaje.ui.screens.ActivityScreen
import com.example.appreciclaje.ui.screens.QrScannerScreen
import com.example.appreciclaje.viewmodel.LoginViewModel
import com.example.appreciclaje.viewmodel.RegisterViewModel
import androidx.camera.core.ExperimentalGetImage

@ExperimentalGetImage
@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = when (currentRoute) {
        AppScreens.Login.route, AppScreens.Register.route -> false
        else -> true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppScreens.Login.route) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(AppScreens.Home.route) {
                            popUpTo(AppScreens.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(AppScreens.Register.route) }
                )
            }
            composable(AppScreens.Register.route) {
                RegisterScreen(
                    viewModel = registerViewModel,
                    onRegisterSuccess = {
                        navController.navigate(AppScreens.Login.route) {
                            popUpTo(AppScreens.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable(AppScreens.Home.route) {
                HomeScreen()
            }
            composable(AppScreens.Ranking.route) {
                // RankingScreen()
            }
            composable(AppScreens.QR.route) {

                QrScannerScreen()
            }
            composable(AppScreens.Exchange.route) {
                // ExchangeScreen()
            }
            composable(AppScreens.Activity.route) {
                ActivityScreen()
            }
        }
    }
}