package com.example.syncdrive

import org.osmdroid.util.GeoPoint

// Model: Represents the remote summon command sent to the car
data class SummonRequest(
    val userLocation: GeoPoint,
    val isActive: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)