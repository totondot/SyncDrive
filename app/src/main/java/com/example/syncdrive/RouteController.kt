package com.example.syncdrive

import android.graphics.Color
import android.widget.TextView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

class RouteController(
    private val mapView: MapView,
    private val tvRouteInfo: TextView
) {
    private var routeLine: Polyline? = null

    // Assumed average speed of the autonomous car in km/h
    private val AVERAGE_SPEED_KMH = 40.0

    fun calculateAndDrawRoute(carLocation: GeoPoint, destination: GeoPoint) {
        // 1. Calculate actual distance in meters, then convert to kilometers
        val distanceMeters = carLocation.distanceToAsDouble(destination)
        val distanceKm = distanceMeters / 1000.0

        // 2. Calculate ETA in minutes based on average speed
        val timeInHours = distanceKm / AVERAGE_SPEED_KMH
        val etaMinutes = (timeInHours * 60).toInt()

        // 3. Create the RouteInfo Model
        val routeInfo = RouteInfo(distanceKm, etaMinutes)

        // 4. Update the UI
        tvRouteInfo.text = "Dist: ${String.format("%.2f", routeInfo.distanceKm)} km | ETA: ${routeInfo.etaMinutes} mins"

        // 5. Draw the autonomous path on the map
        drawPathOnMap(carLocation, destination)
    }

    private fun drawPathOnMap(start: GeoPoint, end: GeoPoint) {
        // Remove previous route if the user picks a new destination
        routeLine?.let { mapView.overlays.remove(it) }

        // Create a visual line between the car and the destination
        routeLine = Polyline(mapView).apply {
            addPoint(start)
            addPoint(end)
            outlinePaint.color = Color.parseColor("#0055FF") // A nice autonomous blue
            outlinePaint.strokeWidth = 12f // Thick enough to see clearly
        }

        mapView.overlays.add(routeLine)
        mapView.invalidate() // Force map to refresh
    }
    // Add this to RouteController.kt for Feature 10
    fun clearRoute() {
        routeLine?.let {
            mapView.overlays.remove(it)
            routeLine = null
        }
        tvRouteInfo.text = "Route: Canceled | ETA: --"
        mapView.invalidate()
    }
}