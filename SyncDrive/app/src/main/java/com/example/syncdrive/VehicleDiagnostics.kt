package com.example.syncdrive

// Model: Holds the hardware diagnostic data of the vehicle
data class VehicleDiagnostics(
    var engineTempCelsius: Double, // Safe range: 85 - 105
    var tirePressurePsi: List<Double>, // 4 tires, Safe range: > 30.0
    var sensorsOnline: Boolean // Must be true
)