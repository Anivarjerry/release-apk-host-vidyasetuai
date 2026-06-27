package com.vidyasetuai.feature_feed.presentation.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LiveBusMapSubScreen(
    state: InstitutionUiState,
    busId: String,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    val busLocation = state.activeBusLocation
    val hasLocation = busLocation != null
    val isLive = busLocation?.isLive ?: false

    val latitude = busLocation?.latitude ?: 26.9124
    val longitude = busLocation?.longitude ?: 75.7873
    val speed = busLocation?.speed ?: 0.0

    val latStr = String.format(java.util.Locale.US, "%.6f", latitude)
    val lngStr = String.format(java.util.Locale.US, "%.6f", longitude)

    // Poll the bus live location from Supabase every 20 seconds
    LaunchedEffect(busId) {
        while (true) {
            viewModel.onEvent(InstitutionEvent.LoadBusLiveLocation(busId))
            delay(20000L)
        }
    }

    val lastUpdated = if (hasLocation) {
        try {
            val cleanStr = busLocation!!.updatedAt.replace(" ", "T")
            val updatedTime = try {
                java.time.Instant.parse(cleanStr)
            } catch (e: Exception) {
                try {
                    java.time.OffsetDateTime.parse(cleanStr).toInstant()
                } catch (e2: Exception) {
                    java.time.LocalDateTime.parse(cleanStr.substringBefore("+").substringBefore("Z")).toInstant(java.time.ZoneOffset.UTC)
                }
            }
            val zonedDateTime = updatedTime.atZone(java.time.ZoneId.systemDefault())
            zonedDateTime.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
        } catch (e: Exception) {
            "Just now"
        }
    } else {
        "N/A"
    }

    val markerHtml = if (isLive) "🚌" else "🚎"
    val markerClass = if (isLive) "bus-marker-live" else "bus-marker-offline"

    val isHtmlLoaded = remember(isDark) { mutableStateOf(false) }
    val webViewErrors = remember { androidx.compose.runtime.mutableStateListOf<String>() }
    val webViewRef = remember { mutableStateOf<android.webkit.WebView?>(null) }

    LaunchedEffect(latStr, lngStr, isHtmlLoaded.value, webViewRef.value) {
        val webView = webViewRef.value
        if (webView != null && isHtmlLoaded.value) {
            val js = "if (typeof window.updateBusLocation === 'function') { window.updateBusLocation($latStr, $lngStr, $isLive, '$markerHtml', '$markerClass'); }"
            webView.evaluateJavascript(js, null)
        }
    }

    val htmlString = remember(isDark) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="icon" href="data:,">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" onerror="this.onerror=null;this.href='https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.css';" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" onerror="var s=document.createElement('script');s.src='https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.js';document.head.appendChild(s);"></script>
            <style>
                html, body {
                    height: 100%;
                    width: 100%;
                    margin: 0;
                    padding: 0;
                    background-color: ${if (isDark) "#1c1c1e" else "#f4f6f5"};
                    overflow: hidden;
                }
                #map {
                    position: fixed;
                    top: 0;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    height: 100%;
                    width: 100%;
                    background-color: ${if (isDark) "#1c1c1e" else "#f4f6f5"};
                }
                .bus-marker-live {
                    font-size: 32px;
                    text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
                    text-align: center;
                    animation: pulse 1.8s infinite;
                }
                .bus-marker-offline {
                    font-size: 32px;
                    text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
                    text-align: center;
                    opacity: 0.6;
                    filter: grayscale(100%);
                }
                @keyframes pulse {
                    0% { transform: scale(1); }
                    50% { transform: scale(1.2); }
                    100% { transform: scale(1); }
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                console.log("JS script block started execution");
                var map;
                var marker;
                var latestCoords = { lat: $latStr, lng: $lngStr, isLive: $isLive, html: '$markerHtml', cssClass: '$markerClass' };
                var retryCount = 0;

                function initMap() {
                    console.log("initMap called. typeof L: " + typeof L + ", retry: " + retryCount);
                    if (typeof L === 'undefined') {
                        retryCount++;
                        if (retryCount < 50) {
                            setTimeout(initMap, 100);
                        } else {
                            console.error("Leaflet (L) remained undefined after 5 seconds!");
                        }
                        return;
                    }
                    
                    // Force Leaflet to use 2D CSS transforms instead of 3D to bypass hardware rendering issues in WebView
                    L.Browser.any3d = false;
                    
                    console.log("Leaflet is defined! Initializing map at: " + latestCoords.lat + ", " + latestCoords.lng);
                    console.log("Map container client size: " + document.getElementById('map').clientWidth + "x" + document.getElementById('map').clientHeight);
                    var lat = parseFloat(latestCoords.lat);
                    var lng = parseFloat(latestCoords.lng);
                    if (isNaN(lat) || isNaN(lng)) {
                        lat = 26.9124;
                        lng = 75.7873;
                    }
                    
                    try {
                        map = L.map('map', {
                            zoomControl: false,
                            attributionControl: false
                        }).setView([lat, lng], 16);
                        console.log("Map instance created successfully");
                        
                        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            maxZoom: 19
                        }).addTo(map);
                        console.log("Tile layer added successfully");

                        var busIcon = L.divIcon({
                            html: latestCoords.html,
                            className: latestCoords.cssClass,
                            iconSize: [40, 40],
                            iconAnchor: [20, 20]
                        });

                        marker = L.marker([lat, lng], {icon: busIcon}).addTo(map);
                        console.log("Marker added successfully");
                        
                        if ($isDark) {
                            var mapEl = document.getElementById('map');
                            mapEl.style.filter = 'invert(90%) hue-rotate(180deg) brightness(95%) contrast(90%)';
                            console.log("Dark mode filter applied to map");
                        }

                        setTimeout(function() {
                            if (map) {
                                map.invalidateSize();
                                console.log("map.invalidateSize() completed");
                            }
                        }, 200);
                    } catch (err) {
                        console.error("Exception during map init: " + err.message + "\n" + err.stack);
                    }
                }

                // JS function to dynamically update marker location and map center
                window.updateBusLocation = function(lat, lng, isLiveVal, html, cssClass) {
                    console.log("updateBusLocation JS called with: " + lat + ", " + lng + ". Client size: " + document.getElementById('map').clientWidth + "x" + document.getElementById('map').clientHeight);
                    latestCoords = { lat: lat, lng: lng, isLive: isLiveVal, html: html, cssClass: cssClass };
                    if (!map || typeof L === 'undefined') {
                        console.log("Cannot update yet, map or L is not ready");
                        return;
                    }
                    
                    try {
                        map.invalidateSize();
                        var newLatLng = new L.LatLng(lat, lng);
                        marker.setLatLng(newLatLng);
                        map.panTo(newLatLng);
                        
                        var newIcon = L.divIcon({
                            html: html,
                            className: cssClass,
                            iconSize: [40, 40],
                            iconAnchor: [20, 20]
                        });
                        marker.setIcon(newIcon);
                        console.log("Bus location updated successfully in UI");
                    } catch (err) {
                        console.error("Exception during update: " + err.message);
                    }
                };

                initMap();

                window.onload = function() {
                    console.log("Window onload fired");
                    if (map) {
                        map.invalidateSize();
                        console.log("onload invalidateSize completed");
                    }
                };
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // WebView for Leaflet Map
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { ctx ->
                android.webkit.WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36 VidyaSetuAI/1.0"
                    
                    webViewClient = object : android.webkit.WebViewClient() {
                        override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isHtmlLoaded.value = true
                        }

                        override fun onReceivedError(
                            view: android.webkit.WebView?,
                            request: android.webkit.WebResourceRequest?,
                            error: android.webkit.WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            val errorMsg = "Resource error: ${error?.description} (URL: ${request?.url})"
                            Log.e("LiveBusMap_WebView", errorMsg)
                        }

                        override fun onReceivedHttpError(
                            view: android.webkit.WebView?,
                            request: android.webkit.WebResourceRequest?,
                            errorResponse: android.webkit.WebResourceResponse?
                        ) {
                            super.onReceivedHttpError(view, request, errorResponse)
                            val errorMsg = "HTTP error: ${errorResponse?.statusCode} ${errorResponse?.reasonPhrase} (URL: ${request?.url})"
                            Log.e("LiveBusMap_WebView", errorMsg)
                        }
                    }
                    webChromeClient = object : android.webkit.WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                            val msg = consoleMessage?.message() ?: ""
                            Log.d("LiveBusMap_WebView", "$msg -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                            return true
                        }
                    }
                    webViewRef.value = this
                }
            },
            update = { webView ->
                webViewRef.value = webView
                if (!isHtmlLoaded.value) {
                    webView.loadDataWithBaseURL("https://unpkg.com/", htmlString, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Custom Header Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(
                        if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f),
                        CircleShape
                    )
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Live/Offline Warning Banner if Offline
        if (!isLive) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 64.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Lucide.TriangleAlert, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "बस ऑफ़लाइन है (अंतिम ज्ञात स्थान)" else "Bus is Offline (Showing last known location)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Speed and Status Overlay Panel at the bottom
        val cardBg = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f)
        val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
        
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val assignedBus = state.studentBuses.find { it.busId == busId }
                    Column {
                        Text(
                            text = assignedBus?.busNumber ?: "Bus Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = assignedBus?.routeName ?: (if (isHindi) "रूट विवरण अनुपलब्ध" else "Route details unavailable"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Pulsing Dot / Badge
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isLive) AppColors.EmeraldGreen.copy(alpha = 0.12f) else Color(0xFFEF4444).copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(if (isLive) AppColors.EmeraldGreen else Color(0xFFEF4444), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isLive) {
                                    if (isHindi) "ट्रैकिंग सक्रिय" else "Live Tracking"
                                } else {
                                    if (isHindi) "ऑफ़लाइन" else "Offline"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLive) AppColors.EmeraldGreen else Color(0xFFEF4444)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(MaterialTheme.colorScheme.outlineVariant))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = if (isHindi) "चाल (Speed)" else "Current Speed", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (isLive) "${speed.toInt()} km/h" else "0 km/h",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLive) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = if (isHindi) "अंतिम अपडेट" else "Last Updated", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = lastUpdated,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
