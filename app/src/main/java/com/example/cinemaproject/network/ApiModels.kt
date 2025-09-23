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

// --- Session details ---
data class Seat(
    val id: String,
    val row: Int,
    val number: Int,
    val categoryId: String,
    val status: String? = null,
)

data class SeatCategory(
    val id: String,
    val name: String,
    val priceCents: Int,
)

data class HallPlan(
    val hallId: String,
    val rows: Int,
    val seats: List<Seat>,
    val categories: List<SeatCategory>,
)

data class Ticket(
    val id: String,
    val sessionId: String,
    val seatId: String,
    val categoryId: String,
    val priceCents: Int,
    val status: String,
)

