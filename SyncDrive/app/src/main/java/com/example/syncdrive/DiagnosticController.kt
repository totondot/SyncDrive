package com.example.syncdrive

import android.view.View
import android.widget.TextView

class DiagnosticController(
    private val alertBanner: View,
    private val alertTextView: TextView
) {

    // Controller logic to evaluate safe parameters
    fun updateDiagnostics(diagnostics: VehicleDiagnostics) {
        val alerts = mutableListOf<String>()

        // Check engine temperature
        if (diagnostics.engineTempCelsius > 105.0) {
            alerts.add("HIGH ENGINE TEMP: ${diagnostics.engineTempCelsius}°C")
        }

        // Check tire pressure
        if (diagnostics.tirePressurePsi.any { it < 30.0 }) {
            alerts.add("LOW TIRE PRESSURE")
        }

        // Check sensor status
        if (!diagnostics.sensorsOnline) {
            alerts.add("CRITICAL: SENSORS OFFLINE")
        }

        // Update the View
        if (alerts.isNotEmpty()) {
            alertBanner.visibility = View.VISIBLE
            // Join multiple alerts together if they happen at the same time
            alertTextView.text = alerts.joinToString(" | ")
        } else {
            alertBanner.visibility = View.GONE
        }
    }
}