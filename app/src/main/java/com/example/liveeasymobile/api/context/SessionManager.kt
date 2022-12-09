package com.example.liveeasymobile.api.context

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.liveeasymobile.R
import com.example.liveeasymobile.api.core.ApiClient
import com.example.liveeasymobile.entity.ProfileResponse
import com.example.liveeasymobile.entity.User
import kotlinx.coroutines.*
import retrofit2.Response

class SessionManager(private val context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.app_name),
        Context.MODE_PRIVATE
    )

    companion object {
        const val USER_TOKEN = "user_token"
    }

    fun saveAuthToken(token: String) {
        Log.i("SessionManager", "Saving token: $token")
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.apply()
    }

    fun getCurrentUser(
        userAPI: com.example.liveeasymobile.api.UserAPI
    ): User? {
        val token = fetchAuthToken()
        var user: User? = null
        if (token == null || token.isEmpty()) {
            return user
        }

        Log.i("Token", "Token: $token")

        var response: Response<ProfileResponse>?
        runBlocking {
            response = userAPI.getProfileResponsePromise(
                context,
                this@SessionManager
            ).await()
        }

        if (response!!.code() == 200) {
            user = response!!.body()!!.data
            return user
        }

        clearAuthToken()

        if (response!!.code() == 401) {
            Toast.makeText(
                context,
                "Your session has expired, please login again",
                Toast.LENGTH_SHORT
            ).show()
            return user
        }

        Toast.makeText(
            context,
            "Something went wrong, please try again later",
            Toast.LENGTH_SHORT
        ).show()

        return user
    }
}