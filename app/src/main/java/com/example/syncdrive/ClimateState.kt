package com.example.syncdrive

// Model: Represents the vehicle's cabin climate control settings
data class ClimateState(
    var isOn: Boolean = false,
    var targetTemperatureC: Int = 22, // Default comfortable room temp in Celsius
    var mode: String = "AUTO"         // AUTO, AC, HEAT, DEFROST
)