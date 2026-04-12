package com.example.syncdrive

import android.widget.TextView

class StatusController(
    private val batteryTextView: TextView,
    private val rangeTextView: TextView
) {

    // Controller method to update the View based on the Model
    fun updateVehicleStatusOnDashboard(status: VehicleStatus) {
        // Update the text fields with the data from the model
        batteryTextView.text = "Battery: ${status.batteryPercentage}%"
        rangeTextView.text = "Range: ${status.estimatedRangeKm} km"

        // Optional: Change color to red if battery is very low (below 20%)
        if (status.batteryPercentage < 20) {
            batteryTextView.setTextColor(android.graphics.Color.RED)
        } else {
            batteryTextView.setTextColor(android.graphics.Color.WHITE)
        }
    }
}