package com.example.syncdrive

import android.app.AlertDialog
import android.widget.Button
import android.widget.Toast

class MaintenanceController(
    private val btnMaintenance: Button
) {
    private val scheduledTasks = mutableListOf<MaintenanceTask>()

    // We simulate the car's current total mileage (odometer)
    private var currentOdometerKm: Double = 50000.0

    fun setupMaintenanceButton() {
        btnMaintenance.setOnClickListener {
            showSchedulingDialog()
        }
    }

    private fun showSchedulingDialog() {
        val services = arrayOf(
            "Tire Rotation (Every 10,000 km)",
            "Brake Fluid Check (Every 40,000 km)",
            "Battery Health Diagnostic (Every 20,000 km)"
        )

        val intervals = arrayOf(10000.0, 40000.0, 20000.0)

        AlertDialog.Builder(btnMaintenance.context)
            .setTitle("Schedule Auto-Maintenance")
            .setIcon(android.R.drawable.ic_menu_agenda)
            .setItems(services) { _, which ->
                scheduleTask(services[which].substringBefore(" ("), intervals[which])
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun scheduleTask(name: String, interval: Double) {
        val dueMileage = currentOdometerKm + interval
        val newTask = MaintenanceTask(name, interval, dueMileage)
        scheduledTasks.add(newTask)

        Toast.makeText(
            btnMaintenance.context,
            "✅ $name scheduled at ${String.format("%.0f", dueMileage)} km",
            Toast.LENGTH_LONG
        ).show()
    }

    // This function will be called repeatedly by the car's live telemetry stream
    fun updateOdometerAndCheckTasks(newOdometerKm: Double) {
        currentOdometerKm = newOdometerKm

        for (task in scheduledTasks) {
            // If the car's mileage has surpassed the due date, and we haven't notified yet
            if (currentOdometerKm >= task.nextDueMileageKm && !task.isNotified) {
                triggerMaintenanceAlert(task)
                task.isNotified = true // Mark as notified so we don't spam the user
            }
        }
    }

    private fun triggerMaintenanceAlert(task: MaintenanceTask) {
        AlertDialog.Builder(btnMaintenance.context)
            .setTitle("🔧 Maintenance Due!")
            .setMessage("Your vehicle has reached ${String.format("%.0f", currentOdometerKm)} km.\n\nIt is time for your scheduled:\n${task.serviceName}")
            .setPositiveButton("Acknowledge", null)
            .show()
    }
}