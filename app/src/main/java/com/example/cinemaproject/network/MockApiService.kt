package com.example.cinemaproject.network

import android.content.Context
import com.google.gson.Gson
import java.io.BufferedReader

class MockApiService(private val context: Context) : ApiService {

    private val gson = Gson()

    override suspend fun register(body: RegisterRequest): AuthResponse {
        val json = readAsset("mocks/auth_register_success.json")
        return gson.fromJson(json, AuthResponse::class.java)
    }

    override suspend fun login(body: LoginRequest): AuthResponse {
        val json = readAsset("mocks/auth_login_success.json")
        return gson.fromJson(json, AuthResponse::class.java)
    }

    override suspend fun getSessions(authorization: String): SessionListResponse {
        val json = readAsset("mocks/sessions_list_success.json")
        return gson.fromJson(json, SessionListResponse::class.java)
    }

    private fun readAsset(path: String): String {
        context.assets.open(path).use { input ->
            return input.bufferedReader().use(BufferedReader::readText)
        }
    }
}

