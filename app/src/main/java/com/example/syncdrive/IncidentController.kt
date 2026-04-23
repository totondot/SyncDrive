package com.example.syncdrive

import android.app.AlertDialog
import android.widget.Button

class IncidentController(
    private val btnIncidentReport: Button
) {
    private val incidentLog = mutableListOf<TripIncident>()

    fun setupIncidentButton() {
        btnIncidentReport.setOnClickListener {
            showIncidentReport()
        }
    }

    // Function to allow other parts of the app to log an incident
    fun logIncident(type: TripIncident.IncidentType, description: String) {
        incidentLog.add(TripIncident(type, description))
    }

    private fun showIncidentReport() {
        val context = btnIncidentReport.context

        if (incidentLog.isEmpty()) {
            AlertDialog.Builder(context)
                .setTitle("Trip Incident Report")
                .setMessage("✅ Zero interventions or unrecognized signs. Flawless trip!")
                .setPositiveButton("Close", null)
                .show()
            return
        }

        // Format the incidents with emojis for scannability
        val reportText = incidentLog.reversed().joinToString(separator = "\n\n") {
            val icon = when (it.type) {
                TripIncident.IncidentType.INTERVENTION -> "⚠️ USER OVERRIDE"
                TripIncident.IncidentType.HARD_STOP -> "🛑 HARD STOP"
                TripIncident.IncidentType.UNKNOWN_SIGN -> "❓ UNKNOWN SIGN"
            }
            "$icon\n└ ${it.description}\n└ 🕒 ${it.getFormattedTime()}"
        }

        AlertDialog.Builder(context)
            .setTitle("Trip Incident Report")
            .setMessage(reportText)
            .setPositiveButton("Acknowledge", null)
            .show()
    }
}