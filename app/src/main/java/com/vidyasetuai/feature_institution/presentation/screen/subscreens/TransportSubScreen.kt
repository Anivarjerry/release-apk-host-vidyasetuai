package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@Composable
fun TransportSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onNavigateToLiveBus: (String) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    val busLiveStates = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(state.studentBuses) {
        state.studentBuses.forEach { assignment ->
            val busId = assignment.busId
            viewModel.repository.getBusLiveLocation(busId).onSuccess { liveLocation ->
                busLiveStates[busId] = liveLocation?.isLive ?: false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.studentBuses.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                    color = cardBg
                ) {
                    Text(
                        text = if (isHindi) "बच्चों के लिए कोई बस आवंटित नहीं है" else "No buses assigned to your children.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                state.studentBuses.forEach { assignment ->
                    val isLive = busLiveStates[assignment.busId] ?: false
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                        color = cardBg
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header: Student Name and Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = assignment.studentName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = if (isHindi) "बस असाइनमेंट" else "Bus Assignment",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Status Badge
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isLive) AppColors.EmeraldGreen.copy(alpha = 0.12f) else Color(0xFF8E8E93).copy(alpha = 0.12f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(if (isLive) AppColors.EmeraldGreen else Color(0xFF8E8E93), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isLive) {
                                                if (isHindi) "लाइव" else "Live"
                                            } else {
                                                if (isHindi) "ऑफलाइन" else "Offline"
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLive) AppColors.EmeraldGreen else Color(0xFF8E8E93)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(MaterialTheme.colorScheme.outlineVariant))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Details
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                BusDetailRow(
                                    icon = Lucide.Bus,
                                    label = if (isHindi) "वाहन नंबर / नाम" else "Vehicle No / Name",
                                    value = "${assignment.busNumber} ${assignment.busName?.let { "• $it" } ?: ""}"
                                )
                                BusDetailRow(
                                    icon = Lucide.Milestone,
                                    label = if (isHindi) "रूट का नाम" else "Route Name",
                                    value = assignment.routeName ?: (if (isHindi) "कोई रूट उपलब्ध नहीं" else "No Route Assigned")
                                )
                                BusDetailRow(
                                    icon = Lucide.MapPin,
                                    label = if (isHindi) "स्टॉप / बोर्डिंग पॉइंट" else "Pickup Stop",
                                    value = assignment.pickupStop ?: (if (isHindi) "निर्धारित नहीं" else "Not Specified")
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Button
                            Button(
                                onClick = { onNavigateToLiveBus(assignment.busId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLive) AppColors.EmeraldGreen else MaterialTheme.colorScheme.outline,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(21.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = Lucide.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isLive) {
                                            if (isHindi) "लाइव लोकेशन देखें" else "Check Live Location"
                                        } else {
                                            if (isHindi) "अंतिम स्थान देखें (ऑफलाइन)" else "View Last Known Location (Offline)"
                                        },
                                        fontSize = 13.sp,
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
}

@Composable
fun BusDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
