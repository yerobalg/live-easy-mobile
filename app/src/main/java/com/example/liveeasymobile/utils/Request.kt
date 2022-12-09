package com.example.liveeasymobile.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RequestHelper {
    companion object Static {
        fun createPartField(value: String): RequestBody {
            return value.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        fun createFileRequest(file: ByteArray): RequestBody {
            return file.toRequestBody(
                "image/*".toMediaTypeOrNull(),
                0,
                file.size
            )
        }
    }
}