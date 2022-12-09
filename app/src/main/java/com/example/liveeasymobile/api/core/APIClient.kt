package com.example.liveeasymobile.api.core

import android.content.Context
import com.example.liveeasymobile.api.context.AuthInterceptor
import com.example.liveeasymobile.api.context.SessionManager
import com.example.liveeasymobile.config.Config
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private lateinit var authAPIService: AuthAPIService
    private lateinit var privateAPIService: PrivateAPIService
    private val gsonConverterFactory: GsonConverterFactory =
        GsonConverterFactory.create()

    fun getAuthApiService(): AuthAPIService {
        // Initialize authAPIService if not initialized yet
        if (!::authAPIService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .client(getAuthAPIInterceptor())
                .build()

            authAPIService = retrofit.create(AuthAPIService::class.java)
        }

        return authAPIService
    }

    fun getPrivateAPIService(context: Context, sessionManager: SessionManager): PrivateAPIService {
        // Initialize privateAPIService if not initialized yet
        if (!::privateAPIService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .client(getPrivateAPIInterceptor(context, sessionManager))
                .build()

            privateAPIService = retrofit.create(PrivateAPIService::class.java)
        }

        return privateAPIService
    }

    private fun getAuthAPIInterceptor(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getHTTPLoggingInterceptor())
            .build()
    }

    private fun getPrivateAPIInterceptor(
        context: Context,
        sessionManager: SessionManager
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context, sessionManager))
            .addInterceptor(getHTTPLoggingInterceptor())
            .build()
    }

    private fun getHTTPLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

}