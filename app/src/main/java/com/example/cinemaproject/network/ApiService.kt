package com.example.cinemaproject.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("/api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("/api/v1/sessions")
    suspend fun getSessions(
        @Header("Authorization") authorization: String,
    ): SessionListResponse
}

