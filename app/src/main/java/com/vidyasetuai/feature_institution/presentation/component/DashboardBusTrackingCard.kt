package com.vidyasetuai.feature_institution.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.domain.model.BusLiveLocation
import com.vidyasetuai.feature_institution.domain.model.BusRouteStop
import com.vidyasetuai.feature_institution.domain.model.StudentBusAssignment
import kotlin.math.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardBusTrackingCard(
    role: String,
    studentBuses: List<StudentBusAssignment>,
    allBuses: List<com.vidyasetuai.feature_institution.data.local.entity.LocalParentBusEntity>,
    activeBusRoutes: Map<String, List<BusRouteStop>>,
    activeBusLocation: BusLiveLocation?,
    isHindi: Boolean,
    isDark: Boolean,
    onViewAllClick: () -> Unit
) {
    val isAdmin = role in listOf("Admin", "System Administrator", "School Administrator", "Org Admin", "Principal", "Director", "Owner", "Transport Manager")
    val hasBuses = if (isAdmin) allBuses.isNotEmpty() else studentBuses.isNotEmpty()
    if (!hasBuses) return

    val cardBgColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.9f) else Color.White
    val borderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val titleColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onViewAllClick() },
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        shape = RoundedCornerShape(16.dp),
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
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "बस लाइव ट्रैकिंग" else "Live Bus Tracking",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    
                    // Live pulsing indicator if any bus has live data
                    val isLive = activeBusLocation?.isLive == true
                    if (isLive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        LiveIndicator()
                    }
                }
                
                Text(
                    text = if (isHindi) "सभी देखें >" else "View All >",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF10B981),
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Limits to maximum 3 routes to maintain card proportions
            val displayBuses = if (isAdmin) {
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
            displayBuses.forEachIndexed { index, assignment ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Divider between children
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
                    activeBusLocation = if (activeBusLocation?.busId == assignment.busId) activeBusLocation else null,
                    isHindi = isHindi,
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
fun LiveIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(Color(0xFFEF4444), shape = CircleShape)
    )
}

