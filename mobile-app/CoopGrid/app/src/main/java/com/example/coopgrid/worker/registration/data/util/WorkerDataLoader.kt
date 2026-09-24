package com.example.coopgrid.worker.registration.data.util

import android.content.Context
import com.example.coopgrid.R
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.JobCategory
import kotlinx.serialization.json.Json

object WorkerDataLoader {
    fun loadWorkerCategories(context: Context): List<JobCategory> {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.category_coopgrid)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            Json.decodeFromString<List<JobCategory>>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}