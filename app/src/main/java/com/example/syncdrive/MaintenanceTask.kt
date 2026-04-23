package com.example.syncdrive

// Model: Represents a scheduled maintenance service
data class MaintenanceTask(
    val serviceName: String,
    val intervalKm: Double,
    val nextDueMileageKm: Double,
    var isNotified: Boolean = false // Prevents the app from spamming the alert over and over
)