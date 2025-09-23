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
import com.example.cinemaproject.network.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    tokenStorage: TokenStorage,
    onRegistered: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val age = remember { mutableStateOf("") }
    val gender = remember { mutableStateOf("") } // MALE or FEMALE

    val isLoading = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SnackbarHost(hostState = snackbarHostState)

        Text(text = "Register")

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
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = firstName.value,
            onValueChange = { firstName.value = it },
            label = { Text("First name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lastName.value,
            onValueChange = { lastName.value = it },
            label = { Text("Last name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = age.value,
            onValueChange = { age.value = it.filter { ch -> ch.isDigit() } },
            label = { Text("Age (optional)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = gender.value,
            onValueChange = { gender.value = it.uppercase() },
            label = { Text("Gender (MALE/FEMALE)") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading.value = true
                    try {
                        val req = RegisterRequest(
                            email = email.value.trim(),
                            password = password.value,
                            firstName = firstName.value.trim(),
                            lastName = lastName.value.trim(),
                            age = age.value.toIntOrNull(),
                            gender = gender.value.trim(),
                        )
                        val api = ApiProvider.getApi(context)
                        val resp = api.register(req)
                        tokenStorage.saveAccessToken(resp.accessToken)
                        onRegistered()
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(e.message ?: "Registration failed")
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
                Text("Register")
            }
        }

        TextButton(onClick = {
            email.value = ""
            password.value = ""
            firstName.value = ""
            lastName.value = ""
            age.value = ""
            gender.value = ""
        }) { Text("Clear") }

        TextButton(onClick = onNavigateToLogin) { Text("Already have an account? Login") }
    }
}

