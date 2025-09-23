package com.example.cinemaproject.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path

interface ApiService {
    @POST("/api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("/api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("/api/v1/sessions")
    suspend fun getSessions(
        @Header("Authorization") authorization: String,
        @Query("date") date: String? = null,
    ): SessionListResponse

    @GET("/api/v1/halls/{id}/plan")
    suspend fun getHallPlan(
        @Header("Authorization") authorization: String,
        @Path("id") hallId: String,
    ): HallPlan

    @GET("/api/v1/sessions/{sessionId}/tickets")
    suspend fun getSessionTickets(
        @Header("Authorization") authorization: String,
        @Path("sessionId") sessionId: String,
        @Query("status") status: String? = null,
    ): List<Ticket>
}

