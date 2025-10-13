package com.example.cinemaproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.example.cinemaproject.ui.theme.CinemaProjectTheme
import com.example.cinemaproject.ui.RegisterScreen
import com.example.cinemaproject.ui.LoginScreen
import com.example.cinemaproject.ui.SessionsListScreen
import com.example.cinemaproject.ui.SessionDetailsScreen
import com.example.cinemaproject.ui.FilmDetailsScreen
import com.example.cinemaproject.data.TokenStorage
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CinemaProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        val context = LocalContext.current
                        val tokenStorage = TokenStorage(context)
                        val navController = rememberNavController()
                        val startDestination = if (tokenStorage.getAccessToken() != null) "sessions" else "auth"
                        NavHost(navController = navController, startDestination = startDestination) {
                            composable("auth") {
                                val showLogin = remember { mutableStateOf(true) }
                                if (showLogin.value) {
                                    LoginScreen(
                                        tokenStorage = tokenStorage,
                                        onLoggedIn = { navController.navigate("sessions") { popUpTo("auth") { inclusive = true } } },
                                        onNavigateToRegister = { showLogin.value = false }
                                    )
                                } else {
                                    RegisterScreen(
                                        tokenStorage = tokenStorage,
                                        onRegistered = { navController.navigate("sessions") { popUpTo("auth") { inclusive = true } } },
                                        onNavigateToLogin = { showLogin.value = true }
                                    )
                                }
                            }
                            composable("sessions") {
                                SessionsListScreen(
                                    tokenStorage = tokenStorage,
                                    onOpenDetails = { sessionId, hallId -> navController.navigate("details/$sessionId/$hallId") },
                                    onOpenFilm = { filmId -> navController.navigate("film/$filmId") }
                                )
                            }
                            composable("film/{filmId}") { backStack ->
                                val filmId = backStack.arguments?.getString("filmId") ?: return@composable
                                FilmDetailsScreen(filmId = filmId)
                            }
                            composable("details/{sessionId}/{hallId}") { backStack ->
                                val sessionId = backStack.arguments?.getString("sessionId") ?: return@composable
                                val hallId = backStack.arguments?.getString("hallId") ?: return@composable
                                SessionDetailsScreen(
                                    tokenStorage = tokenStorage,
                                    sessionId = sessionId,
                                    hallId = hallId,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    CinemaProjectTheme {
        RegisterScreen(tokenStorage = TokenStorage(androidx.compose.ui.platform.LocalContext.current), onRegistered = {})
    }
}