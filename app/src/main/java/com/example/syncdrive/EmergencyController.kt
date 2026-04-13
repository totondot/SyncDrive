package com.example.syncdrive

import android.widget.Button
import android.widget.Toast
import android.app.AlertDialog

class EmergencyController(
    private val btnEmergencyStop: Button,
    private val routeController: RouteController // Needed to cancel active routes
) {
    fun setupEmergencySystem() {
        btnEmergencyStop.setOnClickListener {
            // 1. Show a confirmation dialog to prevent accidental presses
            AlertDialog.Builder(btnEmergencyStop.context)
                .setTitle("EMERGENCY STOP")
                .setMessage("Are you sure you want to halt the vehicle? This will cancel all active routes.")
                .setPositiveButton("HALT VEHICLE") { _, _ ->
                    executeEmergencyStop()
                }
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    private fun executeEmergencyStop() {
        // 2. Create the Command Model (Simulating network payload to car brakes)
        val command = EmergencyCommand()

        // 3. Clear the active route off the map
        routeController.clearRoute()

        // 4. Visual Feedback
        Toast.makeText(
            btnEmergencyStop.context,
            "CRITICAL: Vehicle Halting!",
            Toast.LENGTH_LONG
        ).show()
    }
}