package com.example.syncdrive

import android.app.AlertDialog
import android.content.Context
import android.widget.Button
import android.widget.Toast

class ClimateController(
    private val btnClimateControl: Button
) {
    private var climateState = ClimateState()

    fun setupClimateButton() {
        btnClimateControl.setOnClickListener {
            showClimateMenu()
        }
    }

    private fun showClimateMenu() {
        val context = btnClimateControl.context
        val powerStatus = if (climateState.isOn) "🟢 ON" else "🔴 OFF"

        val menuOptions = arrayOf(
            "Toggle Power ($powerStatus)",
            "Increase Temp (+)",
            "Decrease Temp (-)",
            "Change Mode (${climateState.mode})",
            "View Cabin Status"
        )

        AlertDialog.Builder(context)
            .setTitle("Climate Control (${climateState.targetTemperatureC}°C)")
            .setIcon(android.R.drawable.ic_menu_preferences)
            .setItems(menuOptions) { _, which ->
                when (which) {
                    0 -> togglePower(context)
                    1 -> changeTemperature(context, 1)
                    2 -> changeTemperature(context, -1)
                    3 -> changeMode(context)
                    4 -> showSummary(context)
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun togglePower(context: Context) {
        climateState.isOn = !climateState.isOn
        val state = if (climateState.isOn) "started" else "stopped"
        Toast.makeText(context, "Climate control $state.", Toast.LENGTH_SHORT).show()
        showClimateMenu() // Refresh menu
    }

    private fun changeTemperature(context: Context, delta: Int) {
        climateState.targetTemperatureC += delta

        // Clamp the temperature to realistic vehicle bounds (16°C to 30°C)
        if (climateState.targetTemperatureC > 30) climateState.targetTemperatureC = 30
        if (climateState.targetTemperatureC < 16) climateState.targetTemperatureC = 16

        climateState.isOn = true // Automatically turn the system on if user adjusts the temp
        Toast.makeText(context, "Target temp set to ${climateState.targetTemperatureC}°C", Toast.LENGTH_SHORT).show()
        showClimateMenu()
    }

    private fun changeMode(context: Context) {
        val modes = arrayOf("AUTO", "AC", "HEAT", "DEFROST")

        AlertDialog.Builder(context)
            .setTitle("Select HVAC Mode")
            .setItems(modes) { _, which ->
                climateState.mode = modes[which]
                climateState.isOn = true
                Toast.makeText(context, "Mode changed to ${modes[which]}", Toast.LENGTH_SHORT).show()
                showClimateMenu()
            }
            .show()
    }

    private fun showSummary(context: Context) {
        val status = if (climateState.isOn) "ACTIVE" else "OFF"

        val summaryText = """
            ❄️ System Power: $status
            🌡️ Target Temp: ${climateState.targetTemperatureC}°C
            🌬️ Current Mode: ${climateState.mode}
            
            The cabin is preconditioning to your preferences.
        """.trimIndent()

        AlertDialog.Builder(context)
            .setTitle("HVAC Status")
            .setMessage(summaryText)
            .setPositiveButton("OK", null)
            .show()
    }
}