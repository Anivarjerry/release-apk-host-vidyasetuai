package com.vidyasetuai.feature_institution.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.state.TodayLogItem

data class LogItem(
    val title: String,
    val titleHi: String,
    val time: String,
    val timeHi: String,
    val icon: ImageVector,
    val color: Color
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardTodayLogsCard(
    role: String,
    isHindi: Boolean,
    isDark: Boolean,
    onViewAllClick: () -> Unit,
    todayEventTitle: String? = null,
    isSchoolClosed: Boolean = false,
    todayLogs: List<TodayLogItem> = emptyList(),
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isDark) AppColors.CharcoalGray else AppColors.PureWhite
    val borderStrokeColor = if (isDark) Color(0xFF262626) else Color(0xFFE5E5E5)
    val textColorSecondary = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)

    // Build the logs list dynamically
    val logsList = mutableListOf<LogItem>()

    // Priority 1: Today's Event
    if (isSchoolClosed) {
        logsList.add(
            LogItem(
                title = "School Closed (Summer Holiday)",
                titleHi = "स्कूल बंद है (ग्रीष्मकालीन अवकाश) 🛑",
                time = "Today",
                timeHi = "आज",
                icon = Lucide.CalendarOff,
                color = Color(0xFFEF4444)
            )
        )
    } else if (!todayEventTitle.isNullOrEmpty()) {
        logsList.add(
            LogItem(
                title = todayEventTitle,
                titleHi = todayEventTitle,
                time = "Today",
                timeHi = "आज",
                icon = Lucide.Calendar,
                color = AppColors.EmeraldGreen
            )
        )
    }

    // Role-specific logs from dynamic todayLogs state
    if (todayLogs.isNotEmpty()) {
        todayLogs.forEach { log ->
            val iconVec = when (log.iconType) {
                "user" -> Lucide.UserCheck
                "calendar" -> Lucide.Calendar
                "card" -> Lucide.CreditCard
                "bus" -> Lucide.Bus
                else -> Lucide.Info
            }
            val colorVal = when (log.type) {
                "ATTENDANCE" -> AppColors.EmeraldGreen
                "LEAVE" -> Color(0xFFF59E0B)
                "FEE" -> Color(0xFF3B82F6)
                "BUS" -> Color(0xFF8B5CF6)
                else -> AppColors.EmeraldGreen
            }
            logsList.add(
                LogItem(
                    title = log.title,
                    titleHi = log.titleHi ?: log.title,
                    time = log.subtitle,
                    timeHi = log.subtitle,
                    icon = iconVec,
                    color = colorVal
                )
            )
        }
    } else {
        // Fallback role logs
        val cleanRole = role.lowercase().trim()
        val roleLogs = when {
            cleanRole.contains("admin") || cleanRole.contains("principal") || cleanRole.contains("director") || cleanRole.contains("owner") -> {
                listOf(
                    LogItem(
                        title = "92% students present today",
                        titleHi = "आज की छात्र उपस्थिति: 92%",
                        time = "09:00 AM",
                        timeHi = "सुबह 09:00",
                        icon = Lucide.Users,
                        color = AppColors.EmeraldGreen
                    ),
                    LogItem(
                        title = "Term 2 fees collected: ₹48,500",
                        titleHi = "आज कुल फीस कलेक्शन: ₹48,500",
                        time = "11:30 AM",
                        timeHi = "सुबह 11:30",
                        icon = Lucide.Coins,
                        color = Color(0xFF3B82F6)
                    )
                )
            }
            cleanRole.contains("guardian") -> {
                listOf(
                    LogItem(
                        title = "Rahul present at school",
                        titleHi = "राहुल आज स्कूल पहुंच गया है",
                        time = "08:45 AM",
                        timeHi = "सुबह 08:45",
                        icon = Lucide.UserCheck,
                        color = AppColors.EmeraldGreen
                    ),
                    LogItem(
                        title = "Rahul's Term 2 fee due: ₹5,000",
                        titleHi = "राहुल की टर्म-2 फीस बकाया: ₹5,000",
                        time = "Due Soon",
                        timeHi = "शीघ्र देय",
                        icon = Lucide.CreditCard,
                        color = Color(0xFFEF4444)
                    )
                )
            }
            else -> {
                listOf(
                    LogItem(
                        title = "Work attendance registered",
                        titleHi = "आज की उपस्थिति दर्ज की गई",
                        time = "08:30 AM",
                        timeHi = "सुबह 08:30",
                        icon = Lucide.Check,
                        color = AppColors.EmeraldGreen
                    )
                )
            }
        }
        logsList.addAll(roleLogs)
    }

    val displayLogs = logsList.take(3)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(cardBackground, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = borderStrokeColor, shape = RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        // Header Row: Title & View All
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isHindi) "आज की हलचल 📋" else "Today's Logs 📋",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) AppColors.PureWhite else Color(0xFF171717)
                )
            }

            // View All button hidden for future redesign
            /*
            Text(
                text = if (isHindi) "सभी देखें >" else "View All >",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.EmeraldGreen,
                modifier = Modifier.clickable { onViewAllClick() }
            )
            */
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logs List Items
        displayLogs.forEachIndexed { index, log ->
            LogItemRow(
                log = log,
                isHindi = isHindi,
                isDark = isDark,
                textColorSecondary = textColorSecondary
            )

            if (index < displayLogs.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun LogItemRow(
    log: LogItem,
    isHindi: Boolean,
    isDark: Boolean,
    textColorSecondary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(log.color.copy(alpha = if (isDark) 0.15f else 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = log.icon,
                contentDescription = null,
                tint = log.color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Text Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isHindi) log.titleHi else log.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) AppColors.PureWhite else Color(0xFF262626),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (isHindi) log.timeHi else log.time,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = textColorSecondary
            )
        }
    }
}
