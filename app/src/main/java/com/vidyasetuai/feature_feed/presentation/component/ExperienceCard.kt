package com.vidyasetuai.feature_feed.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ThumbsUp
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.ui.components.AuthorHeader
import com.vidyasetuai.feature_feed.domain.model.Experience

@Composable
fun ExperienceCard(
    experience: Experience,
    onReactionClick: () -> Unit,
    currentLanguage: String,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AuthorHeader(
                authorName = experience.authorName,
                authorUsername = experience.authorUsername,
                authorProfilePicUrl = experience.authorProfilePicUrl,
                isAuthorVerified = experience.isAuthorVerified,
                authorType = "user",
                authorUserId = experience.authorUserId,
                onAuthorClick = onAuthorClick
            )

            // Cover Image
            if (!experience.coverImageUrl.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // 1:1 Aspect ratio like Instagram
                ) {
                    AsyncImage(
                        model = experience.coverImageUrl,
                        contentDescription = "Cover Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Content Title & Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = experience.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = experience.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Inspired button row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onReactionClick() }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ThumbsUp,
                            contentDescription = "Reaction",
                            tint = if (experience.isInspired) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (experience.inspiredCount > 0) "${experience.inspiredCount} Inspired" else "Inspired",
                            fontSize = 12.sp,
                            fontWeight = if (experience.isInspired) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (experience.isInspired) AppColors.EmeraldGreen else Color(0xFF8E8E93)
                        )
                    }
                }
            }
        }
    }
}
