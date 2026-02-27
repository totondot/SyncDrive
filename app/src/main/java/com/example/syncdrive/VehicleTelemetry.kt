package com.example.syncdrive

// Model: Holds the data for the vehicle's real-time movement
data class VehicleTelemetry(
    var speedKmh: Double,
    var headingDegrees: Float, // 0.0 to 360.0
    var headingDirection: String // e.g., "N", "NE", "SW"
)