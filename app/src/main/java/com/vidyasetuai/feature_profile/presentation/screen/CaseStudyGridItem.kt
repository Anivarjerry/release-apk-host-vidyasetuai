package com.vidyasetuai.feature_profile.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.ThumbsUp
import com.composables.icons.lucide.Lucide
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy

@Composable
fun CaseStudyGridItem(
    caseStudy: CaseStudy,
    onClick: () -> Unit,
    onReactionClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    val cardBgGradient = Brush.verticalGradient(
        colors = listOf(
            AppColors.EmeraldGreen.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.surfaceVariant
        )
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = AppColors.EmeraldGreen.copy(alpha = 0.25f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Cover Image or Gradient Background
            if (!caseStudy.coverImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = caseStudy.coverImageUrl,
                    contentDescription = caseStudy.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(cardBgGradient)
                )
            }

            // 2. Language Tag (Top Left)
            val langText = when (caseStudy.language.lowercase()) {
                "hi" -> if (isHindi) "हिंदी" else "Hindi"
                "en" -> if (isHindi) "अंग्रेजी" else "English"
                else -> caseStudy.language.replaceFirstChar { it.uppercase() }
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = langText,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 3. Read Time Tag (Top Right)
            caseStudy.readTimeMinutes?.let { minutes ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.9f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isHindi) "$minutes मिनट" else "${minutes}m read",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 4. Dark Gradient Overlay and Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = caseStudy.title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 15.sp
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reactions/Inspirations
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onReactionClick() }
                                .padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.ThumbsUp,
                                contentDescription = "Reaction",
                                tint = if (caseStudy.isReacted) AppColors.EmeraldGreen else Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = caseStudy.reactionCount.toString(),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Bookmark
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onBookmarkClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Bookmark,
                                contentDescription = "Bookmark",
                                tint = if (caseStudy.isBookmarked) AppColors.EmeraldGreen else Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
