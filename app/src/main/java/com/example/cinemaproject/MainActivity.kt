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
import com.example.cinemaproject.data.TokenStorage

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
                        val hasToken = tokenStorage.getAccessToken() != null
                        val showLogin = remember { mutableStateOf(!hasToken) }
                        if (!showLogin.value) {
                            SessionsListScreen(tokenStorage = tokenStorage)
                        } else if (showLogin.value) {
                            LoginScreen(
                                tokenStorage = tokenStorage,
                                onLoggedIn = { /* TODO: navigate to home */ },
                                onNavigateToRegister = { showLogin.value = false }
                            )
                        } else {
                            RegisterScreen(
                                tokenStorage = tokenStorage,
                                onRegistered = { /* TODO: navigate to home */ },
                                onNavigateToLogin = { showLogin.value = true }
                            )
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