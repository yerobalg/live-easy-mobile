package com.example.liveeasymobile.api

import android.content.Context
import com.example.liveeasymobile.api.context.SessionManager
import com.example.liveeasymobile.api.core.ApiClient
import com.example.liveeasymobile.entity.GetMedicineResponse
import com.example.liveeasymobile.entity.Medicine
import com.example.liveeasymobile.utils.ResponseHelper
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class MedicineAPI(
    private val apiClient: ApiClient,
    private val sessionManager: SessionManager
) {
    fun getMedicines(
        context: Context,
        onFinished: (
            message: String,
            isSuccess: Boolean,
            meds: ArrayList<Medicine>?
        ) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = apiClient
                .getPrivateAPIService(context, sessionManager)
                .getMedicines()
            lateinit var message: String
            var meds: ArrayList<Medicine>? = null
            val isSuccess = response.isSuccessful
            if (isSuccess) {
                message = "Successfully created new medicine!"
                meds = response.body()?.data
            } else {
                message = ResponseHelper.getErrorMessage(response)
            }
            withContext(Dispatchers.Main) {
                onFinished(message, isSuccess, meds)
            }
        }
    }

    fun getMedicineResponsePromise(
        context: Context,
        medID: Int
    ): Deferred<Response<GetMedicineResponse>> {
        return GlobalScope.async(Dispatchers.IO) {
            apiClient.getPrivateAPIService(context, sessionManager)
                .getMedicine(medID)
        }
    }

    fun createMedicine(
        context: Context,
        medParams: Map<String, RequestBody>,
        fileParams: MultipartBody.Part,
        onFinished: (message: String, isSuccess: Boolean) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = apiClient.getPrivateAPIService(
                context,
                sessionManager
            ).createMedicine(medParams, fileParams)
            lateinit var message: String
            val isSuccess = response.isSuccessful
            message = if (isSuccess) {
                "Successfully created new medicine!"
            } else {
                ResponseHelper.getErrorMessage(response)
            }

            withContext(Dispatchers.Main) {
                onFinished(message, isSuccess)
            }
        }
    }

    fun updateMedicine(
        context: Context,
        medID: Int,
        medParams: Map<String, RequestBody>,
        fileParams: MultipartBody.Part?,
        onFinished: (message: String, isSuccess: Boolean) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = apiClient.getPrivateAPIService(
                context,
                sessionManager
            ).updateMedicine(medID, medParams, fileParams)
            lateinit var message: String
            val isSuccess = response.isSuccessful
            message = if (isSuccess) {
                "Successfully updated new medicine!"
            } else {
                ResponseHelper.getErrorMessage(response)
            }

            withContext(Dispatchers.Main) {
                onFinished(message, isSuccess)
            }
        }
    }

    fun deleteMedicine(
        context: Context,
        medID: Int,
        onFinished: (message: String, isSuccess: Boolean) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = apiClient.getPrivateAPIService(
                context,
                sessionManager
            ).deleteMedicine(medID)
            lateinit var message: String
            val isSuccess = response.isSuccessful
            message = if (isSuccess) {
                "Successfully deleted medicine!"
            } else {
                ResponseHelper.getErrorMessage(response)
            }

            withContext(Dispatchers.Main) {
                onFinished(message, isSuccess)
            }
        }
    }

}