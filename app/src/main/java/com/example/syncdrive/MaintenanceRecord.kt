package com.example.syncdrive

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Model: Represents a completed maintenance or sensor calibration event
data class MaintenanceRecord(
    val serviceType: String,
    val mileageKm: Double,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}