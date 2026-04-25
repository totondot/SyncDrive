package com.example.syncdrive

import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class VehicleConnectionManager {

    interface VehicleDataListener {
        fun onTelemetryUpdated(telemetry: VehicleTelemetry)
        fun onLocationUpdated(location: VehicleLocation)
        fun onStatusUpdated(status: VehicleStatus)
        fun onDiagnosticsUpdated(diagnostics: VehicleDiagnostics)
        fun onNewSignDetected(sign: DetectedSign)
    }

    private var listener: VehicleDataListener? = null
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    fun connect(listener: VehicleDataListener) {
        this.listener = listener

        // REPLACE WITH YOUR COMPUTER'S LOCAL IP ADDRESS
        val request = Request.Builder().url("ws://192.168.1.1:8765").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("✅ Connected to Virtual Car!")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Parse the JSON payload from Python
                val json = JSONObject(text)
                if (json.getString("type") == "TELEMETRY_UPDATE") {

                    val locJson = json.getJSONObject("location")
                    listener.onLocationUpdated(VehicleLocation(locJson.getDouble("lat"), locJson.getDouble("lon"), System.currentTimeMillis()))

                    val telJson = json.getJSONObject("telemetry")
                    listener.onTelemetryUpdated(VehicleTelemetry(telJson.getDouble("speed"), telJson.getDouble("heading").toFloat(), "E"))

                    val statJson = json.getJSONObject("status")
                    listener.onStatusUpdated(VehicleStatus(statJson.getInt("battery"), 100.0))
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("❌ Connection failed: ${t.message}")
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "App closed")
    }

    // Send real commands to Python!
    fun sendCommandToCar(action: String, payloadJSON: String) {
        val command = """{"action": "$action", "payload": $payloadJSON}"""
        webSocket?.send(command)
    }
}