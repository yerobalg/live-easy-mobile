package com.example.liveeasymobile.api.core

import com.example.liveeasymobile.entity.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PrivateAPIService {
    /* ===================== User ======================= */
    @GET("user/profile")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun profile(): Response<ProfileResponse>

    /* ==================== Medicine ==================== */
    @GET("medicine?limit=10")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun getMedicines(): Response<GetMedicinesResponse>

    @GET("medicine/{medicineID}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun getMedicine(
        @Path("medicineID") medicineID: Int
    ): Response<GetMedicineResponse>

    @Multipart
    @POST("medicine")
    @JvmSuppressWildcards
    suspend fun createMedicine(
        @PartMap() medParams: Map<String, RequestBody>,
        @Part image: MultipartBody.Part
    ): Response<TransactionMedicineResponse>

    @Multipart
    @PUT("medicine/{medicineID}")
    @JvmSuppressWildcards
    suspend fun updateMedicine(
        @Path("medicineID") medicineID: Int,
        @PartMap() medParams: Map<String, RequestBody>,
        @Part image: MultipartBody.Part?
    ): Response<TransactionMedicineResponse>

    @DELETE("medicine/{medicineID}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun deleteMedicine(
        @Path("medicineID") medicineID: Int,
    ): Response<TransactionMedicineResponse>
}