package com.example.coopgrid.data.lanlocal

import android.content.Context
import android.util.Log
import com.example.coopgrid.data.lanlocal.model.AppStrings
import kotlinx.serialization.json.Json

object AppLangJsonReader {

    @PublishedApi
    internal val jsonInstance = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Centralized localization reader.
     * @param langCode Iso code like "en", "hinglish", or "hi"
     */
    fun loadAppStrings(context: Context, langCode: String): AppStrings {
        val filePath = "localization/strings_$langCode.json"
        return loadFromAssets<AppStrings>(context, filePath) ?: AppStrings()
    }

    /**
     * Inline Reified Generic Asset Reader
     */
    inline fun <reified T> loadFromAssets(context: Context, filePath: String): T? {
        return try {
            val jsonString = context.assets.open(filePath).bufferedReader().use { it.readText() }
            jsonInstance.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            Log.e("AppJsonReader", "Error reading asset JSON at path: $filePath", e)
            null
        }
    }
}