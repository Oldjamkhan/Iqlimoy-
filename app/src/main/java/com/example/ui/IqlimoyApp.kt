package com.example.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.IqlimoyViewModel
import kotlinx.coroutines.launch

// Centralized Iqlimoy color palette (Sleek Interface light theme)
val BgColor = Color(0xFFF3F4F9)
val CardColor = Color.White
val PrimaryColor = Color(0xFF0061A4)
val BorderColor = Color(0xFFE1E2EC)
val MutedTextColor = Color(0xFF64748B)
val TextColor = Color(0xFF0F172A)

// Helper mapping for AQI Colors
fun getAQIComposeColor(aqi: Int): Color {
    return when {
        aqi <= 50 -> Color(0xFF22D3A5)         // Good - Green
        aqi <= 100 -> Color(0xFFF59E0B)        // Moderate - Yellow
        aqi <= 150 -> Color(0xFFF97316)        // Unhealthy for Sensitive - Orange
        aqi <= 200 -> Color(0xFFEF4444)        // Unhealthy - Red
        else -> Color(0xFFA855F7)              // Hazardous - Purple
    }
}

@Composable
fun IqlimoyApp(
    viewModel: IqlimoyViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf("monitoring") }
    var showCitySelector by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BgColor),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFE1E2EC),
                tonalElevation = 0.dp,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .testTag("bottom_nav")
            ) {
                listOf(
                    Triple("monitoring", Icons.Outlined.Air, "Monitoring"),
                    Triple("map", Icons.Outlined.Map, "Xarita"),
                    Triple("assistant", Icons.Outlined.AutoAwesome, "AI Assistant"),
                    Triple("alerts", Icons.Outlined.Notifications, "Ogohlantirish"),
                    Triple("explore", Icons.Outlined.TravelExplore, "Ko'proq")
                ).forEach { (tabId, icon, label) ->
                    val isSelected = currentTab == tabId
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tabId },
                        label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color(0xFF001D36) else MutedTextColor
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF001D36),
                            selectedTextColor = Color(0xFF001D36),
                            unselectedIconColor = MutedTextColor,
                            unselectedTextColor = MutedTextColor,
                            indicatorColor = Color(0xFFD1E4FF)
                        )
                    )
                }
            }
        },
        containerColor = BgColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (currentTab) {
                "monitoring" -> HomeScreen(
                    selectedCity = selectedCity,
                    onSelectCityClicked = { showCitySelector = true }
                )
                "map" -> MapScreen(
                    viewModel = viewModel,
                    onSelectCityAndReturn = { cityId ->
                        viewModel.setCityById(cityId)
                        currentTab = "monitoring"
                    }
                )
                "assistant" -> AssistantScreen(
                    viewModel = viewModel,
                    selectedCity = selectedCity
                )
                "alerts" -> AlertsScreen(
                    viewModel = viewModel
                )
                "explore" -> ExploreScreen(
                    viewModel = viewModel,
                    selectedCity = selectedCity
                )
            }
        }
    }

    // Modal dialog for selecting Uzbek regions
    if (showCitySelector) {
        CitySelectorDialog(
            cities = DemoData.CITIES,
            selectedCity = selectedCity,
            onDismiss = { showCitySelector = false },
            onSelectCity = { city ->
                viewModel.setCityById(city.id)
                showCitySelector = false
            }
        )
    }
}

