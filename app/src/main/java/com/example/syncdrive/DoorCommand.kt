package com.example.syncdrive

// Model: Represents the door lock/unlock command sent to the car
data class DoorCommand(
    val lockDoors: Boolean, // True = Lock, False = Unlock
    val timestamp: Long = System.currentTimeMillis()
)