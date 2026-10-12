package com.example.coopgrid.data.utils


import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationClient: FusedLocationProviderClient // 👈 Hilt ise khud inject karega
) : ViewModel() {

    @SuppressLint("MissingPermission")
    fun getUserLocation(onLocationReceived: (Double, Double) -> Unit) {
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationReceived(location.latitude, location.longitude)
                } else {
                    // Fallback: Agar lastLocation null aaye, toh Fresh Location fetch karein
                    locationClient.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { freshLocation ->
                        if (freshLocation != null) {
                            onLocationReceived(freshLocation.latitude, freshLocation.longitude)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationViewModel", "Error fetching location", exception)
            }
    }
}
