package com.vidyasetuai.feature_institution.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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

    // Role-specific logs (Max 3 logs in total)
    val cleanRole = role.lowercase().trim()
    val remainingSlots = 3 - logsList.size

    if (remainingSlots > 0) {
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
            cleanRole.contains("teacher") -> {
                listOf(
                    LogItem(
                        title = "Class 10-A attendance submitted",
                        titleHi = "कक्षा 10-A की उपस्थिति सबमिट की गई",
                        time = "08:45 AM",
                        timeHi = "सुबह 08:45",
                        icon = Lucide.Check,
                        color = AppColors.EmeraldGreen
                    ),
                    LogItem(
                        title = "Next period: Maths in Class 8",
                        titleHi = "अगला पीरियड: कक्षा 8 में गणित",
                        time = "11:30 AM",
                        timeHi = "सुबह 11:30",
                        icon = Lucide.Clock,
                        color = Color(0xFFF59E0B)
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
                        time = "Yesterday",
                        timeHi = "कल",
                        icon = Lucide.CreditCard,
                        color = Color(0xFFEF4444)
                    )
                )
            }
            cleanRole.contains("driver") -> {
                listOf(
                    LogItem(
                        title = "Morning Route: Completed",
                        titleHi = "सुबह की पिकअप ट्रिप पूरी हो गई",
                        time = "09:00 AM",
                        timeHi = "सुबह 09:00",
                        icon = Lucide.Bus,
                        color = AppColors.EmeraldGreen
                    ),
                    LogItem(
                        title = "Afternoon Route start at 02:00 PM",
                        titleHi = "दोपहर की ट्रिप शुरू होगी 02:00 बजे",
                        time = "01:30 PM",
                        timeHi = "दोपहर 01:30",
                        icon = Lucide.Navigation,
                        color = Color(0xFF3B82F6)
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
                    ),
                    LogItem(
                        title = "Staff meeting in Seminar Hall",
                        titleHi = "सेमिनार हॉल में स्टाफ बैठक",
                        time = "03:00 PM",
                        timeHi = "दोपहर 03:00",
                        icon = Lucide.Users,
                        color = Color(0xFF3B82F6)
                    )
                )
            }
        }
        logsList.addAll(roleLogs.take(remainingSlots))
    }

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
            Text(
                text = if (isHindi) "आज की हलचल 📋" else "Today's Logs 📋",
                color = if (isDark) AppColors.PureWhite else AppColors.NearBlack,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isHindi) "सभी देखें >" else "View All >",
                color = AppColors.EmeraldGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vertical Logs timeline
        if (logsList.isEmpty()) {
            Text(
                text = if (isHindi) "आज कोई हलचल नहीं है" else "No logs for today",
                color = textColorSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                logsList.forEachIndexed { index, log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Icon with subtle colored circle background
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(log.color.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = log.icon,
                                contentDescription = null,
                                tint = log.color,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Middle Content Column
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isHindi) log.titleHi else log.title,
                                color = if (isDark) AppColors.PureWhite else AppColors.NearBlack,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isHindi) log.timeHi else log.time,
                                color = textColorSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
