package com.example.syncdrive

import android.app.AlertDialog
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import java.io.File
import java.io.FileWriter

class ExportController(
    private val btnExportData: Button,
    private val historyController: TripHistoryController
) {
    fun setupExportButton() {
        btnExportData.setOnClickListener {
            exportToCSV()
        }
    }

    private fun exportToCSV() {
        val trips = historyController.getAllTrips()
        val context = btnExportData.context

        if (trips.isEmpty()) {
            Toast.makeText(context, "No trip data available to export.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Get the standard Downloads directory for this app
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val fileName = "SyncDrive_History_${System.currentTimeMillis()}.csv"
            val file = File(downloadsDir, fileName)

            val writer = FileWriter(file)

            // 1. Write the CSV Headers
            writer.append("Date,Destination,Distance_km,Duration_mins\n")

            // 2. Write the Data Rows
            for (trip in trips) {
                // We wrap strings in quotes to ensure commas in the date/name don't break the CSV columns
                writer.append("\"${trip.getFormattedDate()}\",")
                writer.append("\"${trip.destinationName}\",")
                writer.append("${String.format("%.2f", trip.distanceKm)},")
                writer.append("${trip.durationMinutes}\n")
            }

            writer.flush()
            writer.close()

            // 3. Show Success Dialog
            AlertDialog.Builder(context)
                .setTitle("Export Successful")
                .setMessage("Your trip history has been saved as a CSV file.\n\n📍 Location:\nApp-Specific Downloads Folder\n\n📄 File:\n$fileName")
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_menu_save)
                .show()

        } catch (e: Exception) {
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}