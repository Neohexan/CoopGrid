package com.example.coopgrid.employer.data.utils

import android.content.Context
import android.util.Log
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.CategoryResponse
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.EmployerCategoryItem
import kotlinx.serialization.json.Json

object BusinessDataLoader {

    @PublishedApi
    internal val jsonInstance = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // Generic Asset Reader
    inline fun <reified T> loadFromAssets(context: Context, filePath: String): T? {
        return try {
            val jsonString = context.assets.open(filePath).bufferedReader().use { it.readText() }
            jsonInstance.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            Log.e("AppJsonReader", "Error reading asset file at path: $filePath", e)
            null
        }
    }

    // ------------------------------------------------------------------
    // EMPLOYER MODULE LOADERS
    // ------------------------------------------------------------------
    fun loadBusinessData(context: Context): List<EmployerCategoryItem> {
        val rootData = loadFromAssets<CategoryResponse>(
            context = context,
            filePath = "employer/business_categories.json"
        )
        return rootData?.categories ?: emptyList()
    }
}