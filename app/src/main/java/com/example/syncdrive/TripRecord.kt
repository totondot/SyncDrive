package com.example.syncdrive

import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Model: Represents a single completed autonomous trip (UPGRADED FOR FEATURE 12)
data class TripRecord(
    val destinationName: String,
    val distanceKm: Double,
    val durationMinutes: Int,            // NEW: How long the trip took
    val routePoints: List<GeoPoint>,     // NEW: The exact path taken
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}