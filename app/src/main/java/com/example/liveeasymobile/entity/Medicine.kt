package com.example.liveeasymobile.entity

import com.google.gson.annotations.SerializedName

data class Medicine(
    @SerializedName("id")
    var id: Int,

    @SerializedName("name")
    var name: String,

    @SerializedName("price")
    var price: Int,

    @SerializedName("priceString")
    var priceString: String,

    @SerializedName("quantity")
    var quantity: Int,

    @SerializedName("imageURL")
    var imageURL: String,

    @SerializedName("userID")
    var userID: Int,

    @SerializedName("createdAt")
    var createdAt: Int,

    @SerializedName("updatedAt")
    var updatedAt: Int,
)

data class GetMedicinesResponse(
    @SerializedName("message")
    var message: String,

    @SerializedName("isSuccess")
    var isSuccess: Boolean,

    @SerializedName("data")
    var data: ArrayList<Medicine>?,

    @SerializedName("pagination")
    var pagination: Pagination?,
)

data class GetMedicineResponse(
    @SerializedName("message")
    var message: String,

    @SerializedName("isSuccess")
    var isSuccess: Boolean,

    @SerializedName("data")
    var data: Medicine?,

    @SerializedName("pagination")
    var pagination: Pagination?,
)

data class TransactionMedicineResponse(
    @SerializedName("message")
    var message: String,

    @SerializedName("isSuccess")
    var isSuccess: Boolean,
)
