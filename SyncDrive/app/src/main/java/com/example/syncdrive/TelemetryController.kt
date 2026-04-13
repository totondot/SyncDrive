package com.example.syncdrive

import android.widget.TextView

class TelemetryController(
    private val speedTextView: TextView,
    private val headingTextView: TextView
) {

    // Controller method to update the View based on the Model
    fun updateTelemetryOnDashboard(telemetry: VehicleTelemetry) {
        speedTextView.text = "${telemetry.speedKmh} km/h"
        headingTextView.text = "Heading: ${telemetry.headingDirection}"
    }
}