package com.example.syncdrive

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

    // Feature 5 Variables
    private lateinit var rvSignFeed: RecyclerView
    private lateinit var signFeedAdapter: SignFeedAdapter

    // Feature 6 Variables
    private lateinit var tvDestination: TextView
    private lateinit var navigationController: NavigationController

    // Feature 7 Variables
    private lateinit var tvRouteInfo: TextView
    private lateinit var routeController: RouteController

    private val carCurrentLocation = GeoPoint(23.8103, 90.4125)
    private val userCurrentLocation = GeoPoint(23.8041, 90.4152)

    // Feature 8 Variables
    private lateinit var btnSummon: Button
    private lateinit var summonController: SummonController

    // Feature 9 Variables
    private lateinit var btnDoorLock: Button
    private lateinit var doorController: DoorController

    // Feature 10 Variables
    private lateinit var btnEmergencyStop: Button
    private lateinit var emergencyController: EmergencyController

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

        // Setup Feature 5 (Road Sign Feed)
        rvSignFeed = findViewById(R.id.rvSignFeed)

        // Make it scroll horizontally
        rvSignFeed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize adapter with an empty list
        signFeedAdapter = SignFeedAdapter(mutableListOf())
        rvSignFeed.adapter = signFeedAdapter

        // Setup Feature 6 (Set Destination)
        tvDestination = findViewById(R.id.tvDestination)
        navigationController = NavigationController(mapView, tvDestination) { destinationGeoPoint ->
            // This block runs automatically whenever the user long-presses the map!
            routeController.calculateAndDrawRoute(carCurrentLocation, destinationGeoPoint)
        }

        // Activate the long-press listener
        navigationController.enableMapClickToSetDestination()
        // Start mock data

        // Setup Feature 7 (Routing & ETA)
        tvRouteInfo = findViewById(R.id.tvRouteInfo)
        routeController = RouteController(mapView, tvRouteInfo)

        // Setup Feature 8 (Summon Command)
        btnSummon = findViewById(R.id.btnSummon)

        // We pass in the routeController we already created in Feature 7!
        summonController = SummonController(btnSummon, routeController)

        // Activate the button logic
        summonController.setupSummonButton(carCurrentLocation, userCurrentLocation)

        // Setup Feature 9 (Door Locks)
        btnDoorLock = findViewById(R.id.btnDoorLock)
        doorController = DoorController(btnDoorLock)
        doorController.setupDoorControls()

        // Setup Feature 10 (Emergency Stop)
        btnEmergencyStop = findViewById(R.id.btnEmergencyStop)

        // Pass in the routeController so the E-Stop can cancel the map routes
        emergencyController = EmergencyController(btnEmergencyStop, routeController)
        emergencyController.setupEmergencySystem()

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
        // Mock Feature 5 (Simulating the car detecting signs as it drives)
        signFeedAdapter.addSignToFeed(DetectedSign("Speed Limit 40", System.currentTimeMillis() - 10000))
        signFeedAdapter.addSignToFeed(DetectedSign("Pedestrian Crossing", System.currentTimeMillis() - 5000))
        signFeedAdapter.addSignToFeed(DetectedSign("Stop Sign", System.currentTimeMillis()))

        // Scroll to the newest item
        rvSignFeed.scrollToPosition(0)
    }
}