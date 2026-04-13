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

    // Feature 4
    private lateinit var alertBanner: View
    private lateinit var tvAlertMessage: TextView
    private lateinit var diagnosticController: DiagnosticController

    // Feature 5
    private lateinit var rvSignFeed: RecyclerView
    private lateinit var signFeedAdapter: SignFeedAdapter

    // Feature 6
    private lateinit var tvDestination: TextView
    private lateinit var navigationController: NavigationController

    // Feature 7
    private lateinit var tvRouteInfo: TextView
    private lateinit var routeController: RouteController

    // Feature 8
    private lateinit var btnSummon: Button
    private lateinit var summonController: SummonController

    // Feature 9
    private lateinit var btnDoorLock: Button
    private lateinit var doorController: DoorController

    // Feature 10
    private lateinit var btnEmergencyStop: Button
    private lateinit var emergencyController: EmergencyController

    // Feature 11
    private lateinit var btnTripHistory: Button
    private lateinit var tripHistoryController: TripHistoryController

    // Mock Coordinates
    private val carCurrentLocation = GeoPoint(23.8103, 90.4125)
    private val userCurrentLocation = GeoPoint(23.8041, 90.4152)

    // Feature 12 Variables
    private lateinit var tripDetailsPanel: View
    private lateinit var tvTripDetailStats: TextView
    private lateinit var btnCloseTripDetail: Button
    private lateinit var tripDetailController: TripDetailController

    // Feature 13 Variables
    private lateinit var btnEfficiency: Button
    private lateinit var efficiencyController: EfficiencyController

    // Feature 14 Variables
    private lateinit var btnIncidentReport: Button
    private lateinit var incidentController: IncidentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_main)

        // Setup Feature 1 (Map)
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapController = MapController(mapView)

        // Setup Feature 2 (Battery/Range)
        tvBatteryStatus = findViewById(R.id.tvBatteryStatus)
        tvRangeStatus = findViewById(R.id.tvRangeStatus)
        statusController = StatusController(tvBatteryStatus, tvRangeStatus)

        // Setup Feature 3 (Speed/Heading)
        tvSpeed = findViewById(R.id.tvSpeed)
        tvHeading = findViewById(R.id.tvHeading)
        telemetryController = TelemetryController(tvSpeed, tvHeading)

        // Setup Feature 4 (Diagnostics)
        alertBanner = findViewById(R.id.alertBanner)
        tvAlertMessage = findViewById(R.id.tvAlertMessage)
        diagnosticController = DiagnosticController(alertBanner, tvAlertMessage)

        // Setup Feature 5 (Road Sign Feed)
        rvSignFeed = findViewById(R.id.rvSignFeed)
        rvSignFeed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        signFeedAdapter = SignFeedAdapter(mutableListOf())
        rvSignFeed.adapter = signFeedAdapter

        // --- CORE NAVIGATION GROUP (Features 7, 11, 12, then 6) ---

        // Setup Feature 7 (Routing & ETA)
        tvRouteInfo = findViewById(R.id.tvRouteInfo)
        routeController = RouteController(mapView, tvRouteInfo)

        // Setup Feature 12 (Trip Details View)
        tripDetailsPanel = findViewById(R.id.tripDetailsPanel)
        tvTripDetailStats = findViewById(R.id.tvTripDetailStats)
        btnCloseTripDetail = findViewById(R.id.btnCloseTripDetail)
        tripDetailController = TripDetailController(tripDetailsPanel, tvTripDetailStats, btnCloseTripDetail, mapView)

        // Setup Feature 11 (Trip History List) - Passes clicks to Feature 12
        btnTripHistory = findViewById(R.id.btnTripHistory)
        tripHistoryController = TripHistoryController(btnTripHistory) { selectedTrip ->
            tripDetailController.showTripDetails(selectedTrip) // Trigger Feature 12!
        }
        tripHistoryController.setupHistoryButton()

        // Setup Feature 6 (Set Destination) - Logs upgraded data to Feature 11
        tvDestination = findViewById(R.id.tvDestination)
        navigationController = NavigationController(mapView, tvDestination) { destinationGeoPoint ->
            routeController.calculateAndDrawRoute(carCurrentLocation, destinationGeoPoint)

            // Calculate detailed stats to save
            val distanceMeters = carCurrentLocation.distanceToAsDouble(destinationGeoPoint)
            val distanceKm = distanceMeters / 1000.0
            val durationMins = ((distanceKm / 40.0) * 60).toInt() // Assuming 40km/h avg speed
            val routePoints = listOf(carCurrentLocation, destinationGeoPoint) // Simple A-to-B path

            tripHistoryController.addTripToHistory(distanceKm, durationMins, routePoints)
        }
        navigationController.enableMapClickToSetDestination()

        // ------------------------------------------------------

        // Setup Feature 8 (Summon Command)
        btnSummon = findViewById(R.id.btnSummon)
        summonController = SummonController(btnSummon, routeController)
        summonController.setupSummonButton(carCurrentLocation, userCurrentLocation)

        // Setup Feature 9 (Door Locks)
        btnDoorLock = findViewById(R.id.btnDoorLock)
        doorController = DoorController(btnDoorLock)
        doorController.setupDoorControls()

        // Setup Feature 10 (Emergency Stop)
        btnEmergencyStop = findViewById(R.id.btnEmergencyStop)
        emergencyController = EmergencyController(btnEmergencyStop, routeController)
        emergencyController.setupEmergencySystem()

        // Setup Feature 13 (Efficiency Tracking)
        btnEfficiency = findViewById(R.id.btnEfficiency)
        efficiencyController = EfficiencyController(btnEfficiency)
        efficiencyController.setupEfficiencyButton()

        // Setup Feature 14 (Incident Reporting)
        btnIncidentReport = findViewById(R.id.btnIncidentReport)
        incidentController = IncidentController(btnIncidentReport)
        incidentController.setupIncidentButton()

        // Start mock data stream
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
        // Mock Feature 1, 2, 3
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

        // Mock Feature 5 (Simulating the car detecting signs as it drives)
        signFeedAdapter.addSignToFeed(DetectedSign("Speed Limit 40", System.currentTimeMillis() - 10000))
        signFeedAdapter.addSignToFeed(DetectedSign("Pedestrian Crossing", System.currentTimeMillis() - 5000))
        signFeedAdapter.addSignToFeed(DetectedSign("Stop Sign", System.currentTimeMillis()))

        // Mock Feature 14 (Simulating edge-cases during the drive)
        incidentController.logIncident(
            TripIncident.IncidentType.UNKNOWN_SIGN,
            "Faded construction sign detected on Hwy 61."
        )
        incidentController.logIncident(
            TripIncident.IncidentType.HARD_STOP,
            "Pedestrian stepped into crosswalk unexpectedly."
        )
        
        // Scroll to the newest item
        rvSignFeed.scrollToPosition(0)
    }
}