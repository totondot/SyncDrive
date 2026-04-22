package com.example.syncdrive

import android.app.AlertDialog
import android.widget.Button

class EfficiencyController(
    private val btnEfficiency: Button
) {
    fun setupEfficiencyButton() {
        btnEfficiency.setOnClickListener {
            showTimePeriodSelector()
        }
    }

    private fun showTimePeriodSelector() {
        val periods = arrayOf("Last 24 Hours", "Last 7 Days", "Last 30 Days", "All Time")

        AlertDialog.Builder(btnEfficiency.context)
            .setTitle("Select Time Period")
            .setIcon(android.R.drawable.ic_menu_sort_by_size)
            .setItems(periods) { _, which ->
                // User clicked an option, calculate and show the stats
                val selectedPeriod = periods[which]
                calculateAndShowStats(selectedPeriod, which)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun calculateAndShowStats(periodName: String, periodIndex: Int) {
        // In a production app, we would query a database here.
        // For now, we simulate realistic EV data based on the selected timeframe.
        val baseDistance = 45.0
        val baseEnergy = 7.2
        val multiplier = when (periodIndex) {
            0 -> 1.0    // 24 Hours
            1 -> 5.5    // 7 Days
            2 -> 22.0   // 30 Days
            else -> 150.0 // All Time
        }

        val totalDist = baseDistance * multiplier
        val totalEnergy = baseEnergy * multiplier
        // EV Efficiency formula: (Total kWh * 1000) / Total km
        val avgWhKm = ((totalEnergy * 1000) / totalDist).toInt()

        val stats = EfficiencyStats(
            timePeriodName = periodName,
            totalDistanceKm = totalDist,
            energyUsedKWh = totalEnergy,
            averageWhPerKm = avgWhKm
        )

        showStatsReport(stats)
    }

    private fun showStatsReport(stats: EfficiencyStats) {
        val reportText = "📅 Period: ${stats.timePeriodName}\n\n" +
                "🛣️ Distance Driven: ${String.format("%.1f", stats.totalDistanceKm)} km\n" +
                "⚡ Energy Consumed: ${String.format("%.1f", stats.energyUsedKWh)} kWh\n\n" +
                "🔋 Avg Efficiency: ${stats.averageWhPerKm} Wh/km"

        AlertDialog.Builder(btnEfficiency.context)
            .setTitle("Efficiency Report")
            .setMessage(reportText)
            .setPositiveButton("Close", null)
            .show()
    }
}