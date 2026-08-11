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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource
import com.vidyasetuai.feature_profile.data.remote.dto.UserProfileDto
import com.vidyasetuai.feature_profile.data.mapper.toDomain
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
private data class InspirationRecord(
    val id: String = "",
    val inspirer_id: String = "",
    val inspired_id: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUserFabSubScreen(
    isHindi: Boolean,
    isDark: Boolean,
    currentUserId: String = "",
    onBack: () -> Unit,
    onUserClick: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val coroutineScope = rememberCoroutineScope()
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val cardBgColor = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)

    var activeSubTab by remember { mutableStateOf(0) } // 0 = Search Users, 1 = Requests

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchPerformed by remember { mutableStateOf(false) }

    val inspiredUserIds = remember { mutableStateListOf<String>() }

    var incomingRequests by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoadingIncoming by remember { mutableStateOf(false) }

    val profileRemoteDS = remember { ProfileRemoteDataSource() }

    val context = androidx.compose.ui.platform.LocalContext.current
    val campusDao = remember { com.vidyasetuai.core.database.AppDatabase.getDatabase(context).campusDao() }

    // Fetch list of users already inspired by current user
    fun loadMyInspirations() {
        if (currentUserId.isEmpty()) return
        coroutineScope.launch {
            try {
                val list = profileRemoteDS.getInspiringUsers(currentUserId)
                inspiredUserIds.clear()
                inspiredUserIds.addAll(list.map { it.user_id })
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    // Fetch list of incoming requests (users who inspired current user)
    fun loadIncomingRequests() {
        if (currentUserId.isEmpty()) return
        coroutineScope.launch {
            // 1. Instant 0ms Local Load from Room DB (eliminates green loading spinner if cached data exists)
            val cachedEntities = campusDao.getConnectionRequests()
            if (cachedEntities.isNotEmpty()) {
                incomingRequests = cachedEntities.map { entity ->
                    UserProfile(
                        userId = entity.userId,
                        email = entity.email,
                        isActive = true,
                        isDeleted = false,
                        username = entity.username,
                        firstName = entity.firstName,
                        lastName = entity.lastName,
                        fullName = entity.fullName,
                        profilePictureUrl = entity.profilePictureUrl,
                        coverPhotoUrl = null,
                        bio = entity.bio,
                        preferredLanguage = null,
                        isVerified = false,
                        gender = null,
                        dateOfBirth = "",
                        totalInspiringCount = 0,
                        totalInspiredCount = 0
                    )
                }
                isLoadingIncoming = false
            } else {
                isLoadingIncoming = true
            }

            // 2. Silent Background Sync from Supabase
            try {
                val inspiredList = profileRemoteDS.getInspiredUsers(currentUserId)
                val inspiringList = profileRemoteDS.getInspiringUsers(currentUserId)

                val incomingOnly = inspiredList.filter { inc -> inspiringList.none { it.user_id == inc.user_id } }

                val newEntities = incomingOnly.map { dto ->
                    com.vidyasetuai.feature_campus.data.local.entity.ConnectionRequestEntity(
                        userId = dto.user_id,
                        email = dto.email ?: "",
                        username = dto.username,
                        firstName = dto.first_name,
                        lastName = dto.last_name,
                        fullName = dto.full_name,
                        profilePictureUrl = dto.profile_picture_url,
                        bio = dto.bio,
                        requestDirection = "INCOMING"
                    )
                }

                campusDao.clearConnectionRequests()
                if (newEntities.isNotEmpty()) {
                    campusDao.insertConnectionRequests(newEntities)
                }

                incomingRequests = incomingOnly.map { dto ->
                    UserProfile(
                        userId = dto.user_id,
                        email = dto.email ?: "",
                        isActive = dto.is_active,
                        isDeleted = dto.is_deleted,
                        username = dto.username,
                        firstName = dto.first_name,
                        lastName = dto.last_name,
                        fullName = dto.full_name,
                        profilePictureUrl = dto.profile_picture_url,
                        coverPhotoUrl = dto.cover_photo_url,
                        bio = dto.bio,
                        preferredLanguage = dto.preferred_language,
                        isVerified = dto.is_verified,
                        gender = dto.gender,
                        dateOfBirth = dto.date_of_birth ?: "",
                        totalInspiringCount = dto.total_inspiring_count,
                        totalInspiredCount = dto.total_inspired_count
                    )
                }
                isLoadingIncoming = false
            } catch (e: Exception) {
                isLoadingIncoming = false
            }
        }
    }

    LaunchedEffect(currentUserId) {
        loadMyInspirations()
        loadIncomingRequests()
    }

    fun toggleInspire(targetUserId: String) {
        if (currentUserId.isEmpty() || targetUserId == currentUserId) return

        // Instant Optimistic Local UI update
        coroutineScope.launch {
            campusDao.deleteConnectionRequest(targetUserId)
            incomingRequests = incomingRequests.filter { it.userId != targetUserId }
        }

        coroutineScope.launch {
            try {
                val isNowInspiring = profileRemoteDS.toggleUserInspiration(
                    inspiredUserId = currentUserId,
                    inspiringUserId = targetUserId
                )
                if (isNowInspiring) {
                    if (!inspiredUserIds.contains(targetUserId)) {
                        inspiredUserIds.add(targetUserId)
                    }
                } else {
                    inspiredUserIds.remove(targetUserId)
                }
                loadIncomingRequests()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

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

                searchResults = list.map { it.toDomain() }.filter { it.userId != currentUserId }
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (isHindi) "लोग खोजें एवं जुड़ें" else "People & Connections",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                // 2 Segmented Tabs: 0 = Search Users, 1 = Requests
                TabRow(
                    selectedTabIndex = activeSubTab,
                    containerColor = Color.Transparent,
                    contentColor = AppColors.EmeraldGreen,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                            color = AppColors.EmeraldGreen
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = activeSubTab == 0,
                        onClick = { activeSubTab = 0 },
                        text = {
                            Text(
                                text = if (isHindi) "यूज़र्स खोजें" else "Search Users",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = activeSubTab == 1,
                        onClick = {
                            activeSubTab = 1
                            loadIncomingRequests()
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "अनुरोध (Requests)" else "Requests",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (incomingRequests.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${incomingRequests.size}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(dividerColor)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (activeSubTab == 0) {
                // Sub-Tab 0: Search Users
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBgColor)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            color = textColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = if (isHindi) "यूज़रनेम खोजें..." else "Search username...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = { executeSearch() },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isHindi) "खोजें" else "Search",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isSearching) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.EmeraldGreen)
                    }
                } else if (errorMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                } else if (searchPerformed && searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
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
                            val isInspiring = inspiredUserIds.contains(user.userId)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUserClick(user.userId) },
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
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
                                            val initial = if (!user.firstName.isNullOrEmpty()) user.firstName.take(1).uppercase() else "U"
                                            Text(
                                                text = initial,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.EmeraldGreen
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // User Details
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = user.fullName ?: "${user.firstName ?: ""} ${user.lastName ?: ""}".trim(),
                                                fontSize = 15.sp,
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
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                }
                                            }
                                        }
                                        if (!user.username.isNullOrEmpty()) {
                                            Text(
                                                text = "@${user.username}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = if (isHindi) "दोनों के जुड़ने पर 1-on-1 चैट चालू होगी" else "Mutual connection unlocks 1-on-1 chat",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Action Button: Inspire / Inspiring (Send Connection Request)
                                    Button(
                                        onClick = { toggleInspire(user.userId) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isInspiring) MaterialTheme.colorScheme.surfaceVariant else AppColors.EmeraldGreen
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(
                                            text = if (isInspiring) {
                                                if (isHindi) "प्रेरित हो रहे हैं ✓" else "Inspiring ✓"
                                            } else {
                                                if (isHindi) "प्रेरित करें (Inspire)" else "Inspire"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isInspiring) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Sub-Tab 1: Incoming Connection Requests
                if (isLoadingIncoming) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.EmeraldGreen)
                    }
                } else if (incomingRequests.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isHindi) "कोई नया कनेक्शन अनुरोध नहीं है।" else "No pending connection requests.",
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
                        items(incomingRequests) { incUser ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(AppColors.EmeraldGreen.copy(alpha = 0.14f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!incUser.profilePictureUrl.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = incUser.profilePictureUrl,
                                                contentDescription = "Pic",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            val initial = if (!incUser.firstName.isNullOrEmpty()) incUser.firstName.take(1).uppercase() else "U"
                                            Text(
                                                text = initial,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.EmeraldGreen
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = incUser.fullName ?: "${incUser.firstName ?: ""} ${incUser.lastName ?: ""}".trim(),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                        Text(
                                            text = if (isHindi) "आपको Inspire कर रहे हैं" else "Inspired by you",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppColors.EmeraldGreen
                                        )
                                    }

                                    Button(
                                        onClick = { toggleInspire(incUser.userId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "वापस Inspire करें (स्वीकारें)" else "Inspire Back (Accept)",
                                            fontSize = 11.sp,
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
}
