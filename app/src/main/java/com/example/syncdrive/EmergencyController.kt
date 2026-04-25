package com.example.syncdrive

import android.widget.Button
import android.widget.Toast
import android.app.AlertDialog
import org.json.JSONObject


class EmergencyController(
    private val btnEmergencyStop: Button,
    private val routeController: RouteController,
    private val vehicleConnection: VehicleConnectionManager // PASSED IN NOW
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
        val command = EmergencyCommand()
        routeController.clearRoute()

        val commandJson = JSONObject().apply {
            put("emergencyHalt", command.emergencyHalt)
            put("reason", command.reason)
            put("timestamp", command.timestamp)
        }.toString()

        // NEW: SEND THE REAL-TIME PACKET TO THE CAR'S BRAKES
        vehicleConnection.sendCommandToCar("EMERGENCY_HALT", commandJson)

        Toast.makeText(btnEmergencyStop.context, "CRITICAL: Vehicle Halting!", Toast.LENGTH_LONG).show()
    }
}
