package com.example.syncdrive

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

class MainActivity : AppCompatActivity() {

    // Feature 1 Variables
    private lateinit var mapView: MapView
    private lateinit var mapController: MapController

    // Feature 2 Variables
    private lateinit var tvBatteryStatus: TextView
    private lateinit var tvRangeStatus: TextView
    private lateinit var statusController: StatusController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OSMDroid configuration
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_main)

        // --- FEATURE 1 SETUP (Map) ---
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapController = MapController(mapView)

        // --- FEATURE 2 SETUP (Battery/Range) ---
        tvBatteryStatus = findViewById(R.id.tvBatteryStatus)
        tvRangeStatus = findViewById(R.id.tvRangeStatus)
        statusController = StatusController(tvBatteryStatus, tvRangeStatus)

        // Simulate fetching real-time data from the car
        simulateRealTimeUpdates()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun simulateRealTimeUpdates() {
        // Feature 1: Mock Location Data
        val mockLocation = VehicleLocation(
            latitude = 23.8103, // Dhaka
            longitude = 90.4125,
            timestamp = System.currentTimeMillis()
        )
        mapController.updateVehicleLocationOnMap(mockLocation)

        // Feature 2: Mock Status Data
        val mockStatus = VehicleStatus(
            batteryPercentage = 78,
            estimatedRangeKm = 315.5
        )
        statusController.updateVehicleStatusOnDashboard(mockStatus)
    }
}