package com.example.appreciclaje.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.appreciclaje.network.SessionManager
import com.example.appreciclaje.ui.components.BottomNavigationBar
import com.example.appreciclaje.ui.screens.*
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

    val empresaRoutes = listOf(
        AppScreens.EmpresaHome.route,
        AppScreens.ValidarCanjes.route,
        AppScreens.AdministrarCanjes.route,
        AppScreens.EmpresaProfile.route
    )

    val showBottomBar = when (currentRoute) {
        AppScreens.Login.route, AppScreens.Register.route -> false
        in empresaRoutes -> false
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
                        val userRole = SessionManager.getRol()
                        val destination = if (userRole == "empresa") {
                            AppScreens.EmpresaHome.route
                        } else {
                            AppScreens.Home.route
                        }
                        navController.navigate(destination) {
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
                HomeScreen(
                    onLogout = {
                        navController.navigate(AppScreens.Login.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(AppScreens.EmpresaHome.route) {
                EmpresaHomeScreen(
                    onLogout = {
                        navController.navigate(AppScreens.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToValidar = { navController.navigate(AppScreens.ValidarCanjes.route) },
                    onNavigateToAdministrar = { navController.navigate(AppScreens.AdministrarCanjes.route) },
                    onNavigateToProfile = { navController.navigate(AppScreens.EmpresaProfile.route) }
                )
            }
            composable(AppScreens.ValidarCanjes.route) {
                // Placeholder Screen
                ValidarCanjesScreen(navController = navController)

            }
            composable(AppScreens.AdministrarCanjes.route) {
                // Placeholder Screen
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Administrar Canjes Screen")
                }
            }
            composable(AppScreens.EmpresaProfile.route) {
                // Placeholder Screen
                EmpresaProfileScreen(navController = navController)
            }
            composable(AppScreens.Ranking.route) {
                RankingHistoricoScreen()
            }
            composable(AppScreens.QR.route) {
                QrScannerScreen()
            }
            composable(AppScreens.Canjes.route) {
                CanjesScreen()
            }
            composable(AppScreens.Activity.route) {
                ActivityScreen()
            }
        }
    }
}