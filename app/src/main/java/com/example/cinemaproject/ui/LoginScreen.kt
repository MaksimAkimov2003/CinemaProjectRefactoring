package com.example.cinemaproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cinemaproject.data.TokenStorage
import com.example.cinemaproject.network.ApiProvider
import com.example.cinemaproject.network.LoginRequest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    tokenStorage: TokenStorage,
    onLoggedIn: () -> Unit,
    onNavigateToRegister: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SnackbarHost(hostState = snackbarHostState)

        Text(text = "Login")

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading.value = true
                    try {
                        val req = LoginRequest(
                            email = email.value.trim(),
                            password = password.value,
                        )
                        val api = ApiProvider.getApi(context)
                        val resp = api.login(req)
                        tokenStorage.saveAccessToken(resp.accessToken)
                        onLoggedIn()
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(e.message ?: "Login failed")
                    } finally {
                        isLoading.value = false
                    }
                }
            },
            enabled = !isLoading.value
        ) {
            if (isLoading.value) {
                CircularProgressIndicator()
            } else {
                Text("Login")
            }
        }

        TextButton(onClick = {
            email.value = ""
            password.value = ""
        }) { Text("Clear") }

        TextButton(onClick = onNavigateToRegister) { Text("No account? Register") }
    }
}

