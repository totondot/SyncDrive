package com.example.syncdrive

import android.app.AlertDialog
import android.content.Context
import android.widget.Button
import android.widget.Toast

class RestrictionsController(
    private val btnRestrictions: Button
) {
    private var currentRestrictions = DrivingRestrictions()

    fun setupRestrictionsButton() {
        btnRestrictions.setOnClickListener {
            showMainMenu()
        }
    }

    private fun showMainMenu() {
        val context = btnRestrictions.context
        val statusText = if (currentRestrictions.isEnabled) "🟢 ACTIVE" else "🔴 INACTIVE"

        val menuOptions = arrayOf(
            "Toggle Status ($statusText)",
            "Set Max Speed (${currentRestrictions.maxSpeedKmh} km/h)",
            "Set Geofence Radius (${currentRestrictions.geofenceRadiusKm} km)",
            "View Current Summary"
        )

        AlertDialog.Builder(context)
            .setTitle("Driving Restrictions")
            .setIcon(android.R.drawable.ic_lock_lock)
            .setItems(menuOptions) { _, which ->
                when (which) {
                    0 -> toggleStatus(context)
                    1 -> showSpeedMenu(context)
                    2 -> showGeofenceMenu(context)
                    3 -> showSummary(context)
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun toggleStatus(context: Context) {
        currentRestrictions.isEnabled = !currentRestrictions.isEnabled
        val state = if (currentRestrictions.isEnabled) "Enabled" else "Disabled"
        Toast.makeText(context, "Restrictions $state", Toast.LENGTH_SHORT).show()
        showMainMenu() // Refresh menu
    }

    private fun showSpeedMenu(context: Context) {
        val speeds = arrayOf("40 km/h (City)", "60 km/h (Suburbs)", "80 km/h (Rural)", "120 km/h (Highway)")
        val speedValues = intArrayOf(40, 60, 80, 120)

        AlertDialog.Builder(context)
            .setTitle("Select Max Speed")
            .setItems(speeds) { _, which ->
                currentRestrictions.maxSpeedKmh = speedValues[which]
                currentRestrictions.isEnabled = true // Auto-enable if setting a new limit
                Toast.makeText(context, "Speed limit locked to ${speedValues[which]} km/h", Toast.LENGTH_SHORT).show()
                showMainMenu()
            }
            .show()
    }

    private fun showGeofenceMenu(context: Context) {
        val radii = arrayOf("5 km (Neighborhood)", "15 km (City Limits)", "50 km (Metro Area)", "Unlimited")
        val radiiValues = intArrayOf(5, 15, 50, 9999)

        AlertDialog.Builder(context)
            .setTitle("Select Geofence Boundary")
            .setItems(radii) { _, which ->
                currentRestrictions.geofenceRadiusKm = radiiValues[which]
                currentRestrictions.isEnabled = true
                Toast.makeText(context, "Geofence locked to ${radiiValues[which]} km", Toast.LENGTH_SHORT).show()
                showMainMenu()
            }
            .show()
    }

    private fun showSummary(context: Context) {
        val status = if (currentRestrictions.isEnabled) "ACTIVE" else "INACTIVE"
        val radiusDisplay = if (currentRestrictions.geofenceRadiusKm > 1000) "Unlimited" else "${currentRestrictions.geofenceRadiusKm} km"

        val summaryText = """
            🛡️ Status: $status
            
            🏎️ Max Speed Limit: ${currentRestrictions.maxSpeedKmh} km/h
            📍 Geofence Radius: $radiusDisplay
            
            If the vehicle breaches these limits, autonomous mode will safely pull over and alert the owner.
        """.trimIndent()

        AlertDialog.Builder(context)
            .setTitle("Restriction Summary")
            .setMessage(summaryText)
            .setPositiveButton("OK", null)
            .show()
    }
}