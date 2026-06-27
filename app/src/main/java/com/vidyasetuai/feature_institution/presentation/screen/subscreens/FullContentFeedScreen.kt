package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.domain.model.ContentFeedItem

@Composable
fun FullContentFeedScreen(
    item: ContentFeedItem?,
    isHindi: Boolean,
    isDark: Boolean,
    onBack: () -> Unit
) {
    if (item == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isHindi) "डेटा लोड करने में विफल" else "Failed to load content details.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val scrollState = rememberScrollState()
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, borderVal),
                color = cardBg
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.publisherType == "parent") Lucide.Building2 else Lucide.Building,
                                contentDescription = null,
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = item.publisherName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            val relativeDate = item.createdAt.substringBefore("T").substringBefore(" ")
                            Text(
                                text = relativeDate,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Notice / Gallery badge pill
                    val isNotice = item.contentType == "notice"
                    val pillColor = if (isNotice) AppColors.EmeraldGreen else Color(0xFF3B82F6)
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = pillColor.copy(alpha = 0.12f),
                        border = BorderStroke(0.5.dp, pillColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isNotice) {
                                if (isHindi) "सूचना" else "Notice"
                            } else {
                                if (isHindi) "गैलरी" else "Gallery"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = pillColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Image Preview (1:1 aspect ratio square)
            if (!item.imageUrl.isNullOrBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, borderVal),
                    color = cardBg
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = "Full Image View",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Title and Description Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, borderVal),
                color = cardBg
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val titleText = item.title ?: if (item.contentType == "gallery") {
                        if (isHindi) "गैलरी मीडिया" else "Gallery Media"
                    } else {
                        if (isHindi) "बिना शीर्षक की सूचना" else "Untitled Notice"
                    }

                    Text(
                        text = titleText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 24.sp
                    )

                    if (!item.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = borderVal)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = item.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
