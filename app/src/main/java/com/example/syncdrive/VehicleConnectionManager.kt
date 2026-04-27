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

        // FIX 1: Must be "ws://" for local PC connections
        val request = Request.Builder().url("ws://192.168.1.4:8766").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    // Parse the JSON payload from Python
                    val json = JSONObject(text)
                    val type = json.getString("type")
                    
                    // Some updates might be wrapped in "data", others might be at the root
                    val data = if (json.has("data")) json.getJSONObject("data") else json

                    // FIX 2: Match the exact JSON keys being sent by virtual_car.py
                    when (type) {
                        "LOCATION_UPDATE" -> {
                            listener.onLocationUpdated(
                                VehicleLocation(
                                    data.optDouble("latitude", data.optDouble("lat", 0.0)),
                                    data.optDouble("longitude", data.optDouble("lon", 0.0)),
                                    System.currentTimeMillis()
                                )
                            )
                        }
                        "TELEMETRY_UPDATE" -> {
                            // If virtual_car.py sends a combined update, parse individual parts
                            if (json.has("location")) {
                                val loc = json.getJSONObject("location")
                                listener.onLocationUpdated(
                                    VehicleLocation(loc.getDouble("lat"), loc.getDouble("lon"), System.currentTimeMillis())
                                )
                            }
                            
                            val speed = data.optDouble("speedKmh", data.optDouble("speed", 0.0))
                            val heading = data.optDouble("headingDegrees", data.optDouble("heading", 0.0)).toFloat()
                            val direction = data.optString("headingDirection", "N")
                            
                            listener.onTelemetryUpdated(VehicleTelemetry(speed, heading, direction))

                            if (json.has("status")) {
                                val status = json.getJSONObject("status")
                                listener.onStatusUpdated(
                                    VehicleStatus(
                                        status.getInt("battery"),
                                        status.optDouble("range", 0.0)
                                    )
                                )
                            }
                        }
                        "STATUS_UPDATE" -> {
                            listener.onStatusUpdated(
                                VehicleStatus(
                                    data.getInt("batteryPercentage"),
                                    data.optDouble("estimatedRangeKm", 0.0)
                                )
                            )
                        }
                        "SIGN_DETECTED" -> {
                            listener.onNewSignDetected(
                                DetectedSign(
                                    data.optString("name", data.optString("signName", "Unknown")),
                                    System.currentTimeMillis()
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    println("❌ Error parsing JSON: ${e.message}")
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
        // FIX 3: Python is looking for the key "action" to match virtual_car.py
        val command = """{"action": "$action", "payload": $payloadJSON}"""
        webSocket?.send(command)
    }
}