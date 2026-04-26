package com.example.syncdrive

import android.widget.Button
import android.widget.Toast
import android.app.AlertDialog

class ClimateController(
    private val btnClimateControl: Button,
    private val vehicleConnection: VehicleConnectionManager // <-- 1. ADD THIS
) {
    private var currentTemperature = 72

    fun setupClimateButton() {
        btnClimateControl.setOnClickListener { showClimateDialog() }
    }

    private fun showClimateDialog() {
        AlertDialog.Builder(btnClimateControl.context)
            .setTitle("Climate Control")
            .setMessage("Current Temperature: $currentTemperature°F")
            .setPositiveButton("Increase") { _, _ ->
                currentTemperature++
                sendClimateCommand() // <-- 2. TRIGGER COMMAND
                Toast.makeText(btnClimateControl.context, "Temp set to $currentTemperature°F", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Decrease") { _, _ ->
                currentTemperature--
                sendClimateCommand() // <-- 3. TRIGGER COMMAND
                Toast.makeText(btnClimateControl.context, "Temp set to $currentTemperature°F", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Close", null)
            .show()
    }

    // <-- 4. ADD THIS NEW FUNCTION -->
    private fun sendClimateCommand() {
        val payload = """{"state": "ON", "temp": $currentTemperature}"""
        vehicleConnection.sendCommandToCar("SET_CLIMATE", payload)
    }
}