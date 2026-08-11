package com.vidyasetuai.feature_case_study.presentation.screen.desbord

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl
import com.vidyasetuai.feature_case_study.data.repository.QuickRepository
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_case_study.domain.model.Quick
import com.vidyasetuai.feature_case_study.presentation.screen.subscreen.StackedQuicksCard
import com.vidyasetuai.feature_feed.data.repository.ExperienceRepository
import com.vidyasetuai.feature_feed.domain.model.Experience
import com.vidyasetuai.feature_feed.presentation.component.TwitterStyleCaseStudyFeedCard
import com.vidyasetuai.feature_feed.presentation.component.TwitterStyleExperienceFeedCard
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID

sealed class FeedItem {
    data class CaseStudyItem(val caseStudy: CaseStudy) : FeedItem()
    data class ExperienceItem(val experience: Experience) : FeedItem()
    data class QuickItem(val quick: Quick) : FeedItem()
}

@Composable
fun Modifier.shimmerPulse(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    return this.graphicsLayer(alpha = alpha)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedDesbord(
    currentLanguage: String,
    currentTheme: String,
    userId: String,
    caseStudyRepo: CaseStudyRepositoryImpl,
    experienceRepo: ExperienceRepository,
    quickRepo: QuickRepository,
    onNavigateToSubScreen: (String) -> Unit,
    cachedQuicksList: List<Quick>,
    onQuicksListChange: (List<Quick>) -> Unit,
    cachedFullFeedList: androidx.compose.runtime.snapshots.SnapshotStateList<FeedItem>,
    isFeedLoaded: Boolean,
    onFeedLoadedChange: (Boolean) -> Unit,
    onQuickClick: (List<Quick>, Int) -> Unit,
    homeTabClickCount: Int,
    checkVerification: (() -> Unit) -> Unit = { action -> action() },
    listState: androidx.compose.foundation.lazy.LazyListState = androidx.compose.foundation.lazy.rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    val isDark = when (currentTheme) {
        "dark" -> true
        "light" -> false
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val coroutineScope = rememberCoroutineScope()

    // Gallery Image Launcher for "Your Quick" story creation
    val homeQuickPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onNavigateToSubScreen("fab_add_quick:$uri")
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardBgColor = if (isDark) Color(0xFF1A1A1A) else Color(0xFFFFFFFF)
    val textColor = MaterialTheme.colorScheme.onBackground
    val subtitleColor = if (isDark) Color(0xFFA0AEC0) else Color(0xFF718096)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    // Live Feed Cache Lists
    val quicksList = cachedQuicksList
    val fullFeedList = cachedFullFeedList
    var isLoading by remember { mutableStateOf(!isFeedLoaded) }
    var isPagingLoading by remember { mutableStateOf(false) }

    val userQuicks = remember(quicksList, userId) {
        quicksList.filter { it.authorUserId == userId }
    }
    val otherGroupedQuicks = remember(quicksList, userId) {
        quicksList.filter { it.authorUserId != userId }.groupBy { it.authorUserId }.values.toList()
    }
    val fullQuicksSequence = remember(userQuicks, otherGroupedQuicks) {
        (if (userQuicks.isNotEmpty()) listOf(userQuicks) else emptyList()) + otherGroupedQuicks
    }
    val allFeedQuicks = remember(fullQuicksSequence) {
        fullQuicksSequence.flatten()
    }

    // Initial Live Fetch Function (No offline caching)
    fun fetchLatestFeed(showLoader: Boolean = false): kotlinx.coroutines.Job {
        if (showLoader) {
            isLoading = true
        }
        return coroutineScope.launch {
            try {
                // Fetch Quicks, Experiences, and Case Studies in PARALLEL via async for maximum speed
                val quicksDeferred = async { quickRepo.getQuicks(userId) }
                val experiencesDeferred = async { experienceRepo.getExperiences(userId) }
                val caseStudiesDeferred = async { caseStudyRepo.getCaseStudies(userId).firstOrNull() ?: emptyList() }

                val quicksResult = quicksDeferred.await()
                val experiencesResult = experiencesDeferred.await()
                val caseStudies = caseStudiesDeferred.await()

                onQuicksListChange(quicksResult.getOrDefault(emptyList()).take(10))

                val experiences = experiencesResult.getOrDefault(emptyList()).take(6)
                val limitedCaseStudies = caseStudies.take(6)

                // Populate main vertical feed with ONLY Case Studies and Experiences
                fullFeedList.clear()
                val tempItems = mutableListOf<FeedItem>()
                tempItems.addAll(limitedCaseStudies.map { FeedItem.CaseStudyItem(it) })
                tempItems.addAll(experiences.map { FeedItem.ExperienceItem(it) })

                // Maintain stable deterministic order (no random auto-shuffling)
                fullFeedList.addAll(tempItems)
            } catch (e: Exception) {
                // Ignore or log
            } finally {
                if (showLoader) {
                    isLoading = false
                }
            }
        }
    }

    // Trigger initial fetch
    LaunchedEffect(Unit) {
        if (!isFeedLoaded) {
            fetchLatestFeed(showLoader = true)
            onFeedLoadedChange(true)
        }
    }

    val haptic = LocalHapticFeedback.current
    var isRefreshing by remember { mutableStateOf(false) }

    // Trigger scroll to top ONLY when Home bottom tab is clicked (no refreshing or reshuffling)
    LaunchedEffect(homeTabClickCount) {
        if (homeTabClickCount > 0) {
            listState.animateScrollToItem(0)
        }
    }

    // Detect Scroll to Bottom for Pagination (infinite scrolling load more)
    val isAtBottom = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            totalItems > 0 && lastVisibleItem >= totalItems
        }
    }

    // Dynamic mock appender when user runs out of feed items
    LaunchedEffect(isAtBottom.value) {
        if (isAtBottom.value && !isLoading && !isPagingLoading && fullFeedList.isNotEmpty()) {
            isPagingLoading = true
            coroutineScope.launch {
                kotlinx.coroutines.delay(1200) // Beautiful paging transition delay

                val randomAppendList = mutableListOf<FeedItem>()
                for (i in 1..10) {
                    randomAppendList.add(
                        FeedItem.CaseStudyItem(
                            CaseStudy(
                                id = UUID.randomUUID().toString(),
                                title = "Paging Case Study: ${if (isHindi) "नवीनतम एआई अनुसंधान" else "Latest AI Advancements in Compose"}",
                                slug = "paging-case-study-$i",
                                coverImageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500",
                                shortDescription = "Learn how paging combined with state layouts enhances overall system performance and user experience.",
                                language = if (isHindi) "hi" else "en",
                                tags = listOf("Education", "Tech"),
                                readTimeMinutes = 6,
                                authorType = "platform",
                                authorUserId = null,
                                authorName = "VidyaSetu AI",
                                authorUsername = "vidyasetu",
                                authorProfilePicUrl = null,
                                isAuthorVerified = true,
                                viewCount = 345,
                                publishedAt = null,
                                createdAt = "2026-07-01T12:00:00Z",
                                updatedAt = "2026-07-01T12:00:00Z"
                            )
                        )
                    )
                    randomAppendList.add(
                        FeedItem.ExperienceItem(
                            Experience(
                                id = UUID.randomUUID().toString(),
                                title = "My placement journey #$i",
                                coverImageUrl = "https://images.unsplash.com/photo-1427504494785-3a9ca7044f45?w=500",
                                description = "Sharing my experience about landing my dream role and key materials I followed during my journey.",
                                authorUserId = "author-$i",
                                authorName = "Alumni Scholar",
                                authorUsername = "alumnus",
                                authorProfilePicUrl = null,
                                isAuthorVerified = false,
                                inspiredCount = 42,
                                isInspired = false,
                                createdAt = "2026-06-30T10:00:00Z"
                            )
                        )
                    )
                }
                fullFeedList.addAll(randomAppendList)
                isPagingLoading = false
            }
        }
    }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp),
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            if (isLoading) {
                // Skeleton Loader view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Shimmer Quicks Title
                    Row(
                        modifier = Modifier.fillMaxWidth().shimmerPulse(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.width(100.dp).height(18.dp).background(subtitleColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp)))
                        Box(modifier = Modifier.width(60.dp).height(18.dp).background(subtitleColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp)))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shimmer Quicks Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .width(110.dp)
                                    .aspectRatio(3f / 4f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(subtitleColor.copy(alpha = 0.15f))
                                    .shimmerPulse()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Shimmer Feed Card
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(subtitleColor.copy(alpha = 0.15f))
                                .shimmerPulse()
                        )
                    }
                }
            } else {
                PullToRefreshLayout(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isRefreshing = true
                        coroutineScope.launch {
                            fetchLatestFeed(showLoader = false).join()
                            isRefreshing = false
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // Horizontal Quicks Scroll ALWAYS at the top of the feed
                        if (quicksList.isNotEmpty()) {
                            item {
                                Column(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Lucide.Zap,
                                                contentDescription = null,
                                                tint = Color(0xFFFBBF24),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHindi) "क्विक" else "Quicks",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textColor
                                            )
                                        }

                                        Text(
                                            text = if (isHindi) "सभी देखें →" else "See All →",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.EmeraldGreen,
                                            modifier = Modifier.clickable { onNavigateToSubScreen("fab_quicks") }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // 1. "Your Quick / Add Story" Card (WhatsApp / Instagram style)
                                        item(key = "user-your-quick-card") {
                                            if (userQuicks.isNotEmpty()) {
                                                Box(contentAlignment = Alignment.TopEnd) {
                                                    StackedQuicksCard(
                                                        quicks = userQuicks,
                                                        isDark = isDark,
                                                        dividerColor = dividerColor,
                                                        onClick = { onQuickClick(allFeedQuicks, 0) }
                                                    )

                                                    // Add More Story (+) Badge
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(top = 4.dp, end = 4.dp)
                                                            .size(26.dp)
                                                            .clip(CircleShape)
                                                            .background(AppColors.EmeraldGreen)
                                                            .border(1.5.dp, cardBgColor, CircleShape)
                                                            .clickable { homeQuickPickerLauncher.launch("image/*") },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Lucide.Plus,
                                                            contentDescription = "Add More Quick",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .width(115.dp)
                                                        .height(153.dp)
                                                        .clickable { homeQuickPickerLauncher.launch("image/*") },
                                                    contentAlignment = Alignment.TopStart
                                                ) {
                                                    Card(
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF8FAFC)
                                                        ),
                                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .border(
                                                                width = 0.5.dp,
                                                                color = dividerColor.copy(alpha = 0.25f),
                                                                shape = RoundedCornerShape(10.dp)
                                                            )
                                                    ) {
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.Center,
                                                            modifier = Modifier.fillMaxSize().padding(8.dp)
                                                        ) {
                                                            Box(
                                                                modifier = Modifier.size(54.dp),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(48.dp)
                                                                        .clip(CircleShape)
                                                                        .background(AppColors.EmeraldGreen.copy(alpha = 0.15f)),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Lucide.User,
                                                                        contentDescription = "Your Profile",
                                                                        tint = AppColors.EmeraldGreen,
                                                                        modifier = Modifier.size(24.dp)
                                                                    )
                                                                }

                                                                // Green Plus Badge Overlay
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(20.dp)
                                                                        .align(Alignment.BottomEnd)
                                                                        .clip(CircleShape)
                                                                        .background(AppColors.EmeraldGreen)
                                                                        .border(1.5.dp, cardBgColor, CircleShape),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Lucide.Plus,
                                                                        contentDescription = "Add Quick",
                                                                        tint = Color.White,
                                                                        modifier = Modifier.size(12.dp)
                                                                    )
                                                                }
                                                            }

                                                            Spacer(modifier = Modifier.height(10.dp))

                                                            Text(
                                                                text = if (isHindi) "आपका क्विक" else "Your Quick",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = textColor,
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // 2. Other Users' Story Stacked Cards
                                        itemsIndexed(otherGroupedQuicks, key = { _, stack -> "quick-stack-${stack.first().authorUserId}" }) { index, stack ->
                                            StackedQuicksCard(
                                                quicks = stack,
                                                isDark = isDark,
                                                dividerColor = dividerColor,
                                                onClick = {
                                                    val startIdx = allFeedQuicks.indexOfFirst { q -> q.authorUserId == stack.first().authorUserId }.coerceAtLeast(0)
                                                    onQuickClick(allFeedQuicks, startIdx)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(dividerColor.copy(alpha = 0.4f)))
                            }
                        }

                        // Main Vertical Unified Feed list items (Twitter / Threads style)
                        items(fullFeedList, key = {
                            when (it) {
                                is FeedItem.CaseStudyItem -> "feed-case-${it.caseStudy.id}"
                                is FeedItem.ExperienceItem -> "feed-exp-${it.experience.id}"
                                is FeedItem.QuickItem -> "feed-quick-${it.quick.id}"
                            }
                        }) { item ->
                            when (item) {
                                is FeedItem.CaseStudyItem -> {
                                    val csAuthorId = item.caseStudy.authorUserId ?: ""
                                    val hasQuicks = quicksList.any { q -> q.authorUserId == csAuthorId }
                                    TwitterStyleCaseStudyFeedCard(
                                        caseStudy = item.caseStudy,
                                        cardBgColor = cardBgColor,
                                        textColor = textColor,
                                        subtitleColor = subtitleColor,
                                        dividerColor = dividerColor,
                                        isHindi = isHindi,
                                        hasActiveQuicks = hasQuicks,
                                        onQuickClick = {
                                            val startIdx = allFeedQuicks.indexOfFirst { q -> q.authorUserId == csAuthorId }.coerceAtLeast(0)
                                            onQuickClick(allFeedQuicks, startIdx)
                                        },
                                        onExploreClick = {
                                            onNavigateToSubScreen("case_study_detail:${item.caseStudy.id}")
                                        },
                                        onReactClick = {
                                            // Instantly update item locally in place (no reshuffle or full refresh)
                                            val index = fullFeedList.indexOf(item)
                                            if (index >= 0) {
                                                val cs = item.caseStudy
                                                val newIsReacted = !cs.isReacted
                                                val newCount = if (newIsReacted) cs.reactionCount + 1 else (cs.reactionCount - 1).coerceAtLeast(0)
                                                val updatedCs = cs.copy(isReacted = newIsReacted, reactionCount = newCount)
                                                fullFeedList[index] = FeedItem.CaseStudyItem(updatedCs)
                                            }
                                            coroutineScope.launch {
                                                caseStudyRepo.toggleReaction(item.caseStudy.id, userId)
                                            }
                                        },
                                        onAuthorClick = { authorId ->
                                            if (authorId.isNotBlank()) {
                                                onNavigateToSubScreen("public_profile:$authorId")
                                            }
                                        }
                                    )
                                }
                                is FeedItem.ExperienceItem -> {
                                    val expAuthorId = item.experience.authorUserId
                                    val hasQuicks = quicksList.any { q -> q.authorUserId == expAuthorId }
                                    TwitterStyleExperienceFeedCard(
                                        experience = item.experience,
                                        cardBgColor = cardBgColor,
                                        textColor = textColor,
                                        subtitleColor = subtitleColor,
                                        dividerColor = dividerColor,
                                        isHindi = isHindi,
                                        hasActiveQuicks = hasQuicks,
                                        onQuickClick = {
                                            val startIdx = allFeedQuicks.indexOfFirst { q -> q.authorUserId == expAuthorId }.coerceAtLeast(0)
                                            onQuickClick(allFeedQuicks, startIdx)
                                        },
                                        onReactClick = {
                                            // Instantly update item locally in place (no reshuffle or full refresh)
                                            val index = fullFeedList.indexOf(item)
                                            if (index >= 0) {
                                                val exp = item.experience
                                                val newIsInspired = !exp.isInspired
                                                val newCount = if (newIsInspired) exp.inspiredCount + 1 else (exp.inspiredCount - 1).coerceAtLeast(0)
                                                val updatedExp = exp.copy(isInspired = newIsInspired, inspiredCount = newCount)
                                                fullFeedList[index] = FeedItem.ExperienceItem(updatedExp)
                                            }
                                            coroutineScope.launch {
                                                experienceRepo.toggleInspiration(item.experience.id, userId)
                                            }
                                        },
                                        onAuthorClick = { authorId ->
                                            if (authorId.isNotBlank()) {
                                                onNavigateToSubScreen("public_profile:$authorId")
                                            }
                                        }
                                    )
                                }
                                is FeedItem.QuickItem -> {
                                    // Fallback if any QuickItem remains
                                }
                            }
                        }

                        // Shimmer loading at bottom while paging
                        if (isPagingLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(subtitleColor.copy(alpha = 0.1f))
                                        .shimmerPulse()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PullToRefreshLayout(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var pullOffset by remember { mutableStateOf(0f) }
    val maxPull = 450f // max drag distance in pixels
    val triggerThreshold = maxPull * 0.70f // Requires a deliberate downward pull to trigger refresh

    val nestedScrollConnection = remember(isRefreshing) {
        object : androidx.compose.ui.input.nestedscroll.NestedScrollConnection {
            override fun onPreScroll(
                available: androidx.compose.ui.geometry.Offset,
                source: androidx.compose.ui.input.nestedscroll.NestedScrollSource
            ): androidx.compose.ui.geometry.Offset {
                if (isRefreshing) return androidx.compose.ui.geometry.Offset.Zero

                if (available.y < 0 && pullOffset > 0) {
                    val consumed = available.y
                    pullOffset = (pullOffset + consumed).coerceAtLeast(0f)
                    return androidx.compose.ui.geometry.Offset(0f, consumed)
                }
                return androidx.compose.ui.geometry.Offset.Zero
            }

            override fun onPostScroll(
                consumed: androidx.compose.ui.geometry.Offset,
                available: androidx.compose.ui.geometry.Offset,
                source: androidx.compose.ui.input.nestedscroll.NestedScrollSource
            ): androidx.compose.ui.geometry.Offset {
                if (isRefreshing) return androidx.compose.ui.geometry.Offset.Zero

                if (available.y > 0) {
                    // Apply resistance damper so casual scrolls don't trigger pull-to-refresh accidentally
                    pullOffset = (pullOffset + available.y * 0.35f).coerceAtMost(maxPull)
                    return androidx.compose.ui.geometry.Offset(0f, available.y)
                }
                return androidx.compose.ui.geometry.Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: androidx.compose.ui.unit.Velocity,
                available: androidx.compose.ui.unit.Velocity
            ): androidx.compose.ui.unit.Velocity {
                if (pullOffset >= triggerThreshold && !isRefreshing) {
                    onRefresh()
                }
                pullOffset = 0f
                return super.onPostFling(consumed, available)
            }
        }
    }

    val animatedYOffset by animateFloatAsState(
        targetValue = if (isRefreshing) 150f else pullOffset,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "pull_to_refresh_y"
    )

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationY = animatedYOffset
                }
        ) {
            content()
        }

        if (animatedYOffset > 30f || isRefreshing) {
            val progress = if (isRefreshing) 1f else (animatedYOffset / maxPull).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 12.dp + (animatedYOffset * 0.15f).dp)
                    .size(width = 80.dp, height = 44.dp),
                contentAlignment = Alignment.Center
            ) {
                VidyaSetuRefreshIndicator(
                    progress = progress,
                    isRefreshing = isRefreshing
                )
            }
        }
    }
}

