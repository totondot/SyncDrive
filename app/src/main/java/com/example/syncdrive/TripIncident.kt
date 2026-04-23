package com.example.syncdrive

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Model: Represents an edge-case event during an autonomous trip
data class TripIncident(
    val type: IncidentType,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class IncidentType {
        INTERVENTION,
        HARD_STOP,
        UNKNOWN_SIGN
    }

    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}