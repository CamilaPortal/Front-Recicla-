package com.example.appreciclaje.core.navigation

sealed class AppScreens(val route: String) {
    object Login : AppScreens("login")
    object Register : AppScreens("register")
    object Home : AppScreens("home")
    object Ranking : AppScreens("ranking")
    object QR : AppScreens("qr")
    object Canjes : AppScreens("canjes")
    object Activity : AppScreens("activity")
    object EmpresaHome : AppScreens("empresa_home")
    object ValidarCanjes : AppScreens("validar_canjes")
    object AdministrarCanjes : AppScreens("administrar_canjes")
    object EmpresaProfile : AppScreens("empresa_profile")
}

typealias Login = AppScreens.Login
typealias Register = AppScreens.Register
typealias Home = AppScreens.Home
typealias Activity = AppScreens.Activity
typealias Ranking = AppScreens.Ranking
typealias QR = AppScreens.QR
typealias Canjes = AppScreens.Canjes