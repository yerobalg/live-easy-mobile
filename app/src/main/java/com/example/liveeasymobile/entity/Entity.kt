package com.example.liveeasymobile.entity

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("limit")
    var limit: Int,

    @SerializedName("currentPage")
    var currentPage: Int,

    @SerializedName("totalPage")
    var totalPage: Int,

    @SerializedName("currentElement")
    var currentElement: Int,

    @SerializedName("totalElement")
    var totalElement: Int,
)