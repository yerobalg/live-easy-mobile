package com.example.liveeasymobile.utils

import org.json.JSONObject
import retrofit2.Response

class ResponseHelper {
    companion object Static {
        fun <T : Any> getErrorMessage(response: Response<T>): String {
            val jObjError = JSONObject(response.errorBody()!!.string())
            return jObjError.getString("data")
        }
    }
}