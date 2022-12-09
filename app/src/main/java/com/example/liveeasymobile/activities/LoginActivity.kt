package com.example.liveeasymobile.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.liveeasymobile.R
import com.example.liveeasymobile.api.UserAPI
import com.example.liveeasymobile.api.context.SessionManager
import com.example.liveeasymobile.api.core.ApiClient
import com.example.liveeasymobile.databinding.ActivityLoginBinding
import com.example.liveeasymobile.entity.LoginGoogleRequest
import com.example.liveeasymobile.entity.LoginRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var userAPI: UserAPI
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initProgressDialog()
        firebaseAuth = FirebaseAuth.getInstance()
        allowLoginWithGoogle()
        initAPI()
        binding.registerTextView.setOnClickListener { goToRegister() }
        binding.loginButton.setOnClickListener { login() }
        binding.googleImageView.setOnClickListener { loginGoogle() }
    }

    private fun goToRegister() {
        startActivity(
            Intent(
                this,
                RegisterActivity::class.java
            )
        )
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Just a few second...")
        progressDialog.setCancelable(false)
    }

    private fun initAPI() {
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        userAPI = UserAPI(apiClient)
    }

    private fun validateForm(email: String, password: String): String {
        if (email.isEmpty() || password.isEmpty()) return "Please fill out all required fields"
        return if (!Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) "The entered email is not valid" else ""
    }

    private fun login() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val validationErrMsg = validateForm(email, password)
        if (validationErrMsg.isNotEmpty()) {
            Toast.makeText(this, validationErrMsg, Toast.LENGTH_LONG).show()
            return
        }

        progressDialog.show()
        userAPI.login(
            LoginRequest(email, password),
        ) { message, isSuccess, serverToken ->
            if (isSuccess) {
                sessionManager.saveAuthToken(serverToken!!)
            }

            progressDialog.dismiss()
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            if (isSuccess) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun allowLoginWithGoogle() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun loginGoogle() {
        googleLoginLauncher.launch(mGoogleSignInClient.signInIntent)
    }

    private var googleLoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }

        progressDialog.show()
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(
                ApiException::class.java
            )
            firebaseAuthWithGoogle(account!!.idToken!!)
        } catch (e: ApiException) {
            progressDialog.dismiss()
            Toast.makeText(
                this,
                "Login with google failed: " + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val firebaseCredential = GoogleAuthProvider
            .getCredential(idToken, null)

        firebaseAuth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (!task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Login failed: " + task.exception,
                        Toast.LENGTH_LONG
                    ).show()
                    progressDialog.dismiss()
                    return@addOnCompleteListener
                }

                getFirebaseToken()
            }
    }

    private fun getFirebaseToken() {
        firebaseAuth.currentUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Login with google failed: " + task.exception,
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnCompleteListener
                }
                sendFirebaseTokenToServer(task.result?.token!!)
            }
    }

    private fun sendFirebaseTokenToServer(firebaseToken: String) {
        userAPI.loginWithGoogle(
            this,
            LoginGoogleRequest(firebaseToken)
        ) { message, isSuccess, serverToken ->
            if (isSuccess) {
                firebaseAuth.signOut()
                sessionManager.saveAuthToken(serverToken!!)
            }

            progressDialog.dismiss()
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            if (isSuccess) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
