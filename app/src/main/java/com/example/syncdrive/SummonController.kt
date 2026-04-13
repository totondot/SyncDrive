package com.example.syncdrive

import android.widget.Button
import android.widget.Toast
import org.osmdroid.util.GeoPoint

class SummonController(
    private val btnSummon: Button,
    private val routeController: RouteController // Reusing Feature 7!
) {
    private var isSummoning = false

    fun setupSummonButton(carLocation: GeoPoint, userLocation: GeoPoint) {
        btnSummon.setOnClickListener {
            if (!isSummoning) {
                // Activate Summon
                isSummoning = true
                btnSummon.text = "CANCEL SUMMON"
                btnSummon.setBackgroundColor(android.graphics.Color.parseColor("#388E3C")) // Change to Green

                // Draw route from the CAR to the USER
                routeController.calculateAndDrawRoute(carLocation, userLocation)

                Toast.makeText(btnSummon.context, "Summon Command Sent!", Toast.LENGTH_SHORT).show()
            } else {
                // Cancel Summon
                isSummoning = false
                btnSummon.text = "SUMMON CAR"
                btnSummon.setBackgroundColor(android.graphics.Color.parseColor("#D32F2F")) // Back to Red

                Toast.makeText(btnSummon.context, "Summon Canceled.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}