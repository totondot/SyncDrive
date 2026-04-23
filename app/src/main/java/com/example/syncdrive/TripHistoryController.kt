package com.example.syncdrive

import android.app.AlertDialog
import android.widget.Button
import org.osmdroid.util.GeoPoint

class TripHistoryController(
    private val btnTripHistory: Button,
    private val onTripSelected: (TripRecord) -> Unit // Callback for Feature 12
) {
    private val tripLogs = mutableListOf<TripRecord>()

    fun getAllTrips(): List<TripRecord> {
        return tripLogs.toList()
    }
    fun setupHistoryButton() {
        btnTripHistory.setOnClickListener {
            showHistoryDialog()
        }
    }

    // UPGRADED: Now accepts duration and route points
    fun addTripToHistory(distanceKm: Double, durationMins: Int, points: List<GeoPoint>) {
        val record = TripRecord(
            destinationName = "Custom Map Pin",
            distanceKm = distanceKm,
            durationMinutes = durationMins,
            routePoints = points
        )
        tripLogs.add(record)
    }

    private fun showHistoryDialog() {
        val context = btnTripHistory.context

        if (tripLogs.isEmpty()) {
            AlertDialog.Builder(context)
                .setTitle("Autonomous Trip History")
                .setMessage("No autonomous trips recorded yet.")
                .setPositiveButton("Close", null)
                .show()
            return
        }

        // Create an array of strings for the clickable list
        val reversedLogs = tripLogs.reversed()
        val tripStrings = reversedLogs.map {
            "📍 ${it.destinationName} (${String.format("%.1f", it.distanceKm)}km) - ${it.getFormattedDate()}"
        }.toTypedArray()

        AlertDialog.Builder(context)
            .setTitle("Select a Trip to View Route")
            .setItems(tripStrings) { _, which ->
                // When a user clicks an item, trigger the callback
                val selectedTrip = reversedLogs[which]
                onTripSelected(selectedTrip)
            }
            .setNegativeButton("Close", null)
            .show()
    }
}