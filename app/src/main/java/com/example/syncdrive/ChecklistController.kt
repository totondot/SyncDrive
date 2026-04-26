package com.example.syncdrive

import android.app.AlertDialog
import android.widget.Button
import android.widget.Toast

class ChecklistController(
    private val btnSystemCheck: Button,
    private val vehicleConnection: VehicleConnectionManager // <-- 1. ADD THIS
) {
    var isSystemEngaged: Boolean = false
        private set

    fun setupChecklistButton() {
        btnSystemCheck.setOnClickListener { runPreFlightCheck() }
    }

    private fun runPreFlightCheck() {
        // Simulating a diagnostic ping to the vehicle's hardware
        val checklist = listOf(
            ChecklistItem("LiDAR Array", "✅ ONLINE"),
            ChecklistItem("Stereo Cameras", "✅ CLEAN"),
            ChecklistItem("Ultrasonic Sensors", "✅ ACTIVE"),
            ChecklistItem("Braking Actuators", "✅ RESPONSIVE"),
            ChecklistItem("Network Connection", "✅ SECURE")
        )

        val reportText = checklist.joinToString(separator = "\n") {
            "• ${it.componentName} ... ${it.status}"
        }

        AlertDialog.Builder(btnSystemCheck.context)
            .setTitle("Pre-Ride Safety Check")
            .setMessage("Automated diagnostics complete...\nAll systems nominal. Do you acknowledge this check to engage the autonomous driving system?")
            .setPositiveButton("Acknowledge & Engage") { _, _ ->
                isSystemEngaged = true

                vehicleConnection.sendCommandToCar("RUN_DIAGNOSTICS", "{}")

                Toast.makeText(btnSystemCheck.context, "Autonomous System ENGAGED", Toast.LENGTH_SHORT).show()
                btnSystemCheck.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
            }
            .setNegativeButton("Cancel") { _, _ ->
                isSystemEngaged = false
            }
            .setCancelable(false)
            .show()
    }
}