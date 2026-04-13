package com.example.syncdrive

import android.graphics.Color
import android.widget.Button
import android.widget.Toast

class DoorController(
    private val btnDoorLock: Button
) {
    // We assume the car is initially unlocked when the app opens
    private var isLocked = false

    fun setupDoorControls() {
        // Initialize the UI to match the starting state
        updateUI()

        btnDoorLock.setOnClickListener {
            // 1. Toggle the state
            isLocked = !isLocked

            // 2. Create the Model (Simulating network payload)
            val command = DoorCommand(lockDoors = isLocked)

            // 3. Update the View
            updateUI()

            // 4. Show user feedback
            val statusText = if (command.lockDoors) "Doors Locked & Secured" else "Doors Unlocked"
            Toast.makeText(btnDoorLock.context, statusText, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        if (isLocked) {
            btnDoorLock.text = "UNLOCK DOORS"
            btnDoorLock.setBackgroundColor(Color.parseColor("#1976D2")) // Secure Blue
        } else {
            btnDoorLock.text = "LOCK DOORS"
            btnDoorLock.setBackgroundColor(Color.parseColor("#424242")) // Neutral Dark Grey
        }
    }
}