package com.example.syncdrive

// Model: Holds the data for a road sign detected by the car's sensors
data class DetectedSign(
    val signName: String, // e.g., "Speed Limit 60", "Stop Sign"
    val timestampMs: Long
)