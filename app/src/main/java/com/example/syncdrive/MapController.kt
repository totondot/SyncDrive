package com.example.syncdrive

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapController(private val mapView: MapView) {

    private var carMarker: Marker? = null

    init {
        // Configure basic map settings
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(18.0) // Set initial zoom level
    }

    // Controller method to update the View based on the Model
    fun updateVehicleLocationOnMap(location: VehicleLocation) {
        val carPosition = GeoPoint(location.latitude, location.longitude)

        // Remove the previous marker if it exists
        carMarker?.let { mapView.overlays.remove(it) }

        // Create and add a new marker for the car
        carMarker = Marker(mapView).apply {
            position = carPosition
            title = "Autonomous Car Current Location"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }

        mapView.overlays.add(carMarker)
        mapView.invalidate()
    }

    private var locationOverlay: MyLocationNewOverlay? = null

    // Call this to turn on the device's GPS blue dot
    fun enableUserLocation() {
        val provider = GpsMyLocationProvider(mapView.context)
        locationOverlay = MyLocationNewOverlay(provider, mapView)
        locationOverlay?.enableMyLocation()

        // 1. Tell the map to actively follow the user
        locationOverlay?.enableFollowLocation()

        // 2. Wait for the GPS to get a lock, then fly the camera to that spot
        locationOverlay?.runOnFirstFix {
            // runOnFirstFix runs on a background thread, so we must post back to the UI thread
            mapView.post {
                val myLocation = locationOverlay?.myLocation
                if (myLocation != null) {
                    mapView.controller.animateTo(myLocation)
                    mapView.controller.setZoom(18.0) // Zoom in close!
                }
            }
        }

        mapView.overlays.add(locationOverlay)
        mapView.invalidate()
    }
}
