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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardWelcomeCard(
    userName: String,
    roleDisplayName: String,
    profilePictureUrl: String?,
    profilePictureLocalPath: String?,
    todayEventTitle: String? = null,
    todayEventType: String? = null,
    isSchoolClosed: Boolean = false,
    isHindi: Boolean = false,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    
    // 1. IST Date & Greeting calculation
    val zoneId = ZoneId.of("Asia/Kolkata")
    val currentIst = ZonedDateTime.now(zoneId)
    
    val locale = if (isHindi) Locale("hi", "IN") else Locale.getDefault()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", locale)
    val formattedDate = currentIst.format(dateFormatter)
    
    val dayOfWeekStr = currentIst.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, locale)
    val dayOfMonthStr = currentIst.dayOfMonth.toString()
    val monthStr = currentIst.month.getDisplayName(java.time.format.TextStyle.SHORT, locale)

    val hour = currentIst.hour
    val greeting = when {
        hour in 5..11 -> if (isHindi) "शुभ प्रभात" else "Good Morning"
        hour in 12..16 -> if (isHindi) "शुभ दोपहर" else "Good Afternoon"
        hour in 17..21 -> if (isHindi) "शुभ संध्या" else "Good Evening"
        else -> if (isHindi) "नमस्ते" else "Hello"
    }

    // 2. Event styling based on type (Strict Palette: Green/Red/Gray)
    val eventIndicatorColor = if (todayEventType?.uppercase() == "HOLIDAY" || isSchoolClosed) {
        Color(0xFFEF4444) // Red for Alert/Holiday
    } else {
        AppColors.EmeraldGreen // Emerald Green for generic/PTM/Academic events
    }

    val cardBackground = if (isDark) AppColors.CharcoalGray else AppColors.PureWhite
    val borderStrokeColor = if (isDark) Color(0xFF262626) else Color(0xFFE5E5E5)
    val textColorSecondary = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)

    // 3. UI Card Container (Minimalist Flat Layout, No Shadows, Clean Spacing)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(cardBackground, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = borderStrokeColor, shape = RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Welcome Text
                Text(
                    text = "$greeting,",
                    color = textColorSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = userName,
                    color = if (isDark) AppColors.PureWhite else AppColors.NearBlack,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Role Text (Simple flat pill with Emerald Green text)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.08f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = roleDisplayName,
                        color = AppColors.EmeraldGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Premium Physical Calendar Date Badge (Right Side)
            Column(
                modifier = Modifier
                    .width(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp, 
                        color = borderStrokeColor, 
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(if (isDark) Color(0xFF1E1E1E) else Color(0xFFF9F9F9)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header of Calendar Badge (Month banner)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.EmeraldGreen)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = monthStr.uppercase(locale),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                
                // Day Number
                Text(
                    text = dayOfMonthStr,
                    color = if (isDark) AppColors.PureWhite else AppColors.NearBlack,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                // Day of Week
                Text(
                    text = dayOfWeekStr,
                    color = textColorSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }

        // Today's event section (only shows if event exists)
        if (!todayEventTitle.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Flat minimal border line divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(borderStrokeColor)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vertical accent bar (Clean Red or Green indicators)
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(eventIndicatorColor)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isHindi) "आज का एजेंडा" else "TODAY'S EVENT",
                        color = textColorSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = todayEventTitle + if (isSchoolClosed) {
                            if (isHindi) " (स्कूल बंद है 🛑)" else " (School Closed 🛑)"
                        } else "",
                        color = if (isDark) AppColors.PureWhite else AppColors.NearBlack,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Icon(
                    imageVector = if (todayEventType?.uppercase() == "HOLIDAY" || isSchoolClosed) Lucide.CalendarOff else Lucide.Calendar,
                    contentDescription = "Agenda Icon",
                    tint = eventIndicatorColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
