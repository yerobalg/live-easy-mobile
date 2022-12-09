package com.example.liveeasymobile.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageHelper {
    companion object Static {
        @SuppressLint("Range", "Recycle")
        fun getImageName(context: Context, uri: Uri): String {
            var res = ""

            if (!uri.scheme.equals("content")) {
                return res
            }

            val cursor =
                context.contentResolver.query(uri, null, null, null, null)

            try {
                if (cursor == null || !cursor.moveToFirst()) {
                    return res
                }
                res = cursor.getString(
                    cursor.getColumnIndex(
                        OpenableColumns.DISPLAY_NAME
                    )
                )
            } finally {
                cursor!!.close()
            }

            if (res != null) {
                return res
            }

            res = uri.path!!
            val cut = res.lastIndexOf('/')
            if (cut != -1) {
                res = res.substring(cut + 1)
            }

            return res
        }

        fun getFileFromUri(
            context: Context,
            uri: Uri,
            fileName: String
        ): ByteArray? {
            val fileStream = context.
            contentResolver.
            openInputStream(uri) ?: return null

            return fileStream.readBytes()
        }
    }
}