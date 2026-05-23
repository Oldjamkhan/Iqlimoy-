package com.example.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CityData(
    val id: String,
    val name: String,
    val nameUz: String,
    val region: String,
    val aqi: Int,
    val pm25: Int,
    val pm10: Int,
    val uvIndex: Int,
    val temperature: Int,
    val humidity: Int,
    val windSpeed: Int,
    val dustRisk: Int,
    val solarRadiation: Int,
    val magneticField: String, // "normal", "disturbed", "storm"
    val conditions: String
)

@JsonClass(generateAdapter = true)
data class AlertData(
    val id: String,
    val type: String, // "dust", "smog", "uv", "magnetic", "heat", "chemical"
    val title: String,
    val titleUz: String,
    val description: String,
    val regions: List<String>,
    val severity: String, // "low", "medium", "high", "critical"
    val status: String, // "active", "monitoring", "resolved"
    val timestamp: String,
    val expiresAt: String
)

@JsonClass(generateAdapter = true)
data class ForecastDay(
    val date: String,
    val dayName: String,
    val tempMin: Int,
    val tempMax: Int,
    val aqi: Int,
    val uvIndex: Int,
    val precipChance: Int,
    val conditions: String,
    val dustRisk: Int
)

@JsonClass(generateAdapter = true)
data class Partner(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val discount: String,
    val cleanAirScore: Int
)

@JsonClass(generateAdapter = true)
data class RegionGridData(
    val id: String,
    val name: String,
    val aqi: Int,
    val gridRow: Int,
    val gridCol: Int,
    val gridColSpan: Int
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class HealthProfile(
    val name: String = "",
    val ageGroup: String = "adult", // "child", "teen", "adult", "middle", "senior"
    val conditions: List<String> = emptyList() // "asthma", "heart", "hypertension", "pregnant", "children", "elderly", "allergy"
)

@JsonClass(generateAdapter = true)
data class HourlyForecast(
    val hour: String,
    val aqi: Int,
    val temp: Int,
    val icon: String // "sun", "cloud", "moon"
)
