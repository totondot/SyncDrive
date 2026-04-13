package com.example.syncdrive

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapController(private val mapView: MapView) {

    private var carMarker: Marker? = null

    init {
        // Configure basic map settings
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(18.0) // Set initial zoom level
    }

    // Controller method to update the View based on the Model
    fun updateVehicleLocationOnMap(location: VehicleLocation) {
        // OSMDroid uses GeoPoint instead of LatLng
        val carPosition = GeoPoint(location.latitude, location.longitude)

        // Move the camera to the new location smoothly
        mapView.controller.animateTo(carPosition)

        // Remove the previous marker if it exists so we don't draw a trail of markers
        carMarker?.let { mapView.overlays.remove(it) }

        // Create and add a new marker for the car
        carMarker = Marker(mapView).apply {
            position = carPosition
            title = "Autonomous Car Current Location"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }

        mapView.overlays.add(carMarker)

        // Invalidate refreshes the map to show the new marker
        mapView.invalidate()
    }
}