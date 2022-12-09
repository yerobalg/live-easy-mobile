package com.example.liveeasymobile.api.context

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context, sessionManager: SessionManager) : Interceptor {
    private val sessionManager = sessionManager
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}