package com.example.syncdrive

import android.widget.Button
import android.widget.Toast
import org.osmdroid.util.GeoPoint

class SummonController(
    private val btnSummon: Button,
    private val routeController: RouteController,
    private val vehicleConnection: VehicleConnectionManager // <-- 1. ADD THIS
) {
    private var isSummoning = false

    fun setupSummonButton(carLocation: GeoPoint, userLocation: GeoPoint) {
        btnSummon.setOnClickListener {
            if (!isSummoning) {
                isSummoning = true
                btnSummon.text = "CANCEL SUMMON"
                btnSummon.setBackgroundColor(android.graphics.Color.parseColor("#388E3C"))
                routeController.calculateAndDrawRoute(carLocation, userLocation)

                // <-- 2. START SUMMON COMMAND -->
                vehicleConnection.sendCommandToCar("START_SUMMON", "{}")
                // -------------------------------

                Toast.makeText(btnSummon.context, "Summon Command Sent!", Toast.LENGTH_SHORT).show()
            } else {
                isSummoning = false
                btnSummon.text = "SUMMON CAR"
                btnSummon.setBackgroundColor(android.graphics.Color.parseColor("#D32F2F"))

                // <-- 3. CANCEL SUMMON COMMAND -->
                vehicleConnection.sendCommandToCar("CANCEL_SUMMON", "{}")
                // --------------------------------

                Toast.makeText(btnSummon.context, "Summon Canceled.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}