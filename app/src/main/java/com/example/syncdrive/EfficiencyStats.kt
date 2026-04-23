package com.example.syncdrive

// Model: Represents the vehicle's energy consumption over a specific time period
data class EfficiencyStats(
    val timePeriodName: String,
    val totalDistanceKm: Double,
    val energyUsedKWh: Double,
    val averageWhPerKm: Int // Standard EV metric (Watt-hours per kilometer)
)