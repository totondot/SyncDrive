package com.example.syncdrive

// Model: Represents an immediate halt command sent to the car's braking system
data class EmergencyCommand(
    val emergencyHalt: Boolean = true,
    val reason: String = "User Triggered Override",
    val timestamp: Long = System.currentTimeMillis()
)