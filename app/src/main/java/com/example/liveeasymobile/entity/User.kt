package com.example.liveeasymobile.entity

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("id")
    var id: Int,

    @SerializedName("name")
    var name: String,

    @SerializedName("email")
    var email: String,

    @SerializedName("createdAt")
    var createdAt: Int,

    @SerializedName("updatedAt")
    var updatedAt: Int,
)

data class LoginRequest (
    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String
)

data class LoginData (
    @SerializedName("user")
    var user: User,

    @SerializedName("token")
    var token: String,
)

data class LoginResponse (
    @SerializedName("message")
    var message: String,

    @SerializedName("isSuccess")
    var isSuccess: Boolean,

    @SerializedName("data")
    var data: LoginData?,

    @SerializedName("pagination")
    var pagination: Pagination?,
)

data class ProfileResponse (
    @SerializedName("message")
    var message: String,

    @SerializedName("isSuccess")
    var isSuccess: Boolean,

    @SerializedName("data")
    var data: User,

    @SerializedName("pagination")
    var pagination: Pagination?,
)

data class RegisterRequest (
    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String,

    @SerializedName("name")
    var name: String
)

data class RegisterResponse (
    @SerializedName("message")
    var message: String,

    @SerializedName("isSuccess")
    var isSuccess: Boolean,

    @SerializedName("data")
    var data: User,

    @SerializedName("pagination")
    var pagination: Pagination?,
)

data class LoginGoogleRequest (
    @SerializedName("firebaseJWT")
    var firebaseJWT: String
)