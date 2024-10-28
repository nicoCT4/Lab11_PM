package com.example.laboratorio11plats

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.laboratorio11plats.screen.*
import com.example.laboratorio11plats.viewmodel.AuthViewModel
import com.example.laboratorio11plats.viewmodel.BlogViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase y Firebase App Check
        FirebaseApp.initializeApp(this)

        // Configuración de Firebase App Check para proteger la app
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        // Configura para que Firebase renueve automáticamente los tokens de App Check
        firebaseAppCheck.setTokenAutoRefreshEnabled(true)

        setContent {
            val navController = rememberNavController()
            val authViewModel = AuthViewModel() // Instancia del ViewModel de autenticación
            val blogViewModel = BlogViewModel() // Instancia del ViewModel de publicaciones

            NavigationGraph(
                navController = navController,
                authViewModel = authViewModel,
                blogViewModel = blogViewModel
            )
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    blogViewModel: BlogViewModel
) {
    val startDestination = Screen.HomeScreen.route // Iniciar siempre en HomeScreen para iniciar sesión o registrarse

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.SignInScreen.route) {
            SignInScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate(Screen.SignUpScreen.route) },
                onSignInSuccess = {
                    navController.navigate(Screen.PostScreen.route) {
                        popUpTo(Screen.SignInScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.SignUpScreen.route) {
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(Screen.SignInScreen.route) },
                onSignUpSuccess = {
                    navController.navigate(Screen.PostScreen.route) {
                        popUpTo(Screen.SignUpScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(
                onNavigateToSignIn = { navController.navigate(Screen.SignInScreen.route) },
                onNavigateToSignUp = { navController.navigate(Screen.SignUpScreen.route) }
            )
        }
        composable(Screen.PostScreen.route) {
            PostScreen(
                blogViewModel = blogViewModel,
                onPostSuccess = {
                    Log.d("PostScreen", "onPostSuccess called")
                    navController.navigate(Screen.PostsListScreen.route) {
                        popUpTo(Screen.PostScreen.route) { inclusive = true }
                    }
                },
                onNavigateToPostsList = { navController.navigate(Screen.PostsListScreen.route) }
            )
        }
        composable(Screen.PostsListScreen.route) {
            PostsListScreen(blogViewModel = blogViewModel)
        }
    }
}
