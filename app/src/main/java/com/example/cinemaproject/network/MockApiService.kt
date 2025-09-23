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

    override suspend fun getSessions(authorization: String, date: String?): SessionListResponse {
        val primary = when (date) {
            null -> "mocks/sessions_list_success.json"
            else -> "mocks/sessions_${date}_success.json"
        }
        val json = try {
            readAsset(primary)
        } catch (e: Exception) {
            // Fallback to default list if dated mock not found
            try {
                readAsset("mocks/sessions_list_success.json")
            } catch (_: Exception) {
                // Last resort: empty list
                "{" +
                    "\"data\":[]," +
                    "\"pagination\":{\"page\":0,\"limit\":20,\"total\":0,\"pages\":0}" +
                "}"
            }
        }
        return gson.fromJson(json, SessionListResponse::class.java)
    }

    override suspend fun getHallPlan(authorization: String, hallId: String): HallPlan {
        val primary = "mocks/hall_${hallId}_plan.json"
        val json = try {
            readAsset(primary)
        } catch (e: Exception) {
            // Fallback to default plan
            readAsset("mocks/hall_h1_plan.json")
        }
        return gson.fromJson(json, HallPlan::class.java)
    }

    override suspend fun getSessionTickets(authorization: String, sessionId: String, status: String?): List<Ticket> {
        val primary = "mocks/session_${sessionId}_tickets.json"
        val json = try {
            readAsset(primary)
        } catch (e: Exception) {
            // Fallback to default tickets
            readAsset("mocks/session_s1_tickets.json")
        }
        val type = com.google.gson.reflect.TypeToken.getParameterized(List::class.java, Ticket::class.java).type
        return gson.fromJson(json, type)
    }

    private fun readAsset(path: String): String {
        context.assets.open(path).use { input ->
            return input.bufferedReader().use(BufferedReader::readText)
        }
    }
}

