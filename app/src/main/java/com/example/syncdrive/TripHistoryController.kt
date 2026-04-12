package com.example.syncdrive

import android.app.AlertDialog
import android.widget.Button

class TripHistoryController(
    private val btnTripHistory: Button
) {
    // Our in-memory database of trips
    private val tripLogs = mutableListOf<TripRecord>()

    fun setupHistoryButton() {
        btnTripHistory.setOnClickListener {
            showHistoryDialog()
        }
    }

    // Function to save a new trip
    fun addTripToHistory(distanceKm: Double) {
        // In a real app, we'd reverse-geocode the coordinates to get a street name.
        // For now, we simulate a destination name.
        val record = TripRecord(destinationName = "Custom Map Pin", distanceKm = distanceKm)
        tripLogs.add(record)
    }

    // Function to display the data
    private fun showHistoryDialog() {
        val context = btnTripHistory.context

        // Format the list of trips into a single string
        val historyText = if (tripLogs.isEmpty()) {
            "No autonomous trips recorded yet."
        } else {
            // Reverses the list so the newest trips show at the top
            tripLogs.reversed().joinToString(separator = "\n\n") {
                "📍 ${it.destinationName}\n📏 Distance: ${String.format("%.2f", it.distanceKm)} km\n🕒 ${it.getFormattedDate()}"
            }
        }

        // Build and show the pop-up
        AlertDialog.Builder(context)
            .setTitle("Autonomous Trip History")
            .setMessage(historyText)
            .setPositiveButton("Close", null)
            .setIcon(android.R.drawable.ic_menu_recent_history)
            .show()
    }
}