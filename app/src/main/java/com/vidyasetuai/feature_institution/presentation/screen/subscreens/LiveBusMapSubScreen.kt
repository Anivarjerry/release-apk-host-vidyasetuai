package com.vidyasetuai.feature_feed.presentation.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.ConsoleMessage
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    BackHandler(onBack = {
        viewModel.stopBusLocationTracking()
        onBack()
    })

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
    val isLive = busLocation?.isLive ?: false

    val latStr = String.format(java.util.Locale.US, "%.6f", initLat)
    val lngStr = String.format(java.util.Locale.US, "%.6f", initLon)
    val markerHtml = if (isLive) "🚌" else "🚎"
    val markerClass = if (isLive) "bus-marker-live" else "bus-marker-offline"

    // Generate HTML for Leaflet Map
    val mapHtml = remember(isDark, busRoutes) {
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
                    
                    L.Browser.any3d = false;
                    
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
                        }).setView([lat, lng], 15);
                        
                        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            maxZoom: 19
                        }).addTo(map);

                        var busIcon = L.divIcon({
                            html: latestCoords.html,
                            className: latestCoords.cssClass,
                            iconSize: [40, 40],
                            iconAnchor: [20, 20]
                        });

                        marker = L.marker([lat, lng], {icon: busIcon}).addTo(map);
                        
                        $stopsJs
                        
                        if ($isDark) {
                            var mapEl = document.getElementById('map');
                            mapEl.style.filter = 'invert(90%) hue-rotate(180deg) brightness(95%) contrast(90%)';
                        }

                        setTimeout(function() {
                            if (map) {
                                map.invalidateSize();
                            }
                        }, 200);
                    } catch (err) {
                        console.error("Exception during map init: " + err.message);
                    }
                }

                window.updateBusLocation = function(lat, lng, isLiveVal, html, cssClass) {
                    latestCoords = { lat: lat, lng: lng, isLive: isLiveVal, html: html, cssClass: cssClass };
                    if (!map || typeof L === 'undefined') {
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
                    } catch (err) {
                        console.error("Exception during update: " + err.message);
                    }
                };

                initMap();

                window.onload = function() {
                    if (map) {
                        map.invalidateSize();
                    }
                };
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    val isHtmlLoaded = remember { mutableStateOf(false) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    // Dynamic coordinates push to WebView
    val curLatStr = String.format(java.util.Locale.US, "%.6f", busLocation?.latitude ?: initLat)
    val curLngStr = String.format(java.util.Locale.US, "%.6f", busLocation?.longitude ?: initLon)

    LaunchedEffect(curLatStr, curLngStr, isHtmlLoaded.value, webViewRef.value) {
        val webView = webViewRef.value
        if (webView != null && isHtmlLoaded.value) {
            val js = "if (typeof window.updateBusLocation === 'function') { window.updateBusLocation($curLatStr, $curLngStr, $isLive, '$markerHtml', '$markerClass'); }"
            webView.evaluateJavascript(js, null)
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
                    IconButton(onClick = {
                        viewModel.stopBusLocationTracking()
                        onBack()
                    }) {
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
                            databaseEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36 VidyaSetuAI/1.0"
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isHtmlLoaded.value = true
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: android.webkit.WebResourceRequest?,
                                error: android.webkit.WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                Log.e("LiveBusMap_WebView", "Resource error: ${error?.description} (URL: ${request?.url})")
                            }
                        }
                        webChromeClient = object : android.webkit.WebChromeClient() {
                            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                Log.d("LiveBusMap_WebView", "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                                return true
                            }
                        }
                        webViewRef.value = this
                    }
                },
                update = { webView ->
                    webViewRef.value = webView
                    if (!isHtmlLoaded.value) {
                        webView.loadDataWithBaseURL("https://unpkg.com/", mapHtml, "text/html", "UTF-8", null)
                    }
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
                        val isLocationActive = busLocation?.isLive == true
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
                                    if (busLocation == null) {
                                        if (isHindi) "कनेक्टिंग..." else "Connecting..."
                                    } else {
                                        if (isHindi) "ऑफ़लाइन" else "Offline"
                                    }
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLocationActive) Color(0xFF065F46) else Color(0xFF9F1239)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                    )
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

