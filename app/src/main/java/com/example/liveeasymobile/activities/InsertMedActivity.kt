package com.example.liveeasymobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.liveeasymobile.api.MedicineAPI
import com.example.liveeasymobile.api.UserAPI
import com.example.liveeasymobile.api.context.SessionManager
import com.example.liveeasymobile.api.core.ApiClient
import com.example.liveeasymobile.databinding.LayoutFormBinding
import com.example.liveeasymobile.entity.Medicine
import com.example.liveeasymobile.utils.ImageHelper
import com.example.liveeasymobile.utils.RequestHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class InsertMedActivity : AppCompatActivity() {
    private lateinit var binding: LayoutFormBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    private lateinit var medAPI: MedicineAPI
    private var imageUri: Uri? = null
    private var file: ByteArray? = null
    private var imageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutFormBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        initAPI()
        initProgressDialog()
        binding.uploadButton.setOnClickListener { chooseImage() }
        binding.submitButton.setOnClickListener { insertMed() }
    }

    private fun initAPI() {
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        medAPI = MedicineAPI(apiClient, sessionManager)
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Just a few second...")
        progressDialog.setCancelable(false)
    }

    private fun chooseImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        chooseImageLauncher.launch(intent)
    }

    private var chooseImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK || result.data == null) {
            return@registerForActivityResult
        }
        imageUri = result.data?.data

        imageName = ImageHelper.getImageName(this, imageUri!!)
        file = ImageHelper.getFileFromUri(this, imageUri!!, imageName!!)
        if (file == null) {
            Toast.makeText(
                this,
                "Failed to load image",
                Toast.LENGTH_SHORT
            ).show()
            return@registerForActivityResult
        }
        binding.imageEditText.setText(imageName)
    }

    private fun validateForm(
        name: String,
        price: String,
        quantity: String,
        imageName: String
    ): String {
        if (
            name.isEmpty() ||
            price.isEmpty() ||
            quantity.isEmpty() ||
            imageName.isEmpty()
        ) return "Please fill out all required fields"
        if (file == null) return "Please upload an image"
        return ""
    }

    private fun insertMed() {
        val name = binding.medEditText.text.toString()
        val price = binding.priceEditText.text.toString()
        val quantity = binding.qtyEditText.text.toString()
        val imageName = binding.imageEditText.text.toString()
        val validationErrMsg = validateForm(name, price, quantity, imageName)
        if (validationErrMsg.isNotEmpty()) {
            Toast.makeText(
                this,
                validationErrMsg,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        progressDialog.show()

        val requestBodyStrMap = createRequestBodyString(
            name,
            price,
            quantity,
        )

        val requestFile = RequestHelper.createFileRequest(file!!)

        val requestBodyFile = MultipartBody.Part.createFormData(
            "image",
            imageName,
            requestFile
        )

        medAPI.createMedicine(
            this,
            requestBodyStrMap,
            requestBodyFile
        ) { message, isSuccess ->
            progressDialog.dismiss()
            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()
            if (!isSuccess) {
                return@createMedicine
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun createRequestBodyString(
        name: String,
        price: String,
        quantity: String,
    ): HashMap<String, RequestBody> {
        val requestBody = HashMap<String, RequestBody>()
        requestBody["name"] = RequestHelper.createPartField(name)
        requestBody["price"] = RequestHelper.createPartField(price)
        requestBody["quantity"] = RequestHelper.createPartField(quantity)
        return requestBody
    }
}