package com.example.syncdrive

// Model: Represents the active limitations placed on the autonomous system
data class DrivingRestrictions(
    var isEnabled: Boolean = false,
    var maxSpeedKmh: Int = 120,      // Default max highway speed
    var geofenceRadiusKm: Int = 50   // Default driving radius in kilometers
)