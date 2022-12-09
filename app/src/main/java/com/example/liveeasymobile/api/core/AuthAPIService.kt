package com.example.liveeasymobile.api.core

import com.example.liveeasymobile.entity.*
import retrofit2.Response
import retrofit2.http.*

interface AuthAPIService {
    @POST("auth/login")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/login/google")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun loginWithGoogle(@Body request: LoginGoogleRequest): Response<LoginResponse>


    @POST("auth/register")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}