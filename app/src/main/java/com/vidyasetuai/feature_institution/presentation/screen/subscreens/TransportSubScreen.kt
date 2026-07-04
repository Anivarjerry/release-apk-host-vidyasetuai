package com.vidyasetuai.feature_feed.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onNavigateToLiveBus: (String) -> Unit,
    onBack: () -> Unit
) {
    // Hardware back press handler
    BackHandler(onBack = onBack)

    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardColor = if (isDark) Color(0xFF1E293B) else Color.White
    val titleColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val borderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    val role = state.activeWorkspace?.role ?: "Student"
    val isAdmin = role in listOf("Admin", "System Administrator", "School Administrator", "Org Admin", "Principal", "Director", "Owner")

    // Determine list of buses
    val busList = if (isAdmin) {
        state.allBuses
    } else {
        // Map student bus assignments to a unified representation
        state.studentBuses.map { assignment ->
            // Try to find full bus entity to get driver details
            val matchedBus = state.allBuses.find { it.id == assignment.busId }
            BusItemData(
                id = assignment.busId,
                busNumber = assignment.busNumber,
                busName = assignment.busName ?: "",
                routeName = assignment.routeName ?: "",
                driverName = matchedBus?.driverName ?: (if (isHindi) "चालक" else "Driver"),
                driverMobile = matchedBus?.driverMobile ?: "",
                maxCapacity = matchedBus?.maxCapacity ?: 40
            )
        }.distinctBy { it.id }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(cardColor)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = titleColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "परिवहन" else "Transport",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(borderColor)
                )
            }
        },
        containerColor = bgColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
        ) {
            if (busList.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Bus,
                            contentDescription = "No Buses",
                            tint = subtitleColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "कोई वाहन असाइन नहीं है" else "No Vehicles Assigned",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi) 
                            "वर्तमान में आपके रूट के लिए कोई स्कूल बस असाइन नहीं की गई है।" 
                            else "Currently, there are no school buses assigned to your active route.",
                        fontSize = 14.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // List of Buses
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isAdmin) {
                        items(state.allBuses) { bus ->
                            AdminBusCard(
                                bus = bus,
                                cardColor = cardColor,
                                titleColor = titleColor,
                                subtitleColor = subtitleColor,
                                isHindi = isHindi,
                                isDark = isDark,
                                onTrackClick = { onNavigateToLiveBus(bus.id) }
                            )
                        }
                    } else {
                        items(busList as List<BusItemData>) { bus ->
                            StudentBusCard(
                                bus = bus,
                                cardColor = cardColor,
                                titleColor = titleColor,
                                subtitleColor = subtitleColor,
                                isHindi = isHindi,
                                isDark = isDark,
                                onTrackClick = { onNavigateToLiveBus(bus.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Unified item representation for students
data class BusItemData(
    val id: String,
    val busNumber: String,
    val busName: String,
    val routeName: String,
    val driverName: String,
    val driverMobile: String,
    val maxCapacity: Int
)

@Composable
fun StudentBusCard(
    bus: BusItemData,
    cardColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    isHindi: Boolean,
    isDark: Boolean,
    onTrackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTrackClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = bus.busNumber,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    Text(
                        text = bus.routeName,
                        fontSize = 13.sp,
                        color = subtitleColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isHindi) "ट्रैक करें" else "Track Live",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
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
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${if (isHindi) "चालक" else "Driver"}: ${bus.driverName}",
                        fontSize = 13.sp,
                        color = titleColor
                    )
                }

                if (bus.driverMobile.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Lucide.Phone,
                            contentDescription = "Phone",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = bus.driverMobile,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBusCard(
    bus: com.vidyasetuai.feature_institution.data.local.entity.LocalParentBusEntity,
    cardColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    isHindi: Boolean,
    isDark: Boolean,
    onTrackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTrackClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = bus.busNumber,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    Text(
                        text = bus.routeName ?: (if (isHindi) "अज्ञात रूट" else "Unknown Route"),
                        fontSize = 13.sp,
                        color = subtitleColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isHindi) "लाइव मैप" else "Live Map",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
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
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${if (isHindi) "चालक" else "Driver"}: ${bus.driverName ?: (if (isHindi) "आवंटित नहीं" else "Not Assigned")}",
                        fontSize = 13.sp,
                        color = titleColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Users,
                        contentDescription = "Capacity",
                        tint = subtitleColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${if (isHindi) "क्षमता" else "Capacity"}: ${bus.maxCapacity ?: 40}",
                        fontSize = 13.sp,
                        color = subtitleColor
                    )
                }
            }
        }
    }
}
