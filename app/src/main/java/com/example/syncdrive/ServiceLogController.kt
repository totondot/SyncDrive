package com.example.syncdrive

import android.app.AlertDialog
import android.widget.Button

class ServiceLogController(
    private val btnServiceLog: Button
) {
    private val serviceHistory = mutableListOf<MaintenanceRecord>()

    fun setupServiceLogButton() {
        btnServiceLog.setOnClickListener {
            showLogDialog()
        }
    }

    // Allows the system to log a completed service
    fun addRecord(serviceType: String, mileageKm: Double, notes: String, customTimestamp: Long = System.currentTimeMillis()) {
        serviceHistory.add(MaintenanceRecord(serviceType, mileageKm, notes, customTimestamp))
    }

    private fun showLogDialog() {
        val context = btnServiceLog.context

        if (serviceHistory.isEmpty()) {
            AlertDialog.Builder(context)
                .setTitle("Maintenance & Calibration Log")
                .setMessage("No past service records found.")
                .setPositiveButton("Close", null)
                .show()
            return
        }

        // Sort by newest first, then format with emojis for readability
        val reportText = serviceHistory.sortedByDescending { it.timestamp }
            .joinToString(separator = "\n\n") {
                "🔧 ${it.serviceType}\n" +
                        "📅 ${it.getFormattedDate()} | 🛣️ ${String.format("%.0f", it.mileageKm)} km\n" +
                        "📝 ${it.notes}"
            }

        AlertDialog.Builder(context)
            .setTitle("Past Service Records")
            .setMessage(reportText)
            .setPositiveButton("Close", null)
            .show()
    }
}