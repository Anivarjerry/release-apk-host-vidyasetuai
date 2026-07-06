package com.vidyasetuai.feature_profile.presentation.screen.subscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_profile.data.remote.dto.UserProfileDto
import com.vidyasetuai.feature_profile.data.mapper.toDomain
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUserFabSubScreen(
    isHindi: Boolean,
    isDark: Boolean,
    onBack: () -> Unit,
    onUserClick: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val coroutineScope = rememberCoroutineScope()
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val cardBgColor = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchPerformed by remember { mutableStateOf(false) }

    fun executeSearch() {
        val query = searchQuery.trim()
        if (query.isEmpty()) return

        isSearching = true
        errorMessage = null
        searchPerformed = true

        coroutineScope.launch {
            try {
                val list = SupabaseClient.client.from("user_profiles")
                    .select(columns = Columns.raw("*")) {
                        filter {
                            ilike("username", "%$query%")
                        }
                    }.decodeList<UserProfileDto>()

                searchResults = list.map { it.toDomain() }
                isSearching = false
            } catch (e: Exception) {
                isSearching = false
                errorMessage = if (isHindi) "खोज करने में त्रुटि हुई। कृपया पुन: प्रयास करें।" else "Error performing search. Please try again."
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "यूज़र्स खोजें" else "Search Users",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(dividerColor)
                )
            }
        },
        containerColor = backgroundColor,
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search Input Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = cardBgColor
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = textColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = if (isHindi) "यूज़रनेम दर्ज करें..." else "Enter username...",
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { executeSearch() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = "Search",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isSearching) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.EmeraldGreen)
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Lucide.CircleX,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                    }
                }
            } else if (searchPerformed && searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isHindi) "कोई यूज़र नहीं मिला।" else "No users found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults) { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUserClick(user.userId) },
                            colors = CardDefaults.cardColors(containerColor = cardBgColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF25352E) else Color(0xFFE8F8F5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!user.profilePictureUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = user.profilePictureUrl,
                                            contentDescription = "Profile Pic",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        val initial = if (!user.firstName.isNullOrEmpty()) user.firstName.take(1).uppercase() else ""
                                        Text(
                                            text = initial,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.EmeraldGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // User Details
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.fullName ?: "${user.firstName ?: ""} ${user.lastName ?: ""}".trim(),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                        if (user.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
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
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "@${user.username ?: "contributor"}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (!user.bio.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = user.bio,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            maxLines = 2
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
}
