package com.example.appreciclaje.core.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.appreciclaje.ui.screens.LoginScreen
import com.example.appreciclaje.ui.screens.RegisterScreen
import com.example.appreciclaje.ui.screens.HomeScreen
import com.example.appreciclaje.viewmodel.LoginViewModel
import com.example.appreciclaje.viewmodel.RegisterViewModel


@Composable
fun NavigationWrapper(){
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {navController.navigate(Home){
                    popUpTo(Login) { inclusive = true }
                } },
                onNavigateToRegister = { navController.navigate(Register) }
            )
        }
        composable<Register> {
           RegisterScreen(
               viewModel = registerViewModel,
               onRegisterSuccess = {
                   navController.navigate(Login){
                          popUpTo(Register) { inclusive = true }
                   }
               },
               onNavigateToLogin = {
                   navController.popBackStack()
               }
            )
        }
        composable<Home> {
            HomeScreen()
        }
    }
}