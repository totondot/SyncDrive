package com.example.syncdrive

import android.graphics.Color
import android.widget.Button
import android.widget.Toast

class DoorController(
    private val btnDoorLock: Button,
    private val vehicleConnection: VehicleConnectionManager // <-- 1. ADD THIS
) {
    private var isLocked = false

    fun setupDoorControls() {
        updateUI()

        btnDoorLock.setOnClickListener {
            isLocked = !isLocked
            val command = DoorCommand(lockDoors = isLocked)
            updateUI()

            // <-- 2. ADD THIS TO SEND TO PYTHON -->
            val payload = """{"locked": $isLocked}"""
            vehicleConnection.sendCommandToCar("TOGGLE_DOORS", payload)
            // ------------------------------------

            val statusText = if (command.lockDoors) "Doors Locked & Secured" else "Doors Unlocked"
            Toast.makeText(btnDoorLock.context, statusText, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        if (isLocked) {
            btnDoorLock.text = "UNLOCK DOORS"
            btnDoorLock.setBackgroundColor(Color.parseColor("#1976D2"))
        } else {
            btnDoorLock.text = "LOCK DOORS"
            btnDoorLock.setBackgroundColor(Color.parseColor("#424242"))
        }
    }
}