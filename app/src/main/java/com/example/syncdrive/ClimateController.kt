package com.example.syncdrive

import android.widget.Button
import android.widget.Toast
import android.app.AlertDialog

class ClimateController(
    private val btnClimateControl: Button
) {
    private var currentTemperature = 72

    fun setupClimateButton() {
        btnClimateControl.setOnClickListener {
            showClimateDialog()
        }
    }

    private fun showClimateDialog() {
        AlertDialog.Builder(btnClimateControl.context)
            .setTitle("Climate Control")
            .setMessage("Current Temperature: $currentTemperature°F")
            .setPositiveButton("Increase") { _, _ ->
                currentTemperature++
                Toast.makeText(btnClimateControl.context, "Temperature set to $currentTemperature°F", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Decrease") { _, _ ->
                currentTemperature--
                Toast.makeText(btnClimateControl.context, "Temperature set to $currentTemperature°F", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Close", null)
            .show()
    }
}
