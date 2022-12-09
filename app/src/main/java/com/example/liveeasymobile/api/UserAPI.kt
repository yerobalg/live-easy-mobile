package com.example.liveeasymobile.api

import android.content.Context
import com.example.liveeasymobile.api.context.SessionManager
import com.example.liveeasymobile.api.core.ApiClient
import com.example.liveeasymobile.entity.LoginGoogleRequest
import com.example.liveeasymobile.entity.LoginRequest
import com.example.liveeasymobile.entity.ProfileResponse
import com.example.liveeasymobile.entity.RegisterRequest
import com.example.liveeasymobile.utils.ResponseHelper
import kotlinx.coroutines.*
import retrofit2.Response


class UserAPI(private val apiClient: ApiClient) {

    fun login(
        loginRequest: LoginRequest,
        onFinished: (
            message: String,
            isSuccess: Boolean,
            serverToken: String?
        ) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = apiClient.getAuthApiService().login(loginRequest)
            lateinit var message: String
            var serverToken: String? = null
            val isSuccess = response.isSuccessful
            if (isSuccess) {
                message = "Login successful"
                serverToken = response.body()?.data!!.token
            } else {
                message = ResponseHelper.getErrorMessage(response)
            }

            withContext(Dispatchers.Main) {
                onFinished(message, isSuccess, serverToken)
            }
        }
    }

    fun register(
        context: Context,
        registerRequest: RegisterRequest,
        onFinished: (message: String, isSuccess: Boolean) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = apiClient
                .getAuthApiService()
                .register(registerRequest)
            lateinit var message: String
            val isSuccess = response.isSuccessful
            message = if (isSuccess) {
                "Register successful"
            } else {
                ResponseHelper.getErrorMessage(response)
            }
            withContext(Dispatchers.Main) {
                onFinished(message, isSuccess)
            }
        }
    }

    fun loginWithGoogle(
        context: Context,
        loginGoogleRequest: LoginGoogleRequest,
        onFinished: (
            message: String,
            isSuccess: Boolean,
            serverToken: String?
        ) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = apiClient
                .getAuthApiService()
                .loginWithGoogle(loginGoogleRequest)
            lateinit var message: String
            val isSuccess = response.isSuccessful
            var serverToken: String? = null
            if (isSuccess) {
                message = "Login with google success"
                serverToken = response.body()?.data?.token
            } else {
                message = ResponseHelper.getErrorMessage(response)
            }
            withContext(Dispatchers.Main) {
                onFinished(message, isSuccess, serverToken)
            }
        }
    }

    fun getProfileResponsePromise(
        context: Context,
        sessionManager: SessionManager
    ): Deferred<Response<ProfileResponse>> {
        return GlobalScope.async(Dispatchers.IO) {
            apiClient.getPrivateAPIService(
                context,
                sessionManager
            ).profile()
        }
    }
}
