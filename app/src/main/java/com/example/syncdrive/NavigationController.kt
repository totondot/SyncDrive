package com.example.syncdrive

import android.widget.TextView
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class NavigationController(
    private val mapView: MapView,
    private val tvDestination: TextView,
    private val onDestinationSet: (GeoPoint) -> Unit
) {
    private var destinationMarker: Marker? = null
    var currentTarget: NavigationTarget? = null

    // Sets up a listener for long-presses on the map
    fun enableMapClickToSetDestination() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                return false // Ignore single taps
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                p?.let {
                    val target = NavigationTarget(it.latitude, it.longitude)
                    updateDestination(target)
                    onDestinationSet(it)
                }
                return true
            }
        }

        // Add the touch listener overlay to the map
        val overlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(overlay)
    }

    // Updates the Model, updates the View (Text), and places the Map Pin
    private fun updateDestination(target: NavigationTarget) {
        this.currentTarget = target
        val geoPoint = GeoPoint(target.latitude, target.longitude)

        // 1. Remove the old pin if the user picks a new spot
        destinationMarker?.let { mapView.overlays.remove(it) }

        // 2. Create and place the new pin
        destinationMarker = Marker(mapView).apply {
            position = geoPoint
            title = target.label
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(destinationMarker)

        // 3. Force the map to redraw with the new pin
        mapView.invalidate()

        // 4. Update the UI text (formatted to 4 decimal places for readability)
        tvDestination.text = "Destination: ${String.format("%.4f", target.latitude)}, ${String.format("%.4f", target.longitude)}"
    }
}