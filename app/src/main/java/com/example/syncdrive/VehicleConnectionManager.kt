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

        val request = Request.Builder().url("ws://192.168.1.4:8766").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
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
                } else if (json.getString("type") == "DIAGNOSTICS_UPDATE") {
                    val diagJson = json.getJSONObject("diagnostics")
                    val tirePressure = mutableListOf<Double>()
                    val tires = diagJson.getJSONArray("tire_pressure")
                    for (i in 0 until tires.length()) {
                        tirePressure.add(tires.getDouble(i))
                    }
                    listener.onDiagnosticsUpdated(
                        VehicleDiagnostics(
                            diagJson.getDouble("engine_temp"),
                            tirePressure,
                            diagJson.getBoolean("sensors_online")
                        )
                    )
                } else if (json.getString("type") == "SIGN_DETECTED") {
                    val signJson = json.getJSONObject("sign")
                    listener.onNewSignDetected(
                        DetectedSign(
                            signJson.getString("name"),
                            System.currentTimeMillis()
                        )
                    )
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