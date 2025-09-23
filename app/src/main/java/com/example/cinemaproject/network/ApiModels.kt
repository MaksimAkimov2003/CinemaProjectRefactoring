package com.example.cinemaproject.network

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val age: Int?,
    val gender: String,
)

data class AuthResponse(
    val accessToken: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class Session(
    val id: String,
    val filmId: String,
    val hallId: String,
    val startAt: String,
    val timeslot: Timeslot?
)

data class Timeslot(
    val start: String?,
    val end: String?
)

data class Pagination(
    val page: Int?,
    val limit: Int?,
    val total: Int?,
    val pages: Int?,
)

data class SessionListResponse(
    val data: List<Session>,
    val pagination: Pagination?
)

