package com.vidyasetuai.feature_institution.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.component.LogItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllLogsScreen(
    role: String,
    isHindi: Boolean,
    isDark: Boolean,
    onBackClick: () -> Unit,
    todayEventTitle: String? = null,
    isSchoolClosed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardBackground = if (isDark) AppColors.CharcoalGray else AppColors.PureWhite
    val borderStrokeColor = if (isDark) Color(0xFF262626) else Color(0xFFE5E5E5)
    val textColorSecondary = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)

    // Generate comprehensive logs for the View All screen
    val allLogs = mutableListOf<LogItem>()

    // Priority 1: Events
    if (isSchoolClosed) {
        allLogs.add(
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
        allLogs.add(
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
                ),
                LogItem(
                    title = "Staff Leave Requests: 2 pending approvals",
                    titleHi = "कर्मचारी छुट्टी अनुरोध: 2 लंबित स्वीकृतियां",
                    time = "10:15 AM",
                    timeHi = "सुबह 10:15",
                    icon = Lucide.FileText,
                    color = Color(0xFFF59E0B)
                ),
                LogItem(
                    title = "New student enrollment: Amit Sharma (Class 6)",
                    titleHi = "नया छात्र प्रवेश: अमित शर्मा (कक्षा 6)",
                    time = "Yesterday",
                    timeHi = "कल",
                    icon = Lucide.UserPlus,
                    color = Color(0xFF10B981)
                ),
                LogItem(
                    title = "Electricity bill paid: ₹8,200",
                    titleHi = "बिजली बिल भुगतान किया गया: ₹8,200",
                    time = "Yesterday",
                    timeHi = "कल",
                    icon = Lucide.CreditCard,
                    color = Color(0xFF7C3AED)
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
                ),
                LogItem(
                    title = "Homework uploaded for Class 10-A Algebra",
                    titleHi = "कक्षा 10-A बीजगणित के लिए गृहकार्य अपलोड किया गया",
                    time = "09:30 AM",
                    timeHi = "सुबह 09:30",
                    icon = Lucide.BookOpen,
                    color = Color(0xFF3B82F6)
                ),
                LogItem(
                    title = "Exam marks submitted: Unit Test 1",
                    titleHi = "परीक्षा अंक सबमिट किए गए: यूनिट टेस्ट 1",
                    time = "Yesterday",
                    timeHi = "कल",
                    icon = Lucide.FileText,
                    color = Color(0xFF10B981)
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
                ),
                LogItem(
                    title = "Rahul scored 85% in Mathematics Test 1",
                    titleHi = "राहुल ने गणित Test 1 में 85% अंक प्राप्त किए",
                    time = "2 days ago",
                    timeHi = "2 दिन पहले",
                    icon = Lucide.Award,
                    color = Color(0xFFF59E0B)
                ),
                LogItem(
                    title = "School Bus Route 4: Morning drop completed",
                    titleHi = "स्कूल बस रूट 4: सुबह का ड्रॉप पूरा हुआ",
                    time = "Yesterday",
                    timeHi = "कल",
                    icon = Lucide.Bus,
                    color = Color(0xFF3B82F6)
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
                ),
                LogItem(
                    title = "Vehicle Fuel Refill: ₹3,500",
                    titleHi = "वाहन ईंधन रिफिल: ₹3,500",
                    time = "Yesterday",
                    timeHi = "कल",
                    icon = Lucide.Coins,
                    color = Color(0xFF10B981)
                ),
                LogItem(
                    title = "Tyre pressure check done",
                    titleHi = "टायर प्रेशर की जांच पूरी हो गई",
                    time = "2 days ago",
                    timeHi = "2 दिन पहले",
                    icon = Lucide.Activity,
                    color = Color(0xFF7C3AED)
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
                ),
                LogItem(
                    title = "Daily task list checked",
                    titleHi = "दैनिक कार्य सूची की जांच की गई",
                    time = "09:00 AM",
                    timeHi = "सुबह 09:00",
                    icon = Lucide.FileText,
                    color = Color(0xFFF59E0B)
                ),
                LogItem(
                    title = "System maintenance completed",
                    titleHi = "सिस्टम रखरखाव पूरा हुआ",
                    time = "Yesterday",
                    timeHi = "कल",
                    icon = Lucide.Settings,
                    color = Color(0xFF7C3AED)
                )
            )
        }
    }
    allLogs.addAll(roleLogs)

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "आज की हलचल 📋" else "Today's Logs 📋",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(allLogs) { index, log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBackground, shape = RoundedCornerShape(12.dp))
                        .border(width = 0.5.dp, color = borderStrokeColor, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(log.color.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = log.icon,
                            contentDescription = null,
                            tint = log.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isHindi) log.titleHi else log.title,
                            color = if (isDark) AppColors.PureWhite else AppColors.NearBlack,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isHindi) log.timeHi else log.time,
                            color = textColorSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
