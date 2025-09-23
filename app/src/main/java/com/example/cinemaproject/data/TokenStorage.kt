package com.example.cinemaproject.data

import android.content.Context
import android.content.SharedPreferences

class TokenStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
    }
}

