package com.example.syncdrive

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

class TripDetailController(
    private val panel: View,
    private val tvStats: TextView,
    private val btnClose: Button,
    private val mapView: MapView
) {
    private var historicalRouteLine: Polyline? = null

    init {
        // Hide the panel and clear the map when the user clicks close
        btnClose.setOnClickListener {
            closeDetails()
        }
    }

    fun showTripDetails(trip: TripRecord) {
        // 1. Show the panel and update the text
        panel.visibility = View.VISIBLE
        tvStats.text = "📍 ${trip.destinationName}\n" +
                "📏 Distance: ${String.format("%.2f", trip.distanceKm)} km\n" +
                "⏱ Duration: ${trip.durationMinutes} mins\n" +
                "🕒 ${trip.getFormattedDate()}"

        // 2. Draw the historical route on the map
        drawHistoricalRoute(trip.routePoints)
    }

    private fun drawHistoricalRoute(points: List<GeoPoint>) {
        // Clear any previously viewed historical route
        historicalRouteLine?.let { mapView.overlays.remove(it) }

        historicalRouteLine = Polyline(mapView).apply {
            setPoints(points)
            outlinePaint.color = Color.parseColor("#9C27B0") // Purple for history
            outlinePaint.strokeWidth = 10f
        }

        mapView.overlays.add(historicalRouteLine)
        mapView.invalidate()
    }

    private fun closeDetails() {
        panel.visibility = View.GONE
        historicalRouteLine?.let {
            mapView.overlays.remove(it)
            historicalRouteLine = null
        }
        mapView.invalidate()
    }
}