package com.example.laboratorio11plats.screen

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object SignInScreen : Screen("signin")
    object SignUpScreen : Screen("signup")
    object PostScreen : Screen("post")
    object PostsListScreen : Screen("postsList")
}
