package com.example.coopgrid.worker.registration.data.form.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.coopgrid.worker.data.apiservice.ApiService
import com.example.coopgrid.worker.data.local.WorkerTokenManager
import com.example.coopgrid.worker.registration.presentation.steps.step4.viewmodel.KycDocFieldType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.UUID
import javax.inject.Inject

class KycRepository @Inject constructor(
    private val apiService: ApiService, // Aapka Retrofit/Ktor API interface
    private val tokenManager: WorkerTokenManager, // Token manager jo automatically header attach karta hai
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "KycRepository"
    }

    /**
     * Compressed File ko upload karta hai server par.
     * @param file NativeImageCompressor se compressed JPEG file
     * @param fieldType KYC document ka type (FRONT_IMAGE, BACK_IMAGE, etc.)
     * @return Uploaded Media Server URL ya Failure Exception
     */

    suspend fun uploadFileInChunks(
        file: File,
        fieldType: KycDocFieldType,
        originalUri: Uri? = null // Optional original URI for stats comparison
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "==================================================")
            Log.d(TAG, "INIT: Starting chunked upload for field: ${fieldType.name}")
            Log.d(TAG, "File path: ${file.absolutePath} | File exists: ${file.exists()}")

            // 1. Log compression stats before chunking starts
            logCompressionStats(fieldType, originalUri, file)

            // Edge Case Validation: Check if file is valid and non-empty
            if (!file.exists() || file.length() == 0L) {
                Log.e(TAG, "ERROR: Target file does not exist or is completely empty (0 bytes).")
                return@withContext Result.failure(IllegalArgumentException("File is missing or empty."))
            }

            // 1. Generate a unique cryptographic Media ID (UUID v4) for tracking on server
            val mediaId = UUID.randomUUID().toString()
            val fileName = "${fieldType.name.lowercase()}_${System.currentTimeMillis()}.jpg"
            val contentType = "image/jpeg"

            Log.i(TAG, "METADATA -> MediaId: $mediaId | FileName: $fileName | Size: ${file.length()} bytes")

            // 3. Prepare RequestBodies and Multipart Part
            val mediaIdBody = mediaId.toRequestBody("text/plain".toMediaTypeOrNull())
            val fileNameBody = fileName.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentTypeBody = contentType.toRequestBody("text/plain".toMediaTypeOrNull())

            val fileRequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", fileName, fileRequestBody)

            // 4. Execute Network Request
            Log.d(TAG, "NETWORK: Sending complete file to 'media/upload'...")
            val response = apiService.uploadSingleMedia(
                mediaId = mediaIdBody,
                fileName = fileNameBody,
                contentType = contentTypeBody,
                file = filePart
            )

            // 5. Handle Response
            if (!response.isSuccessful || response.body()?.success != true) {
                val serverMessage = response.body()?.message ?: "Unknown error"
                val errorCode = response.code()
                Log.e(TAG, "FAILED: Upload rejected! HTTP Code: $errorCode | Message: $serverMessage")
                return@withContext Result.failure(Exception("Upload failed [Code $errorCode]: $serverMessage"))
            }

            val finalMediaId = response.body()?.mediaId ?: mediaId
            Log.i(TAG, "SUCCESS: File uploaded completely for [${fieldType.name}]. Final Media ID: $finalMediaId")
            Log.d(TAG, "==================================================")

            Result.success(finalMediaId)

        } catch (e: Exception) {
            Log.e(TAG, "FATAL EXCEPTION: Crash or network timeout during chunked upload for [${fieldType.name}]", e)
            Result.failure(e)
        }
    }
    /**
     * Helper function to calculate and print original vs compressed file sizes in Logcat.
     */
    private fun logCompressionStats(fieldType: KycDocFieldType, originalUri: Uri?, compressedFile: File) {
        try {
            val compressedFileSize = compressedFile.length()
            var originalFileSize: Long = -1L

            if (originalUri != null) {
                context.contentResolver.openFileDescriptor(originalUri, "r")?.use { pfd ->
                    originalFileSize = pfd.statSize
                }
            }

            fun formatSize(size: Long): String {
                if (size <= 0) return "Unknown"
                val kb = size / 1024.0
                val mb = kb / 1024.0
                return if (mb >= 1.0) String.format("%.2f MB", mb) else String.format("%.2f KB", kb)
            }

            Log.i(TAG, "📊 COMPRESSION STATS for [${fieldType.name}]:")
            if (originalFileSize != -1L) {
                Log.i(TAG, "   - Original Size: ${formatSize(originalFileSize)} ($originalFileSize bytes)")
            }
            Log.i(TAG, "   - Compressed Size: ${formatSize(compressedFileSize)} ($compressedFileSize bytes)")

            if (originalFileSize != -1L && originalFileSize > 0) {
                val reductionPercent = ((originalFileSize - compressedFileSize).toDouble() / originalFileSize) * 100
                Log.i(TAG, "   - Space Saved: ${String.format("%.1f%%", reductionPercent)}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating compression stats", e)
        }
    }
}