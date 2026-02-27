package com.example.syncdrive

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

class MainActivity : AppCompatActivity() {

    // Feature 1
    private lateinit var mapView: MapView
    private lateinit var mapController: MapController

    // Feature 2
    private lateinit var tvBatteryStatus: TextView
    private lateinit var tvRangeStatus: TextView
    private lateinit var statusController: StatusController

    // Feature 3
    private lateinit var tvSpeed: TextView
    private lateinit var tvHeading: TextView
    private lateinit var telemetryController: TelemetryController

    // Feature 4 Variables
    private lateinit var alertBanner: View
    private lateinit var tvAlertMessage: TextView
    private lateinit var diagnosticController: DiagnosticController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_main)

        // Setup Feature 1
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapController = MapController(mapView)

        // Setup Feature 2
        tvBatteryStatus = findViewById(R.id.tvBatteryStatus)
        tvRangeStatus = findViewById(R.id.tvRangeStatus)
        statusController = StatusController(tvBatteryStatus, tvRangeStatus)

        // Setup Feature 3
        tvSpeed = findViewById(R.id.tvSpeed)
        tvHeading = findViewById(R.id.tvHeading)
        telemetryController = TelemetryController(tvSpeed, tvHeading)

        // Setup Feature 4
        alertBanner = findViewById(R.id.alertBanner)
        tvAlertMessage = findViewById(R.id.tvAlertMessage)
        diagnosticController = DiagnosticController(alertBanner, tvAlertMessage)

        // Start mock data
        simulateRealTimeUpdates()
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }

    private fun simulateRealTimeUpdates() {
        // ... (Keep your mock data for Features 1, 2, and 3 here) ...
        val mockLocation = VehicleLocation(23.8103, 90.4125, System.currentTimeMillis())
        mapController.updateVehicleLocationOnMap(mockLocation)
        val mockStatus = VehicleStatus(78, 315.5)
        statusController.updateVehicleStatusOnDashboard(mockStatus)
        val mockTelemetry = VehicleTelemetry(45.5, 45.0f, "NE")
        telemetryController.updateTelemetryOnDashboard(mockTelemetry)

        // Mock Feature 4 (Simulating a Low Tire Pressure and High Temp situation)
        val mockDiagnostics = VehicleDiagnostics(
            engineTempCelsius = 108.5, // Above 105
            tirePressurePsi = listOf(32.0, 31.5, 28.0, 32.0), // One tire is below 30
            sensorsOnline = true
        )
        diagnosticController.updateDiagnostics(mockDiagnostics)
    }
}