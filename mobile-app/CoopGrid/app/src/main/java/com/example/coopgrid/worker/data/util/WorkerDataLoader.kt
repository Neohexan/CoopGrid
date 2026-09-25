package com.example.coopgrid.worker.data.util

import android.content.Context
import com.example.coopgrid.R
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.JobCategory
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryCategory
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriBusinessCategory
import kotlinx.serialization.json.Json

object WorkerJsonReader {
    // 1. Single reusable Json instance (Fixes "Redundant creation of Json format" warning)
    // @PublishedApi enables inline functions to access internal property safely
    @PublishedApi
    internal val jsonInstance = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun loadWorkerSkillsCategoriesFromAssets(context: Context): List<JobCategory> {
        return loadFromAssets<List<JobCategory>>(context = context,
            filePath ="worker/category_skills.json") ?: emptyList()
    }

    fun loadAgriCategoriesFromAssets(context: Context): List<AgriBusinessCategory> {
        return loadFromAssets<List<AgriBusinessCategory>>(context = context,
            filePath ="worker/agri_categories.json") ?: emptyList()
    }

    fun loadMachineryCategoriesFromAssets(context: Context): List<MachineryCategory> {
        return loadFromAssets<List<MachineryCategory>>(context = context,
            filePath ="worker/machinery_categories.json") ?: emptyList()
    }

    inline fun <reified T> loadFromAssets(context: Context, filePath: String): T? {
        return try {
            val jsonString = context.assets.open(filePath).bufferedReader().use { it.readText() }
            jsonInstance.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}