package com.example.syncdrive

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

// Implement the VehicleDataListener to receive real-time data
class MainActivity : AppCompatActivity(), VehicleConnectionManager.VehicleDataListener {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Pass the callback to send the command
            mapController.enableUserLocation { userPoint ->
                syncCarToUserLocation(userPoint)
            }
        } else {
            Toast.makeText(this, "GPS permission is required to show your location.", Toast.LENGTH_LONG).show()
        }
    }

    // Helper function to send the command
    private fun syncCarToUserLocation(point: GeoPoint) {
        val payload = """{"lat": ${point.latitude}, "lon": ${point.longitude}}"""
        vehicleConnection.sendCommandToCar("SET_LOCATION", payload)
    }

    // Core Map & Dashboard Views (Features 1-5)
    private lateinit var mapView: MapView
    private lateinit var mapController: MapController

    private lateinit var tvBatteryStatus: TextView
    private lateinit var tvRangeStatus: TextView
    private lateinit var statusController: StatusController

    private lateinit var tvSpeed: TextView
    private lateinit var tvHeading: TextView
    private lateinit var telemetryController: TelemetryController

    private lateinit var alertBanner: View
    private lateinit var tvAlertMessage: TextView
    private lateinit var diagnosticController: DiagnosticController

    private lateinit var rvSignFeed: RecyclerView
    private lateinit var signFeedAdapter: SignFeedAdapter

    // Navigation & Routing (Features 6-8)
    private lateinit var tvDestination: TextView
    private lateinit var navigationController: NavigationController

    private lateinit var tvRouteInfo: TextView
    private lateinit var routeController: RouteController

    private lateinit var btnSummon: Button
    private lateinit var summonController: SummonController

    // Remote Controls & Emergency (Features 9-10)
    private lateinit var btnDoorLock: Button
    private lateinit var doorController: DoorController

    private lateinit var btnEmergencyStop: Button
    private lateinit var emergencyController: EmergencyController

    // Data & Analytics (Features 11-15)
    private lateinit var btnTripHistory: Button
    private lateinit var historyController: TripHistoryController

    private lateinit var tripDetailsPanel: View
    private lateinit var tvTripStats: TextView
    private lateinit var btnCloseDetails: Button
    private lateinit var tripDetailController: TripDetailController

    private lateinit var btnEfficiency: Button
    private lateinit var efficiencyController: EfficiencyController

    private lateinit var btnIncidentReport: Button
    private lateinit var incidentController: IncidentController

    private lateinit var btnExportData: Button
    private lateinit var exportController: ExportController

    // Maintenance & Security (Features 16-20)
    private lateinit var btnMaintenance: Button
    private lateinit var maintenanceController: MaintenanceController

    private lateinit var btnSystemCheck: Button
    private lateinit var checklistController: ChecklistController

    private lateinit var btnServiceLog: Button
    private lateinit var serviceLogController: ServiceLogController

    private lateinit var btnRestrictions: Button
    private lateinit var restrictionsController: RestrictionsController

    private lateinit var btnClimateControl: Button
    private lateinit var climateController: ClimateController

    // NEW: Real-Time Connection Manager
    private lateinit var vehicleConnection: VehicleConnectionManager

    // Stores the most recent location
    private var latestCarLocation: GeoPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // --- 1. UI INITIALIZATION & CONTROLLER SETUP ---

        // Feature 1: Map
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapController = MapController(mapView)

        // Feature 2: Battery/Range Status
        tvBatteryStatus = findViewById(R.id.tvBatteryStatus)
        tvRangeStatus = findViewById(R.id.tvRangeStatus)
        statusController = StatusController(tvBatteryStatus, tvRangeStatus)

        // Feature 3: Speed/Heading Telemetry
        tvSpeed = findViewById(R.id.tvSpeed)
        tvHeading = findViewById(R.id.tvHeading)
        telemetryController = TelemetryController(tvSpeed, tvHeading)

        // Feature 4: Diagnostics
        alertBanner = findViewById(R.id.alertBanner)
        tvAlertMessage = findViewById(R.id.tvAlertMessage)
        diagnosticController = DiagnosticController(alertBanner, tvAlertMessage)

        // Feature 5: Sign Feed
        rvSignFeed = findViewById(R.id.rvSignFeed)
        rvSignFeed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        signFeedAdapter = SignFeedAdapter(mutableListOf())
        rvSignFeed.adapter = signFeedAdapter

        // 🚨 IMPORTANT FIX: Initialize connection early so we can pass it to ALL controllers!
        vehicleConnection = VehicleConnectionManager()

        // Feature 6 & 7: Navigation & Routing
        tvDestination = findViewById(R.id.tvDestination)
        tvRouteInfo = findViewById(R.id.tvRouteInfo)
        routeController = RouteController(mapView, tvRouteInfo)

        // Set up Map Long-Press for Destination
        navigationController = NavigationController(mapView, tvDestination) { destinationPoint ->
            // Use the LIVE location if we have it, otherwise fallback to a default
            val startPoint = latestCarLocation ?: GeoPoint(37.7749, -122.4194)
            routeController.calculateAndDrawRoute(startPoint, destinationPoint)

            // 🚨 NEW: Send the destination coordinates to Python!
            val payload = """{"lat": ${destinationPoint.latitude}, "lon": ${destinationPoint.longitude}}"""
            vehicleConnection.sendCommandToCar("SET_DESTINATION", payload)
        }
        navigationController.enableMapClickToSetDestination()

        // Feature 8: Summon (Now passing vehicleConnection)
        btnSummon = findViewById(R.id.btnSummon)
        summonController = SummonController(btnSummon, routeController, vehicleConnection)
        val mockUserLocation = GeoPoint(37.7755, -122.4180)
        val currentCarLocation = GeoPoint(37.7749, -122.4194)
        summonController.setupSummonButton(currentCarLocation, mockUserLocation)

        // Feature 9: Door Locks (Now passing vehicleConnection)
        btnDoorLock = findViewById(R.id.btnDoorLock)
        doorController = DoorController(btnDoorLock, vehicleConnection)
        doorController.setupDoorControls()

        // Feature 10: Emergency Stop
        btnEmergencyStop = findViewById(R.id.btnEmergencyStop)
        emergencyController = EmergencyController(btnEmergencyStop, routeController, vehicleConnection)
        emergencyController.setupEmergencySystem()

        // Feature 11 & 12: Trip History & Details
        tripDetailsPanel = findViewById(R.id.tripDetailsPanel)
        tvTripStats = findViewById(R.id.tvTripStats)
        btnCloseDetails = findViewById(R.id.btnCloseDetails)
        tripDetailController = TripDetailController(tripDetailsPanel, tvTripStats, btnCloseDetails, mapView)

        btnTripHistory = findViewById(R.id.btnTripHistory)
        historyController = TripHistoryController(btnTripHistory) { selectedTrip ->
            tripDetailController.showTripDetails(selectedTrip)
        }
        historyController.setupHistoryButton()

        // Feature 13: Efficiency
        btnEfficiency = findViewById(R.id.btnEfficiency)
        efficiencyController = EfficiencyController(btnEfficiency)
        efficiencyController.setupEfficiencyButton()

        // Feature 14: Incident Reporting
        btnIncidentReport = findViewById(R.id.btnIncidentReport)
        incidentController = IncidentController(btnIncidentReport)
        incidentController.setupIncidentButton()

        // Feature 15: Data Export
        btnExportData = findViewById(R.id.btnExportData)
        exportController = ExportController(btnExportData, historyController)
        exportController.setupExportButton()

        // Feature 16: Maintenance Reminders
        btnMaintenance = findViewById(R.id.btnMaintenance)
        maintenanceController = MaintenanceController(btnMaintenance)
        maintenanceController.setupMaintenanceButton()

        // Feature 17: Pre-ride Checklist (Now passing vehicleConnection)
        btnSystemCheck = findViewById(R.id.btnSystemCheck)
        checklistController = ChecklistController(btnSystemCheck, vehicleConnection)
        checklistController.setupChecklistButton()

        // Feature 18: Service Log
        btnServiceLog = findViewById(R.id.btnServiceLog)
        serviceLogController = ServiceLogController(btnServiceLog)
        serviceLogController.setupServiceLogButton()

        // Feature 19: Restrictions
        btnRestrictions = findViewById(R.id.btnRestrictions)
        restrictionsController = RestrictionsController(btnRestrictions)
        restrictionsController.setupRestrictionsButton()

        // Feature 20: Climate Control (Now passing vehicleConnection)
        btnClimateControl = findViewById(R.id.btnClimateControl)
        climateController = ClimateController(btnClimateControl, vehicleConnection)
        climateController.setupClimateButton()

        // Check if GPS permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // We have permission, show the blue dot AND sync the car!
            mapController.enableUserLocation { userPoint ->
                syncCarToUserLocation(userPoint)
            }
        } else {
            // Ask the user for permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // --- 2. START REAL-TIME CONNECTION ---

        // Connect to the car's data stream (passes this activity as the listener)
        vehicleConnection.connect(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Prevent memory leaks when app closes
        vehicleConnection.disconnect()
    }

    // --- 3. IMPLEMENT REAL-TIME CALLBACKS ---

    override fun onTelemetryUpdated(telemetry: VehicleTelemetry) {
        runOnUiThread {
            telemetryController.updateTelemetryOnDashboard(telemetry)
        }
    }

    override fun onLocationUpdated(location: VehicleLocation) {
        runOnUiThread {
            mapController.updateVehicleLocationOnMap(location)

            // Save the live location to our variable
            latestCarLocation = GeoPoint(location.latitude, location.longitude)
        }
    }

    override fun onDiagnosticsUpdated(diagnostics: VehicleDiagnostics) {
        runOnUiThread {
            diagnosticController.updateDiagnostics(diagnostics)
        }
    }

    override fun onStatusUpdated(status: VehicleStatus) {
        runOnUiThread {
            statusController.updateVehicleStatusOnDashboard(status)
        }
    }

    override fun onNewSignDetected(sign: DetectedSign) {
        runOnUiThread {
            signFeedAdapter.addSignToFeed(sign)
            rvSignFeed.scrollToPosition(0) // Auto-scroll to newest sign
        }
    }
}