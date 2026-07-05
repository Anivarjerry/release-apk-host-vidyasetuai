package com.vidyasetuai.feature_institution.presentation.component

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.domain.model.BusLiveLocation
import com.vidyasetuai.feature_institution.domain.model.BusRouteStop
import com.vidyasetuai.feature_institution.domain.model.StudentBusAssignment
import com.vidyasetuai.feature_institution.data.local.entity.WorkspaceEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.atan2
import kotlin.math.sqrt

// ── In-Memory Cache for Bus Live Locations ──────────────────────────────────
object InMemoryBusLocationCache {
    private val cache = java.util.concurrent.ConcurrentHashMap<String, BusLiveLocation>()
    
    fun get(busId: String): BusLiveLocation? = cache[busId]
    
    fun put(busId: String, location: BusLiveLocation) {
        cache[busId] = location
    }
}

// ── Internet Checker Utility ────────────────────────────────────────────────
private fun isInternetAvailable(context: android.content.Context): Boolean {
    val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// ── Robust API-24 postgres timestamp parser ──────────────────────────────────
private fun parsePostgresTimestampToEpoch(timestampStr: String): Long {
    val cleanStr = timestampStr.replace("T", " ").substringBefore("+").substringBefore("Z")
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
    sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
    return try {
        val baseTime = sdf.parse(cleanStr)?.time ?: System.currentTimeMillis()
        if (cleanStr.contains(".")) {
            val msStr = cleanStr.substringAfter(".").take(3).padEnd(3, '0')
            val ms = msStr.toLongOrNull() ?: 0L
            baseTime + ms
        } else {
            baseTime
        }
    } catch (_: Exception) {
        System.currentTimeMillis()
    }
}

// Helper data class to store student home stop info
data class StudentHomeLocationStop(
    val studentName: String,
    val firstLetter: String,
    val nearestStopIdx: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardBusTrackingCard(
    role: String,
    studentBuses: List<StudentBusAssignment>,
    allBuses: List<com.vidyasetuai.feature_institution.data.local.entity.LocalParentBusEntity>,
    activeBusRoutes: Map<String, List<BusRouteStop>>,
    @Suppress("UNUSED_PARAMETER") activeBusLocation: BusLiveLocation?,
    isHindi: Boolean,
    isDark: Boolean,
    onViewAllClick: () -> Unit
) {
    val isAdmin = role in listOf("Admin", "System Administrator", "School Administrator", "Org Admin", "Principal", "Director", "Owner", "Transport Manager")
    val hasBuses = if (isAdmin) allBuses.isNotEmpty() else studentBuses.isNotEmpty()
    if (!hasBuses) return

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    val cardBgColor = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val titleColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)

    val rotation = remember { Animatable(0f) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotation.snapTo(0f)
        }
    }

    val displayBuses = remember(isAdmin, allBuses, studentBuses) {
        if (isAdmin) {
            allBuses.take(3).map { bus ->
                StudentBusAssignment(
                    studentId = "",
                    studentName = bus.busName ?: bus.busNumber,
                    busId = bus.id,
                    busNumber = bus.busNumber,
                    busName = bus.busName,
                    routeName = bus.routeName,
                    pickupStop = null
                )
            }
        } else {
            studentBuses.take(3)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Bus,
                        contentDescription = "Bus",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "बस लाइव ट्रैकिंग" else "Live Bus Tracking",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sync / Refresh Button
                    Icon(
                        imageVector = Lucide.RefreshCw,
                        contentDescription = "Sync",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(rotation.value)
                            .clickable(enabled = !isRefreshing) {
                                if (!isInternetAvailable(context)) {
                                    Toast.makeText(
                                        context,
                                        if (isHindi) "कृपया इंटरनेट चालू करें" else "Please connect to the internet",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    isRefreshing = true
                                    scope.launch {
                                        try {
                                            val remoteDS = com.vidyasetuai.feature_institution.data.remote.datasource.InstitutionRemoteDataSource()
                                            displayBuses.forEach { bus ->
                                                val remoteLoc = remoteDS.fetchBusLiveLocation(bus.busId)
                                                if (remoteLoc != null) {
                                                    val cleanStr = remoteLoc.updated_at.replace(" ", "T")
                                                    val updatedEpoch = parsePostgresTimestampToEpoch(cleanStr)
                                                    val diffMs = System.currentTimeMillis() - updatedEpoch
                                                    val isLive = abs(diffMs) < 120 * 1000
                                                    
                                                    val busLive = BusLiveLocation(
                                                        busId = remoteLoc.bus_id,
                                                        latitude = remoteLoc.latitude,
                                                        longitude = remoteLoc.longitude,
                                                        speed = remoteLoc.speed,
                                                        updatedAt = remoteLoc.updated_at,
                                                        isLive = isLive
                                                    )
                                                    InMemoryBusLocationCache.put(bus.busId, busLive)
                                                }
                                            }
                                            refreshTrigger++
                                        } catch (_: Exception) {
                                            // Fail silently
                                        } finally {
                                            isRefreshing = false
                                        }
                                    }
                                }
                            }
                    )

                    Text(
                        text = if (isHindi) "सभी देखें >" else "View All >",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.EmeraldGreen,
                        modifier = Modifier.clickable { onViewAllClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            displayBuses.forEachIndexed { index, assignment ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(borderColor)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                BusRouteRow(
                    assignment = assignment,
                    routeStops = activeBusRoutes[assignment.busId] ?: emptyList(),
                    isHindi = isHindi,
                    isDark = isDark,
                    refreshTrigger = refreshTrigger
                )
            }
        }
    }
}

@Composable
fun BusRouteRow(
    assignment: StudentBusAssignment,
    routeStops: List<BusRouteStop>,
    isHindi: Boolean,
    isDark: Boolean,
    refreshTrigger: Int
) {
    val context = LocalContext.current
    val titleColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Load from in-memory cache to maintain states across recompositions
    val syncedLocation = remember(assignment.busId, refreshTrigger) {
        InMemoryBusLocationCache.get(assignment.busId)
    }

    val sortedStops = remember(routeStops) {
        routeStops.sortedBy { it.stopOrder }
    }

    // Proximity checks for student home location
    var activeWorkspace by remember { mutableStateOf<WorkspaceEntity?>(null) }
    var studentHomeStops by remember { mutableStateOf<List<StudentHomeLocationStop>>(emptyList()) }

    LaunchedEffect(assignment.studentId, activeWorkspace, sortedStops) {
        val db = com.vidyasetuai.core.database.AppDatabase.getDatabase(context)
        if (activeWorkspace == null) {
            activeWorkspace = db.institutionDao().getActiveWorkspace()
        }
        val workspace = activeWorkspace ?: return@LaunchedEffect
        
        if (sortedStops.isNotEmpty()) {
            val list = mutableListOf<StudentHomeLocationStop>()
            if (workspace.workspaceRole.equals("STUDENT", ignoreCase = true)) {
                val studentId = workspace.studentId ?: ""
                if (studentId.isNotEmpty()) {
                    val student = db.institutionDao().getStudentById(studentId)
                    if (student != null && student.homeLatitude != null && student.homeLongitude != null) {
                        var minDistance = Double.MAX_VALUE
                        var closestIdx = -1
                        sortedStops.forEachIndexed { idx, stop ->
                            if (stop.latitude != null && stop.longitude != null) {
                                val distance = calculateDistance(student.homeLatitude, student.homeLongitude, stop.latitude, stop.longitude)
                                if (distance < minDistance) {
                                    minDistance = distance
                                    closestIdx = idx
                                }
                            }
                        }
                        if (closestIdx != -1) {
                            list.add(
                                StudentHomeLocationStop(
                                    studentName = student.name ?: "",
                                    firstLetter = (student.name ?: "S").take(1).uppercase(),
                                    nearestStopIdx = closestIdx
                                )
                            )
                        }
                    }
                }
            } else if (workspace.workspaceRole.equals("GUARDIAN", ignoreCase = true)) {
                val guardianId = workspace.guardianId ?: ""
                if (guardianId.isNotEmpty()) {
                    val students = db.institutionDao().getStudentsByGuardianId(guardianId)
                    students.forEach { student ->
                        if (student.homeLatitude != null && student.homeLongitude != null) {
                            var minDistance = Double.MAX_VALUE
                            var closestIdx = -1
                            sortedStops.forEachIndexed { idx, stop ->
                                if (stop.latitude != null && stop.longitude != null) {
                                    val distance = calculateDistance(student.homeLatitude, student.homeLongitude, stop.latitude, stop.longitude)
                                    if (distance < minDistance) {
                                        minDistance = distance
                                        closestIdx = idx
                                    }
                                }
                            }
                            if (closestIdx != -1) {
                                list.add(
                                    StudentHomeLocationStop(
                                        studentName = student.name ?: "",
                                        firstLetter = (student.name ?: "S").take(1).uppercase(),
                                        nearestStopIdx = closestIdx
                                    )
                                )
                            }
                        }
                    }
                }
            }
            studentHomeStops = list
        }
    }

    var currentStopIndex = -1
    // By default, if syncedLocation is null OR updated more than 2 minutes ago, tracking is offline.
    var isTrackingOnline = false

    if (syncedLocation != null) {
        val cleanStr = syncedLocation.updatedAt.replace(" ", "T")
        val updatedEpoch = parsePostgresTimestampToEpoch(cleanStr)
        val diffMs = System.currentTimeMillis() - updatedEpoch
        isTrackingOnline = abs(diffMs) < 120 * 1000
    }

    val busLat = if (isTrackingOnline) syncedLocation?.latitude else null
    val busLon = if (isTrackingOnline) syncedLocation?.longitude else null

    if (busLat != null && busLon != null && sortedStops.isNotEmpty()) {
        var minDistance = Double.MAX_VALUE
        sortedStops.forEachIndexed { idx, stop ->
            if (stop.latitude != null && stop.longitude != null) {
                val distance = calculateDistance(busLat, busLon, stop.latitude, stop.longitude)
                if (distance < minDistance) {
                    minDistance = distance
                    currentStopIndex = idx
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Info Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isHindi) "विद्यार्थी: ${assignment.studentName}" else "Student: ${assignment.studentName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor
                )
                Text(
                    text = "${assignment.busNumber} • ${assignment.routeName ?: ""}",
                    fontSize = 12.sp,
                    color = subtitleColor
                )
            }

            // Status Badge
            val statusText = when {
                syncedLocation == null -> if (isHindi) "शुरू नहीं हुई" else "Not Started"
                !isTrackingOnline -> if (isHindi) "ऑफ़लाइन" else "Offline"
                else -> if (isHindi) "ट्रैकिंग सक्रिय" else "Tracking Active"
            }
            val badgeBg = when {
                syncedLocation == null -> if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
                !isTrackingOnline -> if (isDark) Color(0xFF3A3A3C) else Color(0xFFE5E5EA)
                else -> Color(0xFFE8F5E9)
            }
            val badgeTextColor = when {
                syncedLocation == null -> subtitleColor
                !isTrackingOnline -> subtitleColor
                else -> Color(0xFF2E7D32)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Metro Timeline View
        MetroTimeline(
            stops = sortedStops,
            nearestStopIdx = currentStopIndex,
            isTrackingOnline = isTrackingOnline,
            studentHomeStops = studentHomeStops,
            isDark = isDark,
            isHindi = isHindi
        )
    }
}

@Composable
fun MetroTimeline(
    stops: List<BusRouteStop>,
    nearestStopIdx: Int,
    isTrackingOnline: Boolean,
    studentHomeStops: List<StudentHomeLocationStop>,
    isDark: Boolean,
    isHindi: Boolean
) {
    if (stops.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA), RoundedCornerShape(2.dp))
        )
        return
    }

    val lineColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val activeColor = AppColors.EmeraldGreen
    val textStyleColor = if (isDark) Color(0xFF8E8E93) else Color(0xFF8E8E93)

    // Total points = stops.size + 2 (Start School, intermediate stops, End School)
    val totalPoints = stops.size + 2
    
    // If online, active stop is nearestStopIdx + 1. If offline, it is always at School (Point 0).
    val activePointIdx = if (isTrackingOnline && nearestStopIdx != -1) nearestStopIdx + 1 else 0

    var widthDp by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .onGloballyPositioned { layoutCoordinates ->
                widthDp = with(density) { layoutCoordinates.size.width.toDp() }
            }
    ) {
        val timelineY = 32.dp // Shift timeline down to leave space for student home icons

        // Canvas for line path drawing
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            val y = timelineY.toPx()
            val widthPx = size.width

            // 1. Draw Inactive Gray Base Line
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(widthPx, y),
                strokeWidth = 4.dp.toPx()
            )

            // 2. Draw Active Green Path (If activePointIdx > 0, indicating online & progress)
            if (isTrackingOnline && activePointIdx > 0) {
                val activeFraction = activePointIdx.toFloat() / (totalPoints - 1)
                val activeXPx = widthPx * activeFraction
                
                drawLine(
                    color = activeColor,
                    start = Offset(0f, y),
                    end = Offset(activeXPx, y),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }

        // Overlay dots and icons
        for (i in 0 until totalPoints) {
            val fraction = i.toFloat() / (totalPoints - 1)
            val xOffset = widthDp * fraction

            // Check if point is Start School (0) or End School (totalPoints - 1)
            val isSchoolPoint = i == 0 || i == totalPoints - 1
            val pointRadius = if (isSchoolPoint) 8.dp else 4.dp
            val isPassedOrAt = isTrackingOnline && i <= activePointIdx
            val pointColor = if (isPassedOrAt) activeColor else lineColor

            // Draw dot
            Box(
                modifier = Modifier
                    .offset(x = xOffset - pointRadius, y = timelineY - pointRadius)
                    .size(pointRadius * 2)
                    .background(pointColor, CircleShape)
            )
        }

        // 3. Draw Student Home Location Icons (Only for Guardian/Student roles)
        studentHomeStops.forEach { homeStop ->
            val homePointIdx = homeStop.nearestStopIdx + 1
            val fraction = homePointIdx.toFloat() / (totalPoints - 1)
            val xOffset = widthDp * fraction
            
            // Draw a beautiful small MapPin icon + child's first letter badge above the timeline line
            Row(
                modifier = Modifier
                    .offset(x = xOffset - 18.dp, y = timelineY - 26.dp)
                    .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7), RoundedCornerShape(4.dp))
                    .border(0.5.dp, if (isDark) Color(0xFF3A3A3C) else Color(0xFFE5E5EA), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Lucide.MapPin,
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .background(Color(0xFF3B82F6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = homeStop.firstLetter,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 4. Draw active Bus Pin Icon (At activePointIdx)
        val fraction = activePointIdx.toFloat() / (totalPoints - 1)
        val xOffset = widthDp * fraction
        
        Box(
            modifier = Modifier
                .offset(x = xOffset - 14.dp, y = timelineY - 14.dp)
                .size(28.dp)
                .background(if (isTrackingOnline) Color(0xFF3B82F6) else Color(0xFF8E8E93), CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.MapPin,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        // ── Labels Row at the bottom ─────────────────────────────────────────
        // 1. Start point label (School)
        Text(
            text = if (isHindi) "विद्यालय" else "School",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textStyleColor,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 2.dp)
        )

        // 2. Synced active stop label in center (under matched stop dot)
        if (isTrackingOnline && activePointIdx > 0 && nearestStopIdx in stops.indices) {
            val constrainedX = (xOffset - 50.dp).coerceIn(8.dp, widthDp - 108.dp)

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = constrainedX)
                    .width(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Lucide.MapPin,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = stops[nearestStopIdx].stopName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B82F6),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 3. End point label (School)
        Text(
            text = if (isHindi) "विद्यालय" else "School",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textStyleColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 2.dp)
        )
    }
}

// ── Haversine Proximity/Distance helper ─────────────────────────────────────
private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371000.0 // Earth radius in meters
    val phi1 = Math.toRadians(lat1)
    val phi2 = Math.toRadians(lat2)
    val deltaPhi = Math.toRadians(lat2 - lat1)
    val deltaLambda = Math.toRadians(lon2 - lon1)
    
    val a = sin(deltaPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}
