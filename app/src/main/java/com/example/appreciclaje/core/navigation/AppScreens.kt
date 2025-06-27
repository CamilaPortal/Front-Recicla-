package com.example.appreciclaje.core.navigation

sealed class AppScreens(val route: String) {
    object Login : AppScreens("login")
    object Register : AppScreens("register")
    object Home : AppScreens("home")
    object Ranking : AppScreens("ranking")
    object QR : AppScreens("qr")
    object Canjes : AppScreens("canjes")
    object Activity : AppScreens("activity")
}

typealias Login = AppScreens.Login
typealias Register = AppScreens.Register
typealias Home = AppScreens.Home
typealias Activity = AppScreens.Activity
typealias Ranking = AppScreens.Ranking
typealias QR = AppScreens.QR
typealias Canjes = AppScreens.Canjes