package com.example.liveeasymobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.liveeasymobile.api.UserAPI
import com.example.liveeasymobile.api.context.SessionManager
import com.example.liveeasymobile.api.core.ApiClient
import com.example.liveeasymobile.databinding.ActivityRegisterBinding
import com.example.liveeasymobile.entity.RegisterRequest

class RegisterActivity : AppCompatActivity() {
    private var binding: ActivityRegisterBinding? = null
    private var progressDialog: ProgressDialog? = null
    private lateinit var userAPI: UserAPI
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        initProgressDialog()
        initAPI()
        binding!!.toLoginTextView.setOnClickListener { finish() }
        binding!!.registerButton.setOnClickListener { registerHandler() }
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Loading")
        progressDialog!!.setMessage("Creating your account...")
        progressDialog!!.setCancelable(false)
    }

    private fun initAPI() {
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        userAPI = UserAPI(apiClient)
    }

    private fun validateForm(
        email: String,
        password: String,
        passwordConfirm: String
    ): String {
        if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty())
            return "Please fill out all required fields"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "The entered email is not valid"
        if (password != passwordConfirm)
            return "Password and confirm password must be the same"
        return if (password.length < 6)
            "Password must have at least 6 characters long"
        else ""
    }

    private fun registerHandler() {
        val email = binding!!.emailEditText.text.toString()
        val password = binding!!.passwordEditText.text.toString()
        val passwordConfirm = binding!!.confirmPasswordEditText.text.toString()
        val validationErrMsg = validateForm(email, password, passwordConfirm)
        if (validationErrMsg.isNotEmpty()) {
            Toast.makeText(this, validationErrMsg, Toast.LENGTH_LONG).show()
            return
        }
        progressDialog?.show()
        userAPI.register(
            this,
            RegisterRequest(email, password, "test")
        ) {message, isSuccess ->
            progressDialog?.dismiss()
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            if (!isSuccess) {
                return@register
            }

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}