// ==========================================
// 1. MONITORING / HOME SCREEN
// ==========================================
@Composable
fun HomeScreen(
    selectedCity: CityData,
    onSelectCityClicked: () -> Unit
) {
    val scrollState = rememberScrollState()
    val aqiColor = getAQIComposeColor(selectedCity.aqi)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // High-end Adaptive Headers
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD1E4FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cloud,
                        contentDescription = "Iqlimoy",
                        tint = Color(0xFF001D36),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "IQLIMOY",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001D36),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Havo & Ekologiya",
                        fontSize = 10.sp,
                        color = MutedTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Real-Time Satellite Indicator Pin
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryColor.copy(alpha = 0.08f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22D3A5))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SUN'IY YO'LDOSH LIVE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    letterSpacing = 0.6.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hero Weather / AQI Section with Sleek Gradient [Sleek Interface Theme]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFD1E4FF), Color(0xFFA8C7F0))
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bugun, 24-May",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF001D36).copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                
                // Clickable City / Region Picker
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                        .clickable { onSelectCityClicked() }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Shahar",
                        tint = Color(0xFF001D36),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedCity.nameUz,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001D36)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Tanlash",
                        tint = Color(0xFF001D36),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Modern AQI Circular gauge
                val infiniteTransition = rememberInfiniteTransition(label = "breathing")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.08f,
                    targetValue = 0.24f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1800, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )

                Box(
                    modifier = Modifier
                        .size(174.dp)
                        .drawBehind {
                            drawCircle(
                                color = aqiColor,
                                radius = size.minDimension / 2,
                                style = Stroke(width = 4.dp.toPx())
                            )
                            drawCircle(
                                color = aqiColor.copy(alpha = pulseAlpha),
                                radius = size.minDimension / 2 + 10.dp.toPx()
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = selectedCity.aqi.toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF001D36)
                        )
                        Text(
                            text = "AQI INDEX",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF001D36).copy(alpha = 0.65f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(aqiColor.copy(alpha = 0.22f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = DemoData.getAQILabel(selectedCity.aqi),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = aqiColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Multi-spec status values
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Harorat: ${selectedCity.temperature}°C",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001D36)
                    )
                    Box(modifier = Modifier.size(width = 1.dp, height = 12.dp).background(Color(0xFF001D36).copy(alpha = 0.3f)))
                    Text(
                        text = "Namlik: ${selectedCity.humidity}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001D36)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Environmental metrics grid layout
        Text(
            text = "EKOLOGIK SENSOR DIAGRAMMASI",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedTextColor,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Cards Rows
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "PM2.5 Ko'rsatkich",
                    value = "${selectedCity.pm25} µg/m³",
                    desc = "Mayda chang zarrachalari",
                    icon = Icons.Outlined.Air,
                    color = getAQIComposeColor(selectedCity.aqi),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "PM10 Ko'rsatkich",
                    value = "${selectedCity.pm10} µg/m³",
                    desc = "Yirik chang",
                    icon = Icons.Outlined.Grain,
                    color = getAQIComposeColor(selectedCity.aqi),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "UV Quyosh nuri",
                    value = "${selectedCity.uvIndex} UV",
                    desc = if (selectedCity.uvIndex < 3) "Xavfsiz" else if (selectedCity.uvIndex < 7) "O'rtacha xavf" else "Ekstremal",
                    icon = Icons.Outlined.WbSunny,
                    color = if (selectedCity.uvIndex < 6) Color(0xFF22D3A5) else Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Chang Bo'roni xavfi",
                    value = "${selectedCity.dustRisk}%",
                    desc = if (selectedCity.dustRisk < 30) "Past" else if (selectedCity.dustRisk < 70) "O'rtacha" else "Favqulodda",
                    icon = Icons.Outlined.CloudCircle,
                    color = if (selectedCity.dustRisk < 40) Color(0xFF22D3A5) else Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Harorat & Namlik",
                    value = "${selectedCity.temperature}°C / ${selectedCity.humidity}%",
                    desc = "Havoning zichlik holati",
                    icon = Icons.Outlined.Thermostat,
                    color = PrimaryColor,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Geomagnit",
                    value = when (selectedCity.magneticField) {
                        "storm" -> "G2 Bo'ron"
                        "disturbed" -> "Bezovta"
                        else -> "Tinch G1"
                    },
                    desc = "K-indeks darajasi",
                    icon = Icons.Outlined.Explore,
                    color = if (selectedCity.magneticField == "storm") Color(0xFFEF4444) else if (selectedCity.magneticField == "disturbed") Color(0xFFF59E0B) else Color(0xFF22D3A5),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hourly Air Quality Forecast Progress Row
        Text(
            text = "KUNLIK AQI VA HARORAT PROGNOZI",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedTextColor,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(DemoData.HOURLY_FORECAST) { item ->
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardColor)
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.hour,
                        fontSize = 12.sp,
                        color = MutedTextColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Icon(
                        imageVector = if (item.icon == "sun") Icons.Default.WbSunny else if (item.icon == "cloud") Icons.Default.Cloud else Icons.Default.NightsStay,
                        contentDescription = "Forecast visual",
                        tint = if (item.icon == "sun") Color(0xFFF59E0B) else PrimaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${item.temp}°C",
                        fontSize = 15.sp,
                        color = TextColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(getAQIComposeColor(item.aqi).copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AQI ${item.aqi}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = getAQIComposeColor(item.aqi)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Health Protection Advisory Card Custom Section
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Sog'liq ko'rsatkichi",
                        tint = PrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sog'liqni Himoya Qilish Maslahati",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                val aqi = selectedCity.aqi
                val alertMsg = when {
                    aqi <= 50 -> "Havo toza va xavfsiz. Tashqi jismoniy mashg'ulotlar va pikniklar uchun ayni muddao. Hech qanday himoya choralari shubhasiz talab qilinmaydi."
                    aqi <= 100 -> "Havo sifati o'rtacha. Sezuvchanligi bo'lgan insonlar (astma, allergiya) og'ir jismoniy mehnatni qisqartirishi va eshiklarni berkitishi ma'qul."
                    aqi <= 150 -> "Sog'liq uchun zararli havo. Niqob taqish lozim. Uy eshik-oynalarini beritb, xonani tozalagich bilan parvarishlang."
                    aqi <= 200 -> "O'ta zararli havo darajasi! N95 respiratorsiz tashqariga chiqmang. Barcha tashqi jismoniy faoliyatlarni zudlik bilan to'xtating."
                    else -> "FAVQULODDA XAVFLI! Tashqarida chang muttasil ifloslangan. uydan chiqmang. Oynalarni yopib, germetik chang filtrni yoqing!"
                }
                Text(
                    text = alertMsg,
                    fontSize = 13.sp,
                    color = MutedTextColor,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MutedTextColor,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 10.sp,
                color = MutedTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Dialog to search/select cities
@Composable
fun CitySelectorDialog(
    cities: List<CityData>,
    selectedCity: CityData,
    onDismiss: () -> Unit,
    onSelectCity: (CityData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCities = cities.filter {
        it.nameUz.contains(searchQuery, ignoreCase = true) || it.region.contains(searchQuery, ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 450.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Shahar tanlang (Uzbekistan)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Qidirish...", color = MutedTextColor) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextColor,
                        unfocusedTextColor = TextColor,
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = BorderColor,
                        cursorColor = PrimaryColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredCities) { city ->
                        val isSelected = city.id == selectedCity.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryColor.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { onSelectCity(city) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = city.nameUz,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PrimaryColor else TextColor
                                )
                                Text(
                                    text = city.region,
                                    fontSize = 11.sp,
                                    color = MutedTextColor
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(getAQIComposeColor(city.aqi).copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "AQI ${city.aqi}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = getAQIComposeColor(city.aqi)
                                    )
                                }
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Yopish", color = PrimaryColor)
                }
            }
        }
    }
}

// ==========================================
// 2. EXPOSITION MAP SCREEN WITH WEBVIEW INTEGRATION
// ==========================================
class WebAppInterface(private val onCitySelected: (String) -> Unit) {
    @JavascriptInterface
    fun postMessage(messageJson: String) {
        try {
            // Parses the JS callback {"type": "cityPress", "id": "toshkent"}
            val obj = org.json.JSONObject(messageJson)
            if (obj.getString("type") == "cityPress") {
                val cityId = obj.getString("id")
                onCitySelected(cityId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen(
    viewModel: IqlimoyViewModel,
    onSelectCityAndReturn: (String) -> Unit
) {
    val context = LocalContext.current
    val sortedRankings = remember { DemoData.CITIES.sortedByDescending { it.aqi } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "EKOLOGIK XARITA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextColor
                )
                Text(
                    text = "ESA Sentinel-5P sun'iy yo'ldosh monitoringi",
                    fontSize = 11.sp,
                    color = MutedTextColor
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF22D3A5).copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = null,
                    tint = Color(0xFF22D3A5),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "YANGI 30D",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22D3A5)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Leaflet Interactive WebView container of Web Maps!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .background(CardColor)
        ) {
            // Build the dynamic Leaflet Web page
            val htmlContent = remember {
                val citiesJsonArray = org.json.JSONArray()
                DemoData.CITIES.forEach { city ->
                    val cObj = org.json.JSONObject()
                    cObj.put("id", city.id)
                    cObj.put("name", city.nameUz)
                    cObj.put("aqi", city.aqi)
                    cObj.put("temp", city.temperature)
                    cObj.put("conditions", city.conditions)
                    
                    // Coordinates mapping
                    val (lat, lng) = when (city.id) {
                        "toshkent" -> Pair(41.2995, 69.2401)
                        "samarqand" -> Pair(39.6542, 66.9597)
                        "buxoro" -> Pair(39.7747, 64.4286)
                        "nukus" -> Pair(42.4619, 59.6076)
                        "fargona" -> Pair(40.3742, 71.7878)
                        "namangan" -> Pair(41.0022, 71.6726)
                        "andijon" -> Pair(40.7821, 72.3443)
                        "qarshi" -> Pair(38.8604, 65.7902)
                        else -> Pair(41.0, 64.0)
                    }
                    cObj.put("lat", lat)
                    cObj.put("lng", lng)
                    
                    // Hex AQI colors
                    val colorHex = when {
                        city.aqi <= 50 -> "#22D3A5"
                        city.aqi <= 100 -> "#F59E0B"
                        city.aqi <= 150 -> "#F97316"
                        city.aqi <= 200 -> "#EF4444"
                        else -> "#A855F7"
                    }
                    cObj.put("color", colorHex)
                    citiesJsonArray.put(cObj)
                }

                """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
                    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                    <style>
                        * { margin:0; padding:0; box-sizing:border-box; }
                        body, html { background:#F3F4F9; width:100%; height:100%; overflow:hidden; }
                        #map { width:100%; height:100vh; }
                        .leaflet-popup-content-wrapper {
                            background:#FFFFFF;
                            border:1px solid #E1E2EC;
                            border-radius:16px;
                            color:#0F172A;
                            font-family: sans-serif;
                            padding: 4px;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                        }
                        .leaflet-popup-tip { background:#FFFFFF; }
                        .leaflet-container { background:#F3F4F9; }
                        .leaflet-control-zoom { display:none; }
                        .leaflet-control-attribution { display:none; }
                        .pup-title { font-weight: bold; font-size: 14px; margin-bottom: 4px; color:#0F172A; }
                        .pup-aqi { font-size: 18px; font-weight: 800; margin: 4px 0; }
                        .pup-btn {
                            margin-top: 8px;
                            background: #0061A4;
                            border: none;
                            color: #FFFFFF;
                            border-radius: 8px;
                            padding: 6px 10px;
                            font-size: 11px;
                            font-weight: 800;
                            cursor: pointer;
                            width: 100%;
                            text-align: center;
                        }
                    </style>
                </head>
                <body>
                    <div id="map"></div>
                    <script>
                        var map = L.map('map', {
                            center: [40.8, 65.5],
                            zoom: 5,
                            zoomControl: false,
                            attributionControl: false
                        });

                        L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
                            maxZoom: 9,
                            minZoom: 4
                        }).addTo(map);

                        var cities = $citiesJsonArray;

                        cities.forEach(function(city) {
                            var icon = L.divIcon({
                                className: '',
                                html: '<div style="width:50px; height:50px; display:flex; align-items:center; justify-content:center; flex-direction:column; position:relative;">' +
                                      '<div style="width:40px; height:40px; border-radius:50%; background:' + city.color + '; opacity:0.15; position:absolute; animation:pulse 2s infinite;"></div>' +
                                      '<div style="width:34px; height:34px; border-radius:50%; background:' + city.color + '; border:2px solid rgba(255,255,255,0.3); display:flex; align-items:center; justify-content:center; z-index:1; box-shadow:0 2px 8px ' + city.color + '88;">' +
                                      '<span style="color:#fff; font-size:11px; font-weight:800; line-height:1;">' + city.aqi + '</span>' +
                                      '</div>' +
                                      '</div>',
                                iconSize: [50, 50],
                                iconAnchor: [25, 25]
                            });

                            var marker = L.marker([city.lat, city.lng], {icon: icon}).addTo(map);
                            
                            var popupContent = '<div class="pup-title">' + city.name + '</div>' +
                                               '<div class="pup-aqi" style="color:' + city.color + '">AQI ' + city.aqi + '</div>' +
                                               '<div style="font-size:11px; color:#64748B;">Harorat: ' + city.temp + '°C</div>' +
                                               '<button class="pup-btn" onclick="cityClicked(\'' + city.id + '\')">Batafsil Tanlash</button>';
                            
                            marker.bindPopup(popupContent);
                        });

                        function cityClicked(id) {
                            AndroidInterface.postMessage(JSON.stringify({type: 'cityPress', id: id}));
                        }
                    </script>
                </body>
                </html>
                """.trimIndent()
            }

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = WebViewClient()
                        addJavascriptInterface(
                            WebAppInterface { cityId ->
                                onSelectCityAndReturn(cityId)
                            },
                            "AndroidInterface"
                        )
                        loadDataWithBaseURL("https://leafletjs.com", htmlContent, "text/html", "UTF-8", null)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Standard references for AQI Index
        Text(
            text = "AQI DARALALARI",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedTextColor,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                Pair("Yaxshi", Color(0xFF22D3A5)),
                Pair("O'rtacha", Color(0xFFF59E0B)),
                Pair("Zararli", Color(0xFFF97316)),
                Pair("J.Zararli", Color(0xFFEF4444)),
                Pair("Xavfli", Color(0xFFA855F7))
            ).forEach { (lbl, clr) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(clr.copy(alpha = 0.15f))
                        .border(1.dp, clr.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lbl,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = clr
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Rankings table of Shaharlar
        Text(
            text = "HAVO IFLOSLANISHI BO'YICHA REYTING (YOMONDAN YAXSHIGA)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MutedTextColor,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sortedRankings.withIndex().toList()) { (idx, city) ->
                val clr = getAQIComposeColor(city.aqi)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardColor)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .clickable { onSelectCityAndReturn(city.id) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${idx + 1}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedTextColor,
                        modifier = Modifier.width(32.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = city.nameUz,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor
                        )
                        Text(
                            text = city.region,
                            fontSize = 11.sp,
                            color = MutedTextColor
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${city.temperature}°C",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(clr.copy(alpha = 0.16f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                    text = city.aqi.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = clr
                                )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. AI ASSISTANT CONVERSATION SCREEN
// ==========================================
@Composable
fun AssistantScreen(
    viewModel: IqlimoyViewModel,
    selectedCity: CityData
) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()
    val errorMsg by viewModel.error.collectAsStateWithLifecycle()

    var inputMessageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // AutoScroll on new message
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // AI Header Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "IQLIMOY AI ASSISTANT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextColor
                    )
                }
                Text(
                    text = "Ekologik tahlillar va maslahatlar yordamchisi",
                    fontSize = 11.sp,
                    color = MutedTextColor
                )
            }

            IconButton(
                onClick = { viewModel.clearChat() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Tozalash",
                    tint = MutedTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grounded context chip selectors
        Text(
            text = "TEZKOR SAVOLLAR",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MutedTextColor,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chips = listOf(
                "Toshkent AQI joriy ifloslanish ko'rsatkichi",
                "Chang bo'roni bo'yicha maslahatlar",
                "Geomagnit bo'ronining G2 ta'siri",
                "Qurilish maydonchalari ekologik xavfi"
            )
            items(chips) { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardColor)
                        .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                        .clickable { viewModel.sendMessage(chip) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(chip, fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chats bubbles thread
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(24.dp))
                .background(CardColor)
        ) {
            if (messages.isEmpty()) {
                // Welcoming Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = PrimaryColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Assalomu alaykum! Men Iqlimoy AI.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sizga O'zbekistondagi ekologik vaziyat, shahar AQI tahlillari, geomagnit bo'ronlar va ekologik salomatlik bo'yicha maslahat bera olaman. Quyosh nurlanishi yoki astma haqida so'rang!",
                        fontSize = 12.sp,
                        color = MutedTextColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { message ->
                        val bubbleColor = if (message.isUser) Color(0xFFD1E4FF) else Color.White
                        val bubbleTextColor = if (message.isUser) Color(0xFF001D36) else TextColor
                        val bubbleBorder = if (message.isUser) Color(0xFFD1E4FF) else BorderColor
                        val align = if (message.isUser) Alignment.End else Alignment.Start
                        val bubbleShape = if (message.isUser) {
                            RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
                        } else {
                            RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
                        }

                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
                            Box(
                                modifier = Modifier
                                    .clip(bubbleShape)
                                    .background(bubbleColor)
                                    .border(1.dp, bubbleBorder, bubbleShape)
                                    .padding(12.dp)
                                    .widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = message.text,
                                    fontSize = 13.sp,
                                    color = bubbleTextColor,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    if (isTyping) {
                        item {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BgColor)
                                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = PrimaryColor,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ma'lumotlar tahlil qilinmoqda...",
                                    fontSize = 11.sp,
                                    color = MutedTextColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (errorMsg != null) {
                        item {
                            Text(
                                text = errorMsg ?: "",
                                fontSize = 11.sp,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Message input text box bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputMessageText,
                onValueChange = { inputMessageText = it },
                placeholder = { Text("AI dan so'rang, masalan: 'astma maslahati'...", color = MutedTextColor, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = BorderColor,
                    cursorColor = PrimaryColor
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputMessageText.trim().isNotEmpty()) {
                        viewModel.sendMessage(inputMessageText)
                        inputMessageText = ""
                    }
                }),
                modifier = Modifier.weight(1f)
            )

            FloatingActionButton(
                onClick = {
                    if (inputMessageText.trim().isNotEmpty()) {
                        viewModel.sendMessage(inputMessageText)
                        inputMessageText = ""
                    }
                },
                containerColor = PrimaryColor,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ==========================================
// 4. SMART ALERTS NOTIFICATION CONTROL SCREEN
// ==========================================
@Composable
fun AlertsScreen(
    viewModel: IqlimoyViewModel
) {
    val activeFilter by viewModel.alertFilter.collectAsStateWithLifecycle()
    val allAlerts = remember { DemoData.ALERTS }

    // State counts
    val counts = remember(allAlerts) {
        mapOf(
            "all" to allAlerts.size,
            "active" to allAlerts.count { it.status == "active" },
            "monitoring" to allAlerts.count { it.status == "monitoring" },
            "resolved" to allAlerts.count { it.status == "resolved" }
        )
    }

    val criticalCount = remember(allAlerts) { allAlerts.count { it.severity == "critical" && it.status == "active" } }
    val activeCount = remember(allAlerts) { allAlerts.count { it.status == "active" } }

    val filteredAlerts = remember(activeFilter) {
        if (activeFilter == "all") allAlerts else allAlerts.filter { it.status == activeFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Alarms title header area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "OGOHLANTIRISHLAR",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextColor
                )
                Text(
                    text = "Ekologik va meteorologik krizis bildirishnomalari",
                    fontSize = 11.sp,
                    color = MutedTextColor
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (criticalCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.16f))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$criticalCount KRITIK",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF59E0B).copy(alpha = 0.16f))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$activeCount FAOL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal selectors of filtering feeds
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Pair("all", "Barchasi"),
                Pair("active", "Faol"),
                Pair("monitoring", "Kuzatuv"),
                Pair("resolved", "Tugallangan")
            ).forEach { (key, label) ->
                val isSelected = activeFilter == key
                val badgeColor = if (isSelected) PrimaryColor else CardColor
                val textColor = if (isSelected) Color.White else MutedTextColor

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(badgeColor)
                        .border(1.dp, if (isSelected) PrimaryColor else BorderColor, RoundedCornerShape(20.dp))
                        .clickable { viewModel.setAlertFilter(key) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White.copy(alpha = 0.25f) else BorderColor)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = (counts[key] ?: 0).toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MutedTextColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Alerts feeds items
        if (filteredAlerts.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF22D3A5),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Xavfli bildirishnomalar yo'q!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor
                )
                Text(
                    text = "Barcha tekshirilgan hududlarda havo xavfsiz va tinch.",
                    fontSize = 12.sp,
                    color = MutedTextColor
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredAlerts) { alert ->
                    val borderClr = when (alert.severity) {
                        "critical" -> Color(0xFFEF4444)
                        "high" -> Color(0xFFF97316)
                        "medium" -> Color(0xFFF59E0B)
                        else -> Color(0xFF22D3A5)
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardColor),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                // Draw high-end vertical state stripe
                                drawLine(
                                    color = borderClr,
                                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                    end = androidx.compose.ui.geometry.Offset(0f, size.height),
                                    strokeWidth = 4.dp.toPx()
                                )
                            }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (alert.type) {
                                            "dust" -> Icons.Default.Cloud
                                            "magnetic" -> Icons.Default.Explore
                                            "uv" -> Icons.Default.WbSunny
                                            "heat" -> Icons.Default.Thermostat
                                            else -> Icons.Default.Warning
                                        },
                                        contentDescription = null,
                                        tint = borderClr,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = alert.titleUz,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextColor
                                    )
                                }

                                // Status Indicator text
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (alert.status == "active") Color(0xFFEF4444).copy(alpha = 0.12f)
                                            else if (alert.status == "monitoring") Color(0xFFF59E0B).copy(alpha = 0.12f)
                                            else Color(0xFF22D3A5).copy(alpha = 0.12f)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (alert.status == "active") "FAOL" else if (alert.status == "monitoring") "KUZATUV" else "TUGALLANDI",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (alert.status == "active") Color(0xFFEF4444) else if (alert.status == "monitoring") Color(0xFFF59E0B) else Color(0xFF22D3A5)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = alert.description,
                                fontSize = 13.sp,
                                color = MutedTextColor,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // List of targeted regions
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = MutedTextColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Hududlar: " + alert.regions.joinToString(", "),
                                    fontSize = 11.sp,
                                    color = MutedTextColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. EXPLORE / MEDICAL WELLNESS SCREEN
// ==========================================
@Composable
fun ExploreScreen(
    viewModel: IqlimoyViewModel,
    selectedCity: CityData
) {
    val healthProfile by viewModel.healthProfile.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Activity index progress calculations (100 - AQI/2 - UV*2 - DustRisk*0.3)
    val activityScore = remember(selectedCity) {
        val score = 100 - (selectedCity.aqi * 0.5f) - (selectedCity.dustRisk * 0.3f) - (selectedCity.uvIndex * 2f)
        score.coerceIn(0f, 100f)
    }

    val scoreColor = when {
        activityScore >= 70 -> Color(0xFF22D3A5)
        activityScore >= 40 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "EKOLOGIK FAROVONLIK",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextColor
        )
        Text(
            text = "Yaqin zonalardagi HEPA-filtered markazlar va ekologik profil",
            fontSize = 11.sp,
            color = MutedTextColor
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Activity score panel
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tashqi Faoliyat Salomatlik Balli",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor
                        )
                        Text(
                            text = selectedCity.nameUz + " shahriga ko'ra",
                            fontSize = 12.sp,
                            color = MutedTextColor
                        )
                    }

                    // Score Circle
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(2.dp, scoreColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Math.round(activityScore).toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = scoreColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress slide bar
                LinearProgressIndicator(
                    progress = { activityScore / 100f },
                    color = scoreColor,
                    trackColor = BorderColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when {
                        activityScore >= 70 -> "Tashqi faoliyat va yugurish uchun juda qulay sharoit. Sport zonalari ochiq havo uchun tavsiya etiladi."
                        activityScore >= 40 -> "O'rtacha havo zaxirasi. Jismoniy ishlarni qisqartiring, ko'p bo'lmagan ochiq faoliyat va kosmetik quyosh kremidan himoya foydalaning."
                        else -> "Bugun havo ruhan og'ir va zararli. Tashqi faoliyatlarni mutlaq bekor qilinib, HEPA filtrlar barqaror kafemizda qoling!"
                    },
                    fontSize = 12.sp,
                    color = MutedTextColor,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Personal Health profile setup form
        Text(
            text = "FOYDALANUVCHINING SOG'LIQ PROFILI (JADVAL)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedTextColor,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sog'lig'ingiz haqida ma'lumot qoldiring. Bu shaxsiy AI (Gemini) maslahat berishida unga qo'shimcha kasallik va yosh ko'rsatkichlaringizni o'rgatadi.",
                    fontSize = 12.sp,
                    color = MutedTextColor,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text("Ismingiz", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextColor)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = healthProfile.name,
                    onValueChange = { viewModel.setProfileName(it) },
                    placeholder = { Text("Ismingiz...", color = MutedTextColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextColor,
                        unfocusedTextColor = TextColor,
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = BorderColor,
                        cursorColor = PrimaryColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Yosh Guruhingiz", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextColor)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Pair("child", "Bolalik"),
                        Pair("adult", "Kattalar"),
                        Pair("senior", "Kattalar 60+"),
                    ).forEach { (groupId, name) ->
                        val isSelected = healthProfile.ageGroup == groupId
                        val clr = if (isSelected) PrimaryColor else BorderColor
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryColor.copy(alpha = 0.12f) else TransparentBg)
                                .border(1.dp, clr, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setProfileAgeGroup(groupId) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(name, fontSize = 11.sp, color = if (isSelected) PrimaryColor else TextColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Surunkali holatlar / sezuvchanliklar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextColor)
                Spacer(modifier = Modifier.height(6.dp))

                val symptoms = listOf(
                    Pair("asthma", "Astma yoki bronxit"),
                    Pair("allergies", "Chang allergiyasi"),
                    Pair("heart", "Yurak qon-tomir kasalliklari"),
                    Pair("hypertension", "Gipertoniya")
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    symptoms.forEach { (sympId, sympLabel) ->
                        val hasSymp = healthProfile.conditions.contains(sympId)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleHealthCondition(sympId) }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = hasSymp,
                                onCheckedChange = { viewModel.toggleHealthCondition(sympId) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryColor,
                                    uncheckedColor = MutedTextColor,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(sympLabel, fontSize = 13.sp, color = TextColor)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Clean Air Partners listings
        Text(
            text = "TOZA HAVO HAMKORLARI (CHAS/HEPA FILTER)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedTextColor,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DemoData.PARTNERS.forEach { partner ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardColor),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PrimaryColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = partner.category,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryColor
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "HEPA ${partner.cleanAirScore}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF22D3A5)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = partner.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = partner.description,
                                fontSize = 11.sp,
                                color = MutedTextColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = partner.discount,
                                fontSize = 11.sp,
                                color = PrimaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = "Details",
                            tint = MutedTextColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Static About platform
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Iqlimoy App haqida v1.0",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Iqlimoy - O'zbekiston hududidan ekologiya, kimyoviy chang bo'ronlari va geomagnit o'zgarishlarni real vaqtda monitoring qiluvchi milliy platforma. Sentinel-5P sun'iy yo'ldoshlari asosida ishlaydi.",
                    fontSize = 12.sp,
                    color = MutedTextColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

val TransparentBg = Color.Transparent
