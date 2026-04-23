package com.example.syncdrive

// Model: Holds the data for the vehicle's battery and range
data class VehicleStatus(
    var batteryPercentage: Int, // e.g., 85 for 85%
    var estimatedRangeKm: Double // e.g., 120.5 for kilometers left
)