package com.example.syncdrive

// Model: Represents the destination set by the user
data class NavigationTarget(
    val latitude: Double,
    val longitude: Double,
    val label: String = "Selected Destination"
)