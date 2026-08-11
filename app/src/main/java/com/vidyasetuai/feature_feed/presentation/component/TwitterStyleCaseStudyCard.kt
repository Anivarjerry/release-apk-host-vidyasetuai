package com.vidyasetuai.feature_feed.presentation.component

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy

private fun formatRelativeTime(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return "now"
    return try {
        if (dateStr.contains("T")) {
            val datePart = dateStr.substringBefore("T")
            val parts = datePart.split("-")
            if (parts.size == 3) {
                "${parts[1]}/${parts[2]}"
            } else datePart
        } else {
            dateStr
        }
    } catch (e: Exception) {
        "now"
    }
}

/**
 * Case Study Feed Card Composable
 * Layout Order:
 * 1. Author Header
 * 2. Cover Image (Natural Ratio)
 * 3. Title (Below Image)
 * 4. Short Description + Dotted "...See More" (Below Title)
 * 5. Action Row: Inspired Button (Left) + "Read Full Case Study →" Button (Right)
 * 6. Distinct Separator Divider between posts
 */
@Composable
fun TwitterStyleCaseStudyFeedCard(
    caseStudy: CaseStudy,
    cardBgColor: Color,
    textColor: Color,
    subtitleColor: Color,
    dividerColor: Color,
    isHindi: Boolean,
    onExploreClick: () -> Unit,
    onReactClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    hasActiveQuicks: Boolean = false,
    onQuickClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBgColor)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // 1. Author Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author Avatar (Circular with Emerald Gradient Story Ring if quicks present)
                Box(
                    modifier = if (hasActiveQuicks) {
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AppColors.EmeraldGreen, Color(0xFF34D399))))
                            .padding(2.dp)
                            .clickable { onQuickClick() }
                    } else {
                        Modifier.clickable { onAuthorClick(caseStudy.authorUserId ?: "") }
                    },
                    contentAlignment = Alignment.Center
                ) {
                    if (!caseStudy.authorProfilePicUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = caseStudy.authorProfilePicUrl,
                            contentDescription = caseStudy.authorName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(if (hasActiveQuicks) 40.dp else 40.dp)
                                .clip(CircleShape)
                                .border(1.dp, if (hasActiveQuicks) Color.White else dividerColor.copy(alpha = 0.3f), CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(if (hasActiveQuicks) 40.dp else 40.dp)
                                .clip(CircleShape)
                                .background(AppColors.EmeraldGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = caseStudy.authorName.take(1).uppercase(),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.EmeraldGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Author Name + Handle + Badge + Time
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = caseStudy.authorName.ifBlank { "VidyaSetu AI" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (caseStudy.isAuthorVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Verified",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Color(0xFF0284C7).copy(alpha = 0.15f), CircleShape)
                                    .padding(2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "· ${formatRelativeTime(caseStudy.createdAt)}",
                            fontSize = 12.sp,
                            color = subtitleColor
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (!caseStudy.authorUsername.isNullOrBlank()) "@${caseStudy.authorUsername}" else "@vidyasetu",
                            fontSize = 12.sp,
                            color = subtitleColor
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Type Badge: "Case Study"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AppColors.EmeraldGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isHindi) "केस स्टडी" else "Case Study",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.EmeraldGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Cover Image (Natural Aspect Ratio - Positioned MOVED UP directly below Author Header)
            if (!caseStudy.coverImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = caseStudy.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onExploreClick() }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 3. Full Title (Positioned BELOW Image)
            Text(
                text = caseStudy.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                lineHeight = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExploreClick() }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 4. Caption / Description with Dotted Ellipsis and "See More / और देखें" (Positioned BELOW Title)
            val hasLongDescription = caseStudy.shortDescription.length > 100
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = caseStudy.shortDescription,
                    fontSize = 13.sp,
                    color = subtitleColor,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                if (hasLongDescription) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isExpanded) (if (isHindi) "कम दिखाएं" else "Show less") else (if (isHindi) "...और देखें" else "...See more"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen,
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }
                            .padding(vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Action Row: Inspired Button (Left) + "Read Full Case Study →" Button (Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Inspired Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (caseStudy.isReacted) AppColors.EmeraldGreen.copy(alpha = 0.15f) else dividerColor.copy(alpha = 0.08f))
                        .clickable { onReactClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.Lightbulb,
                        contentDescription = "Inspired",
                        tint = if (caseStudy.isReacted) AppColors.EmeraldGreen else subtitleColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (caseStudy.reactionCount > 0) "${caseStudy.reactionCount} " + (if (isHindi) "प्रेरित" else "Inspired") else (if (isHindi) "प्रेरित हों" else "Inspire"),
                        fontSize = 12.sp,
                        fontWeight = if (caseStudy.isReacted) FontWeight.Bold else FontWeight.Medium,
                        color = if (caseStudy.isReacted) AppColors.EmeraldGreen else subtitleColor
                    )
                }

                // Read Full Case Study Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.12f))
                        .clickable { onExploreClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "पूरा केस स्टडी पढ़ें →" else "Read Full Case Study →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                }
            }
        }

        // 6. Distinct Post Separator Divider Bar between consecutive posts
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(dividerColor.copy(alpha = 0.15f))
        )
    }
}
