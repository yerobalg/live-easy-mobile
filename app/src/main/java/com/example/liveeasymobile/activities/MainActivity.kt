package com.example.liveeasymobile.activities

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.liveeasymobile.adapters.MedAdapter
import com.example.liveeasymobile.api.MedicineAPI
import com.example.liveeasymobile.api.UserAPI
import com.example.liveeasymobile.api.context.SessionManager
import com.example.liveeasymobile.api.core.ApiClient
import com.example.liveeasymobile.databinding.ActivityMainBinding
import com.example.liveeasymobile.utils.ResponseHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    private lateinit var userAPI: UserAPI
    private lateinit var medAPI: MedicineAPI
    private lateinit var medAdapter: MedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAPI()
        initProgressDialog()
        initRecyclerView()
        binding.addMedButton.setOnClickListener { toInsertMed() }
        binding.logoutButton.setOnClickListener { logout() }
    }

    private fun initAPI() {
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        userAPI = UserAPI(apiClient)
        medAPI = MedicineAPI(apiClient, sessionManager)
    }

    protected override fun onStart() {
        super.onStart()
        val currentUser = sessionManager.getCurrentUser(userAPI)
        if (currentUser == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
            return
        }
        binding.userEmailTextView.text = currentUser.email
        medAPI.getMedicines(this) { message, isSuccess, meds ->
            if (isSuccess) {
                medAdapter.setMedicines(meds!!)
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Fetching medicine...")
        progressDialog.setCancelable(false)
    }

    private fun initRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(
            this,
            2,
            GridLayoutManager.VERTICAL,
            false
        )
        medAdapter = MedAdapter(this, medAPI)
        recyclerView.adapter = medAdapter
    }


    private fun toInsertMed() {
        startActivity(Intent(this@MainActivity, InsertMedActivity::class.java))
        finish()
    }

    private fun logout() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Log out")
        alert.setMessage("Are you sure want to log out?")
        alert.setPositiveButton("Yes")
        { _: DialogInterface?, _: Int -> processLogout() }
        alert.setNegativeButton("No")
        { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alert.show()
    }

    private fun processLogout() {
        sessionManager.clearAuthToken()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }
}