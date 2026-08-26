package com.vidyasetuai.feature_profile.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_profile.presentation.component.ProfileImageLightboxDialog
import com.vidyasetuai.feature_profile.presentation.viewmodel.ProfileEvent
import com.vidyasetuai.feature_profile.presentation.viewmodel.ProfileViewModel
import java.io.File

/**
 * Public Profile Screen for viewing other members on Campus/VidyaSetu.
 * Includes Follow toggle, Direct E2EE Chat shortcut, and layered BackHandler.
 */
@Composable
fun PublicProfileScreen(
    currentUserId: String = "",
    targetUserId: String = "",
    currentLanguage: String = "en",
    onBackClick: () -> Unit = {},
    onInspirationsClick: (String, Int) -> Unit = { _, _ -> },
    onCaseStudyClick: (String) -> Unit = {},
    onDirectChatClick: (String) -> Unit = {},
    viewModel: ProfileViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(targetUserId) {
        if (targetUserId.isNotBlank()) {
            viewModel.onEvent(ProfileEvent.LoadProfile(targetUserId))
        }
    }

    BackHandler(enabled = true) {
        if (state.isImageLightboxOpen) {
            viewModel.onEvent(ProfileEvent.ToggleImageLightbox(false))
        } else {
            onBackClick()
        }
    }

    val profile = state.profile
    val avatarSource = profile?.profilePictureLocalPath?.takeIf { File(it).exists() } ?: profile?.profilePictureUrl
    val coverSource = profile?.coverPhotoLocalPath?.takeIf { File(it).exists() } ?: profile?.coverPhotoUrl

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item(key = "public_header") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                                )
                            )
                    ) {
                        if (!coverSource.isNullOrBlank()) {
                            AsyncImage(
                                model = coverSource,
                                contentDescription = "Cover Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.45f))
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .offset(y = (-46).dp)
                            .size(92.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    if (!avatarSource.isNullOrBlank()) {
                                        viewModel.onEvent(ProfileEvent.ToggleImageLightbox(true, avatarSource))
                                    }
                                },
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(3.dp, Color.White),
                            shadowElevation = 4.dp
                        ) {
                            if (!avatarSource.isNullOrBlank()) {
                                AsyncImage(
                                    model = avatarSource,
                                    contentDescription = profile?.fullName ?: "Avatar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                val initials = (profile?.fullName ?: "Member")
                                    .split(" ")
                                    .filter { it.isNotEmpty() }
                                    .take(2)
                                    .map { it.first().uppercase() }
                                    .joinToString("")
                                    .ifEmpty { "M" }

                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            color = Color(0xFF10B981)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .offset(y = (-36).dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = profile?.fullName?.ifBlank { "Member" } ?: "Member",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 21.sp,
                                    color = Color(0xFF0F172A)
                                )
                            )

                            if (profile?.isVerified == true) {
                                Icon(
                                    imageVector = Lucide.ShieldCheck,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "@${profile?.username ?: "user"}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        )

                        if (!profile?.bio.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = profile?.bio ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = Color(0xFF334155),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .clickable { onDirectChatClick(targetUserId) },
                                shape = RoundedCornerShape(22.dp),
                                color = Color(0xFF10B981)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.MessageSquare,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Direct Chat",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { onInspirationsClick(targetUserId, 0) },
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${profile?.totalInspiringCount ?: 0}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    )
                                    Text(
                                        text = "Inspirations",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { onInspirationsClick(targetUserId, 1) },
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${profile?.totalInspiredCount ?: 0}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    )
                                    Text(
                                        text = "Inspired",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.isImageLightboxOpen) {
            ProfileImageLightboxDialog(
                imageUrl = state.lightboxImageUrl,
                onDismiss = { viewModel.onEvent(ProfileEvent.ToggleImageLightbox(false)) }
            )
        }
    }
}
