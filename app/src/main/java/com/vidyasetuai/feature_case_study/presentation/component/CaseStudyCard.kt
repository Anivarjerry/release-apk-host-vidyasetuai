package com.vidyasetuai.feature_case_study.presentation.component

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ThumbsUp
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.ui.components.AuthorHeader
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseStudyCard(
    caseStudy: CaseStudy,
    onClick: () -> Unit,
    onReactionClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    currentLanguage: String,
    onExploreClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"

    Card(
        onClick = onClick,
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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AuthorHeader(
                authorName = caseStudy.authorName,
                authorUsername = caseStudy.authorUsername,
                authorProfilePicUrl = caseStudy.authorProfilePicUrl,
                isAuthorVerified = caseStudy.isAuthorVerified,
                authorType = caseStudy.authorType,
                authorUserId = caseStudy.authorUserId,
                onAuthorClick = onAuthorClick
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // 1:1 Ratio for cover image like Instagram
                    .clip(RoundedCornerShape(0.dp))
            ) {
                AsyncImage(
                    model = caseStudy.coverImageUrl,
                    contentDescription = "Cover Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = caseStudy.language.replaceFirstChar { it.uppercase() },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .background(AppColors.EmeraldGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    if (caseStudy.readTimeMinutes != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Clock,
                                contentDescription = "Read Time",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${caseStudy.readTimeMinutes} min",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = caseStudy.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 24.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = caseStudy.shortDescription,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
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
                            tint = if (caseStudy.isReacted) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (caseStudy.reactionCount > 0) "${caseStudy.reactionCount} Inspired" else "Inspired",
                            fontSize = 12.sp,
                            fontWeight = if (caseStudy.isReacted) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (caseStudy.isReacted) AppColors.EmeraldGreen else Color(0xFF8E8E93)
                        )
                    }

                    // Explore Text Button
                    Text(
                        text = if (isHindi) "एक्सप्लोर करें" else "Explore",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen,
                        modifier = Modifier
                            .clickable { onExploreClick() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )

                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Bookmark,
                            contentDescription = "Bookmark",
                            tint = if (caseStudy.isBookmarked) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
