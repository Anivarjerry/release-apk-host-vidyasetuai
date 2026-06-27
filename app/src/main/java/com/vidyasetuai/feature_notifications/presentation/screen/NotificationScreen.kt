package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors

data class NotificationItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val descEn: String,
    val descHi: String,
    val timeEn: String,
    val timeHi: String,
    val isRead: Boolean,
    val type: String, // "assignment", "tournament", "system", "general"
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun NotificationEvent(
    currentLanguage: String,
    currentTheme: String,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (currentTheme) {
        "dark" -> true
        "light" -> false
        else -> isSystemDark
    }

    val notifications = listOf(
        NotificationItem(
            id = "1",
            titleEn = "New Quiz Unlocked",
            titleHi = "नई प्रश्नोत्तरी अनलॉक हुई",
            descEn = "Your Physics Day 5 MCQ quiz is now available for completion.",
            descHi = "भौतिकी दिवस 5 का MCQ परीक्षण अब हल करने के लिए उपलब्ध है।",
            timeEn = "2 hours ago",
            timeHi = "2 घंटे पहले",
            isRead = false,
            type = "assignment",
            icon = Lucide.FileText
        ),
        NotificationItem(
            id = "2",
            titleEn = "Tournament Starting Soon",
            titleHi = "टूर्नामेंट जल्द शुरू होगा",
            descEn = "The Weekly Coding Arena starts in 1 hour. Get ready!",
            descHi = "साप्ताहिक कोडिंग अखाड़ा 1 घंटे में शुरू होगा। तैयार हो जाइए!",
            timeEn = "3 hours ago",
            timeHi = "3 घंटे पहले",
            isRead = false,
            type = "tournament",
            icon = Lucide.Trophy
        ),
        NotificationItem(
            id = "3",
            titleEn = "Institution Connection Approved",
            titleHi = "संस्थान कनेक्शन स्वीकृत",
            descEn = "Your connection with DPS School has been successfully verified.",
            descHi = "DPS स्कूल के साथ आपका कनेक्शन सफलतापूर्वक सत्यापित हो गया है।",
            timeEn = "Yesterday",
            timeHi = "कल",
            isRead = true,
            type = "system",
            icon = Lucide.Check
        ),
        NotificationItem(
            id = "4",
            titleEn = "New Announcement",
            titleHi = "नई घोषणा",
            descEn = "School board published the final exams time-sheet for 2026.",
            descHi = "स्कूल बोर्ड ने 2026 की वार्षिक परीक्षा समय-सारणी जारी की।",
            timeEn = "2 days ago",
            timeHi = "2 दिन पहले",
            isRead = true,
            type = "general",
            icon = Lucide.Bell
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Clear all option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHindi) "हालिया अपडेट" else "Recent Updates",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (isHindi) "सभी को पढ़ा हुआ चिह्नित करें" else "Mark all as read",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.EmeraldGreen,
                modifier = Modifier.clickable { /* Mark all as read */ }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(notifications) { item ->
                NotificationRow(notification = item, isHindi = isHindi, isDark = isDark)
            }
        }
    }
}

@Composable
fun NotificationRow(
    notification: NotificationItem,
    isHindi: Boolean,
    isDark: Boolean
) {
    val unreadBg = if (isDark) AppColors.EmeraldGreen.copy(alpha = 0.05f) else AppColors.EmeraldGreen.copy(alpha = 0.02f)
    val readBg = Color.Transparent
    val borderBottomColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (notification.isRead) readBg else unreadBg)
            .clickable { /* Handle click */ }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // Icon category indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = when (notification.type) {
                            "assignment" -> Color(0xFF0D6EFD).copy(alpha = 0.12f)
                            "tournament" -> Color(0xFFFFC107).copy(alpha = 0.12f)
                            "system" -> AppColors.EmeraldGreen.copy(alpha = 0.12f)
                            else -> Color(0xFF6C757D).copy(alpha = 0.12f)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = when (notification.type) {
                        "assignment" -> Color(0xFF0D6EFD)
                        "tournament" -> Color(0xFFE0A800)
                        "system" -> AppColors.EmeraldGreen
                        else -> Color(0xFF6C757D)
                    },
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Body text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) notification.titleHi else notification.titleEn,
                        fontSize = 14.sp,
                        fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    // Unread blue/green dot indicator
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(AppColors.EmeraldGreen, shape = CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isHindi) notification.descHi else notification.descEn,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isHindi) notification.timeHi else notification.timeEn,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(borderBottomColor)
        )
    }
}
