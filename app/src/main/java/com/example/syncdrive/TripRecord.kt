package com.example.syncdrive

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Model: Represents a single completed autonomous trip
data class TripRecord(
    val destinationName: String,
    val distanceKm: Double,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Helper function to turn the timestamp into a readable date/time
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}