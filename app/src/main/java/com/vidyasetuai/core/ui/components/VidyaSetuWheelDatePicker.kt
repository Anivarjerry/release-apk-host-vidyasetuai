package com.vidyasetuai.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Flagship Universal App-Level Apple 3D Drum Wheel Date Picker Dialog:
 * - Reusable across all modules (Profile, Store POS, Institution, Campus, Reports, Attendance)
 * - Adaptive Light & Dark Theme Parity (Pure White Card / Obsidian Charcoal Glass)
 * - 3 Cylindrical Rolling Drum Columns (Month | Day | Year) with Snap Fling Physics
 * - Dynamic Leap-Year & Month-End Day Count Validation
 * - Symmetrical CANCEL & OK Action Triggers
 */
@Composable
fun VidyaSetuWheelDatePicker(
    initialDateString: String = "",
    minYear: Int = 1920,
    maxYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    title: String? = null,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    // Parse initial date or default to 2000-01-01
    val parsedCal = remember(initialDateString) {
        val cal = Calendar.getInstance()
        if (initialDateString.isNotBlank()) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val d = sdf.parse(initialDateString)
                if (d != null) cal.time = d
            } catch (_: Exception) {}
        } else {
            cal.set(2000, Calendar.JANUARY, 1)
        }
        cal
    }

    val months = remember {
        listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
    }

    val safeMinYear = minYear.coerceAtLeast(1900)
    val safeMaxYear = maxYear.coerceAtLeast(safeMinYear)
    val years = remember(safeMinYear, safeMaxYear) { (safeMinYear..safeMaxYear).map { it.toString() } }

    var selectedMonthIndex by remember { mutableStateOf(parsedCal.get(Calendar.MONTH).coerceIn(0, 11)) }
    var selectedYear by remember { mutableStateOf(parsedCal.get(Calendar.YEAR).coerceIn(safeMinYear, safeMaxYear)) }

    // Dynamic days computation based on selected month & year (Leap-year aware)
    val daysInSelectedMonth = remember(selectedMonthIndex, selectedYear) {
        val temp = Calendar.getInstance()
        temp.set(Calendar.YEAR, selectedYear)
        temp.set(Calendar.MONTH, selectedMonthIndex)
        temp.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    val days = remember(daysInSelectedMonth) { (1..daysInSelectedMonth).map { it.toString() } }
    var selectedDayIndex by remember(daysInSelectedMonth) {
        mutableStateOf((parsedCal.get(Calendar.DAY_OF_MONTH) - 1).coerceIn(0, daysInSelectedMonth - 1))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = if (isDark) Color(0xFF0F172A) else Color.White,
            border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                if (!title.isNullOrBlank()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // 3D Drum Wheel Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Center Selector Highlight Lens
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Month Column
                        WheelColumn(
                            items = months,
                            initialIndex = selectedMonthIndex,
                            onSelectedIndexChanged = { selectedMonthIndex = it },
                            modifier = Modifier.weight(1.4f),
                            isDark = isDark
                        )

                        // 2. Day Column
                        WheelColumn(
                            items = days,
                            initialIndex = selectedDayIndex,
                            onSelectedIndexChanged = { selectedDayIndex = it },
                            modifier = Modifier.weight(0.9f),
                            isDark = isDark
                        )

                        // 3. Year Column
                        val initialYearIndex = remember { (selectedYear - safeMinYear).coerceIn(0, years.size - 1) }
                        WheelColumn(
                            items = years,
                            initialIndex = initialYearIndex,
                            onSelectedIndexChanged = { idx ->
                                selectedYear = safeMinYear + idx
                            },
                            modifier = Modifier.weight(1.1f),
                            isDark = isDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Row (CANCEL / OK)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "CANCEL",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 13.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            val finalDay = (selectedDayIndex + 1).coerceIn(1, daysInSelectedMonth)
                            val finalMonth = (selectedMonthIndex + 1).coerceIn(1, 12)
                            val formatted = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, finalMonth, finalDay)
                            onDateSelected(formatted)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF10B981) else Color(0xFF0F172A),
                                fontSize = 13.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3D Rolling Drum Cylinder Column with Snap Physics and Optical Depth.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelColumn(
    items: List<String>,
    initialIndex: Int,
    onSelectedIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)))
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Notify selected index changes
    val currentIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
        }
    }

    LaunchedEffect(currentIndex) {
        onSelectedIndexChanged(currentIndex)
    }

    LazyColumn(
        state = listState,
        flingBehavior = snapFlingBehavior,
        modifier = modifier.height(180.dp),
        contentPadding = PaddingValues(vertical = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(items) { index, item ->
            val isSelected = index == currentIndex
            val textColor = if (isSelected) {
                if (isDark) Color.White else Color(0xFF0F172A)
            } else {
                if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
            }

            val scale = if (isSelected) 1.05f else 0.88f
            val alpha = if (isSelected) 1.0f else 0.45f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        fontSize = if (isSelected) 15.sp else 13.5.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
