package com.vidyasetuai.feature_feed.presentation.screen

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveBusMapSubScreen(
    state: InstitutionUiState,
    busId: String,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    // Hardware back press handler
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardColor = if (isDark) Color(0xFF1E293B) else Color.White
    val titleColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Start tracking when screen enters
    LaunchedEffect(busId) {
        viewModel.startBusLocationTracking(busId)
    }

    val busLocation = state.activeBusLocation
    val busRoutes = state.activeBusRoutes[busId] ?: emptyList()

    // Find driver info from the synced buses list
    val busEntity = state.allBuses.find { it.id == busId }
    val driverName = busEntity?.driverName ?: (if (isHindi) "चालक" else "Driver")
    val driverPhone = busEntity?.driverMobile ?: ""
    val busNumber = busEntity?.busNumber ?: (if (isHindi) "स्कूल बस" else "School Bus")
    val routeName = busEntity?.routeName ?: ""

    // Initial bus coordinates
    val initLat = busLocation?.latitude ?: 26.9124 // Default Jaipur coordinates
    val initLon = busLocation?.longitude ?: 75.7873

    // Generate HTML for Leaflet Map
    val mapHtml = remember(busId, busRoutes) {
        val stopsJs = StringBuilder()
        busRoutes.forEach { stop ->
            if (stop.latitude != null && stop.longitude != null) {
                stopsJs.append("""
                    var stopIcon = L.divIcon({
                        html: '<div style="font-size:16px;">📍</div>',
                        iconSize: [20, 20],
                        iconAnchor: [10, 10]
                    });
                    L.marker([${stop.latitude}, ${stop.longitude}], {icon: stopIcon})
                        .addTo(map)
                        .bindPopup("${stop.stopName} (Order: ${stop.stopOrder})");
                """.trimIndent())
            }
        }

        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
            <style>
                body { padding: 0; margin: 0; overflow: hidden; background: #000; }
                html, body, #map { height: 100%; width: 100vw; }
                .leaflet-control-attribution { display: none !important; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', { zoomControl: false }).setView([$initLat, $initLon], 15);
                L.control.zoom({ position: 'topright' }).addTo(map);
                
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19
                }).addTo(map);

                var busIcon = L.divIcon({
                    html: '<div style="font-size:32px; filter: drop-shadow(0px 2px 4px rgba(0,0,0,0.4));">🚌</div>',
                    iconSize: [36, 36],
                    iconAnchor: [18, 18]
                });
                var busMarker = L.marker([$initLat, $initLon], {icon: busIcon}).addTo(map);

                $stopsJs

                function updateBusLocation(lat, lon) {
                    var newLatLng = new L.LatLng(lat, lon);
                    busMarker.setLatLng(newLatLng);
                    map.panTo(newLatLng);
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    // Keep WebView reference to push dynamic GPS coordinate updates
    var webViewRef: WebView? = null

    // Push coordinates to WebView in real-time
    LaunchedEffect(busLocation) {
        if (busLocation != null && webViewRef != null) {
            webViewRef?.evaluateJavascript(
                "updateBusLocation(${busLocation.latitude}, ${busLocation.longitude})",
                null
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "लाइव लोकेशन" else "Live Location",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = titleColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cardColor
                )
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
        ) {
            // Leaflet Map via AndroidView (WebView)
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                        }
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL("https://openstreetmap.org", mapHtml, "text/html", "UTF-8", null)
                        webViewRef = this
                    }
                },
                update = {
                    webViewRef = it
                }
            )

            // Bottom Driver Details Overlay Card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = busNumber,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = titleColor
                            )
                            if (routeName.isNotEmpty()) {
                                Text(
                                    text = routeName,
                                    fontSize = 12.sp,
                                    color = subtitleColor
                                )
                            }
                        }

                        // Tracking status
                        val isLocationActive = busLocation != null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isLocationActive) Color(0xFFD1FAE5) else Color(0xFFFFE4E6)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isLocationActive) {
                                    if (isHindi) "सक्रिय" else "Active"
                                } else {
                                    if (isHindi) "कनेक्टिंग..." else "Connecting..."
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLocationActive) Color(0xFF065F46) else Color(0xFF9F1239)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Lucide.User,
                                contentDescription = "Driver",
                                tint = subtitleColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = driverName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = titleColor
                                )
                                Text(
                                    text = if (isHindi) "वाहन चालक" else "Vehicle Driver",
                                    fontSize = 11.sp,
                                    color = subtitleColor
                                )
                            }
                        }

                        if (driverPhone.isNotEmpty()) {
                            Button(
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$driverPhone"))
                                    context.startActivity(dialIntent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Phone,
                                    contentDescription = "Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "कॉल करें" else "Call",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
