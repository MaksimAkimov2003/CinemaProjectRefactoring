package com.example.cinemaproject.network

import android.content.Context
import com.example.cinemaproject.BuildConfig

object ApiProvider {
    fun getApi(context: Context): ApiService {
        return if (BuildConfig.IS_REAL_API) {
            RetrofitClient.api
        } else {
            MockApiService(context)
        }
    }
}

