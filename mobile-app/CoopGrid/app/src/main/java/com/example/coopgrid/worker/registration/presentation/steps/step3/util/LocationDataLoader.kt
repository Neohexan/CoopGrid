package com.example.coopgrid.worker.registration.presentation.steps.step3.util

import android.content.Context
import com.example.coopgrid.R
import com.example.coopgrid.worker.registration.presentation.steps.step3.model.DistrictLocationData
import kotlinx.serialization.json.Json


object LocationDataLoader {

    // Extra keys se crash na ho iske liye ignoreUnknownKeys
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun loadLocationsFromRaw(context: Context): List<DistrictLocationData> {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.india_locations)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            jsonParser.decodeFromString(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}