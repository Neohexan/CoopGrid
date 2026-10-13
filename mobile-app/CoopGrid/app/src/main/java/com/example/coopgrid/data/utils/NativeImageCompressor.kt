package com.example.coopgrid.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

object NativeImageCompressor {

    fun compressImage(
        context: Context,
        imageUri: Uri,
        maxDimension: Int = 1280,
        quality: Int = 75
    ): File? {
        return try {
            // 1. Read bounds only to check original dimensions without loading full image into memory
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            val originalWidth = options.outWidth
            val originalHeight = options.outHeight

            if (originalWidth <= 0 || originalHeight <= 0) return null

            // 2. Calculate optimal inSampleSize to prevent OutOfMemoryError (OOM)
            var inSampleSize = 1
            if (originalHeight > maxDimension || originalWidth > maxDimension) {
                val halfHeight = originalHeight / 2
                val halfWidth = originalWidth / 2
                while ((halfHeight / inSampleSize) >= maxDimension && (halfWidth / inSampleSize) >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            // 3. Decode scaled bitmap using calculated sample size
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }
            val decodedBitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            } ?: return null

            // 4. Further scale down precisely to maxDimension limits if needed
            val finalBitmap = scaleBitmapToMaxDimension(decodedBitmap, maxDimension)

            // 5. Create a temporary compressed file in cache directory
            val compressedFile = File(context.cacheDir, "kyc_compressed_${System.currentTimeMillis()}.jpg")
            FileOutputStream(compressedFile).use { outputStream ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
            }

            // 6. Clean up bitmaps to free native memory
            if (finalBitmap != decodedBitmap) {
                decodedBitmap.recycle()
            }
            finalBitmap.recycle()

            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun scaleBitmapToMaxDimension(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int

        if (ratio > 1f) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt()
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt()
        }

        return bitmap.scale(targetWidth, targetHeight)
    }
}