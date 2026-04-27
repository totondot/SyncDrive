package com.example.syncdrive

import android.graphics.Color
import android.widget.TextView
import okhttp3.*
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import java.io.IOException

class RouteController(
    private val mapView: MapView,
    private val tvRouteInfo: TextView
) {
    private var routeLine: Polyline? = null
    private val client = OkHttpClient()

    fun calculateAndDrawRoute(
        startLocation: GeoPoint,
        destination: GeoPoint,
        onRouteReady: ((List<GeoPoint>) -> Unit)? = null // 👈 NEW: Optional callback
    ) { // Build the URL for the free OSRM public API
        // Format: longitude,latitude (OSRM requires longitude first!)
        val url = "https://router.project-osrm.org/route/v1/driving/" +
                "${startLocation.longitude},${startLocation.latitude};" +
                "${destination.longitude},${destination.latitude}" +
                "?overview=full&geometries=geojson"

        val request = Request.Builder().url(url).build()

        // Run the network request on a background thread
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("❌ Routing Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonString ->
                    try {
                        val json = JSONObject(jsonString)
                        val routes = json.getJSONArray("routes")

                        if (routes.length() > 0) {
                            val route = routes.getJSONObject(0)

                            // 1. Get real distance and duration from the API
                            val distanceMeters = route.getDouble("distance")
                            val durationSeconds = route.getDouble("duration")

                            val distanceKm = distanceMeters / 1000.0
                            val etaMinutes = (durationSeconds / 60).toInt()

                            // 2. Parse the road geometry (the actual turns and curves)
                            val geometry = route.getJSONObject("geometry")
                            val coordinates = geometry.getJSONArray("coordinates")

                            val routePoints = mutableListOf<GeoPoint>()
                            for (i in 0 until coordinates.length()) {
                                val point = coordinates.getJSONArray(i)
                                // GeoJSON provides coordinates as [Longitude, Latitude]
                                val lon = point.getDouble(0)
                                val lat = point.getDouble(1)
                                routePoints.add(GeoPoint(lat, lon))
                            }

                            // 3. Update the UI and Map on the Main Thread!
                            mapView.post {
                                // Update Route text box
                                tvRouteInfo.text = "Dist: ${String.format("%.2f", distanceKm)} km | ETA: $etaMinutes mins"

                                // Remove previous straight line
                                routeLine?.let { mapView.overlays.remove(it) }

                                // Draw the real road path
                                routeLine = Polyline(mapView).apply {
                                    setPoints(routePoints)
                                    outlinePaint.color = Color.parseColor("#0055FF") // Blue route
                                    outlinePaint.strokeWidth = 12f
                                }

                                mapView.overlays.add(routeLine)
                                mapView.invalidate() // Refresh map
                                onRouteReady?.invoke(routePoints)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    fun clearRoute() {
        routeLine?.let { mapView.overlays.remove(it) }
        mapView.invalidate()
    }
}