@Composable
private fun VidyaSetuRefreshIndicator(
    progress: Float,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bridge_loading")
    val particleProgress by if (isRefreshing) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_pos"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val color = AppColors.EmeraldGreen

    androidx.compose.foundation.Canvas(
        modifier = modifier.size(width = 60.dp, height = 30.dp)
    ) {
        val width = size.width
        val height = size.height

        val margin = width * 0.15f
        val leftX = margin + (width * 0.1f) * (1f - progress)
        val rightX = width - margin - (width * 0.1f) * (1f - progress)
        val centerY = height * 0.6f

        drawCircle(
            color = color,
            radius = 3.5.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(leftX, centerY)
        )

        drawCircle(
            color = color,
            radius = 3.5.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(rightX, centerY)
        )

        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(leftX, centerY)
            val controlY = centerY - (12.dp.toPx() * progress)
            quadraticTo(
                (leftX + rightX) / 2f,
                controlY,
                rightX,
                centerY
            )
        }

        drawPath(
            path = path,
            color = color.copy(alpha = if (isRefreshing) 1f else progress.coerceIn(0.1f, 1f)),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )

        if (isRefreshing) {
            val t = particleProgress
            val controlY = centerY - 12.dp.toPx()
            val particleX = (1 - t) * (1 - t) * leftX + 2 * (1 - t) * t * ((leftX + rightX) / 2f) + t * t * rightX
            val particleY = (1 - t) * (1 - t) * centerY + 2 * (1 - t) * t * controlY + t * t * centerY

            drawCircle(
                color = color,
                radius = 3.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(particleX, particleY)
            )
        }
    }
}
