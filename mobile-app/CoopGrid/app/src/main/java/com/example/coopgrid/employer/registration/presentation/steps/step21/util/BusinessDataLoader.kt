package com.example.coopgrid.employer.registration.presentation.steps.step21.util

import android.content.Context
import com.example.coopgrid.R
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.CategoryGroup
import kotlinx.serialization.json.Json
import android.util.Log
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.BusinessCategoryRoot

object BusinessDataLoader {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun loadBusinessData(context: Context): List<CategoryGroup> {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.business_categories)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            // 🔹 FIX: Direct List<CategoryGroup> ki jagah BusinessCategoryRoot decode karein
            val rootData = jsonParser.decodeFromString<BusinessCategoryRoot>(jsonString)
            rootData.categories
        } catch (e: Exception) {
            // Log print karne se agar issue hoga toh Logcat me dikh jayega
            Log.e("BusinessDataLoader", "Error parsing business categories JSON", e)
            emptyList()
        }
    }
}