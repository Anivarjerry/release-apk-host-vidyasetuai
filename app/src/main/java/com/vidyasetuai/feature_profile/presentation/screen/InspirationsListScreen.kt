package com.vidyasetuai.feature_profile.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_profile.data.local.datasource.ProfileLocalDataSource
import com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource
import com.vidyasetuai.feature_profile.data.repository.ProfileRepositoryImpl
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import kotlinx.coroutines.launch

@Composable
fun InspirationsListScreen(
    currentUserId: String,
    targetUserId: String,
    initialTab: Int, // 0 = Inspirers, 1 = Inspiring
    currentLanguage: String,
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isHindi = currentLanguage == "hi"

    val profileDb = remember { AppDatabase.getDatabase(context) }
    val profileRepository = remember {
        val localDS = ProfileLocalDataSource(profileDb.userProfileDao())
        val remoteDS = ProfileRemoteDataSource()
        ProfileRepositoryImpl(localDS, remoteDS)
    }

    var selectedTab by remember { mutableStateOf(initialTab) } // 0 or 1
    var isLoading by remember { mutableStateOf(true) }

    var inspiredUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) } // Followers
    var inspiringUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) } // Following
    var currentUserInspiringIds by remember { mutableStateOf<Set<String>>(emptySet()) } // User IDs current user follows

    LaunchedEffect(targetUserId, currentUserId) {
        if (targetUserId.isNotEmpty()) {
            isLoading = true
            
            profileRepository.getInspiredUsers(targetUserId).onSuccess {
                inspiredUsers = it
            }
            
            profileRepository.getInspiringUsers(targetUserId).onSuccess {
                inspiringUsers = it
            }
            
            if (currentUserId.isNotEmpty()) {
                profileRepository.getInspiringUsers(currentUserId).onSuccess { list ->
                    currentUserInspiringIds = list.map { it.userId }.toSet()
                }
            }
            
            isLoading = false
        }
    }

    val currentList = if (selectedTab == 0) inspiredUsers else inspiringUsers

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "प्रेरणा संबंध" else "Inspiration Connections",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Tab Row Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.Center
        ) {
            // Inspirers Tab
            val isInspirers = selectedTab == 0
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = if (isHindi) "प्रेरक (Inspirers)" else "Inspirers",
                    fontSize = 14.sp,
                    fontWeight = if (isInspirers) FontWeight.Bold else FontWeight.Medium,
                    color = if (isInspirers) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(2.dp)
                        .background(if (isInspirers) AppColors.EmeraldGreen else Color.Transparent)
                )
            }

            // Inspiring Tab
            val isInspiring = selectedTab == 1
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = 1 }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = if (isHindi) "प्रेरित (Inspiring)" else "Inspiring",
                    fontSize = 14.sp,
                    fontWeight = if (isInspiring) FontWeight.Bold else FontWeight.Medium,
                    color = if (isInspiring) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(2.dp)
                        .background(if (isInspiring) AppColors.EmeraldGreen else Color.Transparent)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        // List
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.EmeraldGreen)
            }
        } else if (currentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedTab == 0) {
                        if (isHindi) "कोई प्रेरक उपलब्ध नहीं है।" else "No inspirers found."
                    } else {
                        if (isHindi) "किसी को प्रेरित नहीं किया गया है।" else "Not inspiring anyone yet."
                    },
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentList, key = { it.userId }) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUserClick(user.userId) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Avatar
                        if (!user.profilePictureUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = user.profilePictureUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(0.5.dp, Color.LightGray, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F8F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                val initial = user.firstName?.take(1)?.uppercase() ?: ""
                                Text(
                                    text = initial,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.EmeraldGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Name Details
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = user.fullName ?: "${user.firstName ?: ""} ${user.lastName ?: ""}".trim().ifEmpty { "Scholar" },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (user.isVerified) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2196F3)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Check,
                                            contentDescription = "Verified",
                                            tint = Color.White,
                                            modifier = Modifier.size(9.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "@${user.username ?: "scholar"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Connection Action Button (Only show if not the logged-in user themselves)
                        if (user.userId != currentUserId) {
                            val isFollowing = currentUserInspiringIds.contains(user.userId)
                            if (isFollowing) {
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            profileRepository.toggleUserInspiration(currentUserId, user.userId).onSuccess { nextState ->
                                                if (nextState) {
                                                    currentUserInspiringIds = currentUserInspiringIds + user.userId
                                                } else {
                                                    currentUserInspiringIds = currentUserInspiringIds - user.userId
                                                }
                                                // Refresh lists if target is current user
                                                if (targetUserId == currentUserId) {
                                                    profileRepository.getInspiringUsers(targetUserId).onSuccess { list ->
                                                        inspiringUsers = list
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    border = BorderStroke(1.dp, AppColors.EmeraldGreen),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.EmeraldGreen),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) "प्रेरित" else "Inspired",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            profileRepository.toggleUserInspiration(currentUserId, user.userId).onSuccess { nextState ->
                                                if (nextState) {
                                                    currentUserInspiringIds = currentUserInspiringIds + user.userId
                                                } else {
                                                    currentUserInspiringIds = currentUserInspiringIds - user.userId
                                                }
                                                if (targetUserId == currentUserId) {
                                                    profileRepository.getInspiringUsers(targetUserId).onSuccess { list ->
                                                        inspiringUsers = list
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) "प्रेरित हों" else "Be Inspired",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
