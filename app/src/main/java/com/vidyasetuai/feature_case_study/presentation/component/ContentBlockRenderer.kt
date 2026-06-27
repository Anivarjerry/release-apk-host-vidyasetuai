package com.vidyasetuai.feature_case_study.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.TriangleAlert
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.domain.model.ContentBlock

@Composable
fun ContentBlockRenderer(
    block: ContentBlock,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        when (block) {
            is ContentBlock.Title -> {
                Text(
                    text = block.text,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1C1C1E),
                    lineHeight = 32.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            is ContentBlock.Section -> {
                Text(
                    text = block.text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E),
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            is ContentBlock.SubSection -> {
                Text(
                    text = block.text,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1C1C1E),
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                )
            }
            is ContentBlock.Paragraph -> {
                Text(
                    text = block.text,
                    fontSize = 16.sp,
                    color = Color(0xFF1C1C1E),
                    lineHeight = 24.sp
                )
            }
            is ContentBlock.ImageBlock -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = block.url,
                        contentDescription = block.caption ?: "Case Study Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF2F2F7)),
                        contentScale = ContentScale.Crop
                    )
                    block.caption?.let { caption ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = caption,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF8E8E93),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
            is ContentBlock.VideoLink -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { uriHandler.openUri(block.url) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (block.thumbnailUrl != null) {
                            AsyncImage(
                                model = block.thumbnailUrl,
                                contentDescription = block.title ?: "Video Thumbnail",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White, RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Play,
                                    contentDescription = "Play Video",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp).padding(start = 2.dp)
                                )
                            }
                        }
                    }
                    block.title?.let { title ->
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
            is ContentBlock.Quote -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.5.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF8E8E93), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "\"${block.text}\"",
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF48484A),
                            lineHeight = 24.sp
                        )
                        block.author?.let { author ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "— $author",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF8E8E93)
                            )
                        }
                    }
                }
            }
            is ContentBlock.Checklist -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    block.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Check",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(18.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                color = Color(0xFF1C1C1E),
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
            is ContentBlock.ResourceLink -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { uriHandler.openUri(block.url) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ExternalLink,
                            contentDescription = "Resource",
                            tint = Color(0xFF636366),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = block.label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1C1C1E)
                        )
                    }
                    Text(
                        text = "Open",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                }
            }
            is ContentBlock.Tip -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Lucide.Info,
                        contentDescription = "Tip",
                        tint = Color(0xFF636366),
                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = block.text,
                        fontSize = 14.sp,
                        color = Color(0xFF2C2C2E),
                        lineHeight = 20.sp
                    )
                }
            }
            is ContentBlock.Warning -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF9F6), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFBEADF), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Lucide.TriangleAlert,
                        contentDescription = "Warning",
                        tint = Color(0xFFE27C3E),
                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = block.text,
                        fontSize = 14.sp,
                        color = Color(0xFF2C2C2E),
                        lineHeight = 20.sp
                    )
                }
            }
            is ContentBlock.Unknown -> {
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}
