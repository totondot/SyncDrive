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
    fun enableUserLocation(onLocationFound: ((GeoPoint) -> Unit)? = null) {
        val provider = GpsMyLocationProvider(mapView.context)
        locationOverlay = MyLocationNewOverlay(provider, mapView)
        locationOverlay?.enableMyLocation()

        locationOverlay?.enableFollowLocation()

        locationOverlay?.runOnFirstFix {
            mapView.post {
                val myLocation = locationOverlay?.myLocation
                if (myLocation != null) {
                    mapView.controller.animateTo(myLocation)
                    mapView.controller.setZoom(18.0)

                    // NEW: Pass the GPS coordinates back to the callback!
                    val userPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
                    onLocationFound?.invoke(userPoint)
                }
            }
        }

        mapView.overlays.add(locationOverlay)
        mapView.invalidate()
    }
}