@Composable
fun BusRouteRow(
    assignment: StudentBusAssignment,
    routeStops: List<BusRouteStop>,
    activeBusLocation: BusLiveLocation?,
    isHindi: Boolean,
    isDark: Boolean
) {
    val titleColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Calculate details
    val busLat = activeBusLocation?.latitude
    val busLon = activeBusLocation?.longitude
    val pickupStopName = assignment.pickupStop ?: ""

    val stopsCount = routeStops.size
    val pickupIndex = routeStops.indexOfFirst { it.stopName.equals(pickupStopName, ignoreCase = true) }
    
    var currentStopIndex = -1
    var remainingStops = -1
    var currentStopName = ""
    var tripNotStarted = true

    if (busLat != null && busLon != null && routeStops.isNotEmpty()) {
        tripNotStarted = activeBusLocation?.isLive == false
        
        // Find nearest stop to bus
        var minDistance = Double.MAX_VALUE
        routeStops.forEachIndexed { idx, stop ->
            if (stop.latitude != null && stop.longitude != null) {
                val distance = calculateDistance(busLat, busLon, stop.latitude, stop.longitude)
                if (distance < minDistance) {
                    minDistance = distance
                    currentStopIndex = idx
                }
            }
        }
        
        if (currentStopIndex != -1) {
            currentStopName = routeStops[currentStopIndex].stopName
            if (pickupIndex != -1) {
                remainingStops = pickupIndex - currentStopIndex
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Child & Bus info
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
            
            // ETA or Status Badge
            val statusText = when {
                tripNotStarted || routeStops.isEmpty() -> if (isHindi) "शुरू नहीं हुई" else "Not Started"
                remainingStops > 0 -> if (isHindi) "${remainingStops} स्टॉप दूर" else "${remainingStops} stops away"
                remainingStops == 0 -> if (isHindi) "पहुंच गई" else "Arrived"
                remainingStops < 0 -> if (isHindi) "निकल चुकी" else "Departed"
                else -> if (isHindi) "ट्रैकिंग सक्रिय" else "Tracking Active"
            }
            val badgeBg = when {
                tripNotStarted -> if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)
                remainingStops == 0 -> Color(0xFFD1FAE5)
                remainingStops > 0 -> Color(0xFFFEF3C7)
                else -> if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
            }
            val badgeTextColor = when {
                tripNotStarted -> subtitleColor
                remainingStops == 0 -> Color(0xFF065F46)
                remainingStops > 0 -> Color(0xFF92400E)
                else -> subtitleColor
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

        // Metro timeline layout
        MetroTimeline(
            stops = routeStops,
            currentBusIndex = currentStopIndex,
            pickupIndex = pickupIndex,
            isDark = isDark,
            isHindi = isHindi,
            tripNotStarted = tripNotStarted
        )
    }
}

@Composable
fun MetroTimeline(
    stops: List<BusRouteStop>,
    currentBusIndex: Int,
    pickupIndex: Int,
    isDark: Boolean,
    isHindi: Boolean,
    tripNotStarted: Boolean
) {
    if (stops.isEmpty()) {
        // Fallback simple line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
        )
        return
    }

    val lineColor = if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1)
    val activeColor = Color(0xFF10B981) // Green
    val textStyleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Let's decide which nodes to show
    // We only show up to 4 key nodes: First stop, Current Bus stop, Student Stop, and Last stop
    // To fit nicely on mobile screens:
    // Node 1: First stop (S_0)
    // Node 2: Bus (Current Stop) - if started
    // Node 3: Home/Pickup (Student Stop)
    // Node 4: Destination (Last Stop)

    val firstStop = stops.first()
    val lastStop = stops.last()
    val studentStop = if (pickupIndex != -1) stops[pickupIndex] else null

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(20.dp)
            ) {
                val width = size.width
                val y = size.height / 2

                // Draw base line
                drawLine(
                    color = lineColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 4.dp.toPx()
                )

                // If trip started and bus position is valid
                if (!tripNotStarted && currentBusIndex != -1) {
                    val busProgress = currentBusIndex.toFloat() / (stops.size - 1).coerceAtLeast(1)
                    val busX = width * busProgress
                    
                    // Draw green active route up to bus
                    drawLine(
                        color = activeColor,
                        start = Offset(0f, y),
                        end = Offset(busX, y),
                        strokeWidth = 4.dp.toPx()
                    )

                    // Draw dotted active line between Bus and Student Pickup if bus hasn't passed it
                    if (pickupIndex != -1 && currentBusIndex < pickupIndex) {
                        val studentProgress = pickupIndex.toFloat() / (stops.size - 1).coerceAtLeast(1)
                        val studentX = width * studentProgress
                        
                        drawLine(
                            color = activeColor,
                            start = Offset(busX, y),
                            end = Offset(studentX, y),
                            strokeWidth = 4.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }
                }

                // Draw circles for nodes
                // Node 1: Start (First Stop)
                drawCircle(
                    color = if (!tripNotStarted && currentBusIndex >= 0) activeColor else lineColor,
                    radius = 5.dp.toPx(),
                    center = Offset(0f, y)
                )

                // Node 2: Student Pickup stop (Highlighted)
                if (pickupIndex != -1) {
                    val studentProgress = pickupIndex.toFloat() / (stops.size - 1).coerceAtLeast(1)
                    val studentX = width * studentProgress
                    
                    val isBusPassed = !tripNotStarted && currentBusIndex > pickupIndex
                    val isBusAtStop = !tripNotStarted && currentBusIndex == pickupIndex

                    drawCircle(
                        color = when {
                            isBusAtStop -> Color(0xFFEF4444) // Red for arrival
                            isBusPassed -> activeColor
                            else -> Color(0xFF3B82F6) // Blue for student home stop
                        },
                        radius = 7.dp.toPx(),
                        center = Offset(studentX, y)
                    )
                }

                // Node 3: Bus current location (as a pulsing circle or indicator)
                if (!tripNotStarted && currentBusIndex != -1) {
                    val busProgress = currentBusIndex.toFloat() / (stops.size - 1).coerceAtLeast(1)
                    val busX = width * busProgress

                    drawCircle(
                        color = activeColor,
                        radius = 7.dp.toPx(),
                        center = Offset(busX, y)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(busX, y)
                    )
                }

                // Node 4: End Destination (Last Stop)
                drawCircle(
                    color = if (!tripNotStarted && currentBusIndex >= stops.size - 1) activeColor else lineColor,
                    radius = 5.dp.toPx(),
                    center = Offset(width, y)
                )
            }
        }

        // Labels Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // First stop label
            Text(
                text = firstStop.stopName,
                fontSize = 10.sp,
                color = textStyleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(60.dp)
            )

            // Current Bus stop label (if bus is between first and last)
            if (!tripNotStarted && currentBusIndex > 0 && currentBusIndex < stops.size - 1 && currentBusIndex != pickupIndex) {
                Text(
                    text = "🚌 " + stops[currentBusIndex].stopName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(80.dp)
                )
            }

            // Student Stop label
            if (pickupIndex != 0 && pickupIndex != stops.size - 1) {
                Text(
                    text = "🏠 " + (studentStop?.stopName ?: ""),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentBusIndex == pickupIndex) Color(0xFFEF4444) else Color(0xFF3B82F6),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(80.dp)
                )
            }

            // Last stop label
            Text(
                text = lastStop.stopName,
                fontSize = 10.sp,
                color = textStyleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(60.dp)
            )
        }
    }
}

// Haversine formula to calculate distance in meters
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
