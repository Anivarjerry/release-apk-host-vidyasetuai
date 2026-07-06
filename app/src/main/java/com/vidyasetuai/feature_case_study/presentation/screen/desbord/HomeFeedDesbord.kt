package com.vidyasetuai.feature_case_study.presentation.screen.desbord

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
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl
import com.vidyasetuai.feature_case_study.data.repository.QuickRepository
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_case_study.domain.model.Quick
import com.vidyasetuai.feature_case_study.presentation.screen.subscreen.StackedQuicksCard
import com.vidyasetuai.feature_feed.data.repository.ExperienceRepository
import com.vidyasetuai.feature_feed.domain.model.Experience
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

sealed class FeedItem(open val id: String, open val createdAt: String) {
    data class CaseStudyItem(val caseStudy: CaseStudy) : FeedItem(caseStudy.id, caseStudy.createdAt)
    data class ExperienceItem(val experience: Experience) : FeedItem(experience.id, experience.createdAt)
    data class QuickItem(val quick: Quick) : FeedItem(quick.id, quick.createdAt)
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
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    val isDark = when (currentTheme) {
        "dark" -> true
        "light" -> false
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardBgColor = if (isDark) Color(0xFF1A1A1A) else Color(0xFFFFFFFF)
    val inputBgColor = if (isDark) Color(0xFF262626) else Color(0xFFF7FAFC)
    val textColor = MaterialTheme.colorScheme.onBackground
    val subtitleColor = if (isDark) Color(0xFFA0AEC0) else Color(0xFF718096)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    // Tabs: 0 -> All, 1 -> Case Studies, 2 -> Experiences
    var selectedTab by remember { mutableStateOf(0) }

    // Live Feed Cache Lists
    val quicksList = cachedQuicksList
    val fullFeedList = cachedFullFeedList
    var isLoading by remember { mutableStateOf(!isFeedLoaded) }
    var isPagingLoading by remember { mutableStateOf(false) }

    // Initial Live Fetch Function (No offline caching)
    fun fetchLatestFeed(showLoader: Boolean = false): kotlinx.coroutines.Job {
        if (showLoader) {
            isLoading = true
        }
        return coroutineScope.launch {
            try {
                // Fetch Quicks
                val quicksResult = quickRepo.getQuicks(userId)
                onQuicksListChange(quicksResult.getOrDefault(emptyList()).take(20))

                // Fetch Experiences
                val experiencesResult = experienceRepo.getExperiences(userId)
                val experiences = experiencesResult.getOrDefault(emptyList()).take(20)

                // Fetch Case Studies
                val caseStudies = caseStudyRepo.getCaseStudies(userId).firstOrNull() ?: emptyList()
                val limitedCaseStudies = caseStudies.take(20)

                // Interleave into main Mixed Feed
                fullFeedList.clear()
                val tempItems = mutableListOf<FeedItem>()
                tempItems.addAll(limitedCaseStudies.map { FeedItem.CaseStudyItem(it) })
                tempItems.addAll(experiences.map { FeedItem.ExperienceItem(it) })
                tempItems.addAll(quicksList.map { FeedItem.QuickItem(it) })

                // Shuffle the feed items randomly so the feed changes on every load/refresh
                tempItems.shuffle()
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

    // Trigger scroll to top or reload when Home bottom tab is clicked
    LaunchedEffect(homeTabClickCount) {
        if (homeTabClickCount > 0) {
            if (listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0) {
                listState.animateScrollToItem(0)
            } else {
                fetchLatestFeed(showLoader = true)
            }
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

                // Add 30 more simulated items to feed cache
                val randomAppendList = mutableListOf<FeedItem>()
                for (i in 1..10) {
                    randomAppendList.add(
                        FeedItem.CaseStudyItem(
                            CaseStudy(
                                id = UUID.randomUUID().toString(),
                                title = "Paging Case Study: ${if (isHindi) "नवीनतम एआई अनुसंधान" else "Latest AI Advancements in Compose"}",
                                slug = "paging-case-study-$i",
                                coverImageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500",
                                shortDescription = "Learn how paging combined with state layouts enhances overall system performance.",
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
                                description = "Sharing my experience about landing my dream role and key materials I followed.",
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
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {

                // Tabs matching mockup: ALL | CASE STUDIES | EXPERIENCES
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = backgroundColor,
                    contentColor = AppColors.EmeraldGreen,
                    divider = { Divider(color = dividerColor.copy(alpha = 0.5f), thickness = 0.5.dp) },
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AppColors.EmeraldGreen,
                            height = 2.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(if (isHindi) "सभी" else "All", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(if (isHindi) "केस स्टडी" else "Case Studies", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(if (isHindi) "अनुभव" else "Experiences", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            if (isLoading) {
                // Proper premium Skeleton Loader view
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
                                .height(130.dp)
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(subtitleColor.copy(alpha = 0.15f))
                                .shimmerPulse()
                        )
                    }
                }
            } else {
                // Mixed Feed Column
                val filteredFeed = remember(selectedTab, fullFeedList.size) {
                    when (selectedTab) {
                        1 -> fullFeedList.filterIsInstance<FeedItem.CaseStudyItem>()
                        2 -> fullFeedList.filterIsInstance<FeedItem.ExperienceItem>()
                        else -> fullFeedList
                    }
                }

                var isRefreshing by remember { mutableStateOf(false) }

                PullToRefreshLayout(
                    isRefreshing = isRefreshing,
                    onRefresh = {
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
                        // Horizontal Quicks Scroll at the top of the feed (Only on 'ALL' tab)
                        if (selectedTab == 0 && quicksList.isNotEmpty()) {
                            item {
                                Column(modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
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

                                    val groupedQuicks = remember(quicksList) {
                                        quicksList.groupBy { it.authorUserId }.values.toList()
                                    }

                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        itemsIndexed(groupedQuicks, key = { _, stack -> "quick-stack-${stack.first().authorUserId}" }) { index, stack ->
                                            StackedQuicksCard(
                                                quicks = stack,
                                                isDark = isDark,
                                                dividerColor = dividerColor,
                                                onClick = { onQuickClick(stack, 0) }
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Divider(color = dividerColor.copy(alpha = 0.4f), thickness = 0.5.dp)
                            }
                        }

                        // Mixed Feed list items
                        items(filteredFeed, key = {
                            when (it) {
                                is FeedItem.CaseStudyItem -> "feed-case-${it.caseStudy.id}"
                                is FeedItem.ExperienceItem -> "feed-exp-${it.experience.id}"
                                is FeedItem.QuickItem -> "feed-quick-${it.quick.id}"
                            }
                        }) { item ->
                            when (item) {
                                is FeedItem.CaseStudyItem -> {
                                    CaseStudyFeedCard(
                                        caseStudy = item.caseStudy,
                                        cardBgColor = cardBgColor,
                                        textColor = textColor,
                                        subtitleColor = subtitleColor,
                                        dividerColor = dividerColor,
                                        isHindi = isHindi,
                                        onExploreClick = {
                                            onNavigateToSubScreen("case_study_detail:${item.caseStudy.id}")
                                        },
                                        onReactClick = {
                                            coroutineScope.launch {
                                                caseStudyRepo.toggleReaction(item.caseStudy.id, userId).onSuccess {
                                                    fetchLatestFeed()
                                                }
                                            }
                                        }
                                    )
                                }
                                is FeedItem.ExperienceItem -> {
                                    ExperienceFeedCard(
                                        experience = item.experience,
                                        cardBgColor = cardBgColor,
                                        textColor = textColor,
                                        subtitleColor = subtitleColor,
                                        dividerColor = dividerColor,
                                        isHindi = isHindi,
                                        onReactClick = {
                                            coroutineScope.launch {
                                                experienceRepo.toggleInspiration(item.experience.id, userId).onSuccess {
                                                    fetchLatestFeed()
                                                }
                                            }
                                        }
                                    )
                                }
                                is FeedItem.QuickItem -> {
                                    QuickFeedCard(
                                        quick = item.quick,
                                        cardBgColor = cardBgColor,
                                        textColor = textColor,
                                        subtitleColor = subtitleColor,
                                        dividerColor = dividerColor,
                                        isHindi = isHindi,
                                        onReactClick = {
                                            coroutineScope.launch {
                                                quickRepo.toggleHelpful(item.quick.id, userId).onSuccess {
                                                    fetchLatestFeed()
                                                }
                                            }
                                        }
                                    )
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

// 1. Case Study Card Composable matching design
@Composable
fun CaseStudyFeedCard(
    caseStudy: CaseStudy,
    cardBgColor: Color,
    textColor: Color,
    subtitleColor: Color,
    dividerColor: Color,
    isHindi: Boolean,
    onExploreClick: () -> Unit,
    onReactClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .border(0.5.dp, dividerColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Large Cover Image at top (16:9)
            if (!caseStudy.coverImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = caseStudy.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Title
            Text(
                text = caseStudy.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Short Description
            Text(
                text = caseStudy.shortDescription,
                fontSize = 13.sp,
                color = subtitleColor,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(dividerColor.copy(alpha = 0.4f)))
            Spacer(modifier = Modifier.height(10.dp))

            // Footer Row: Inspired count, Views count, Explore Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Inspired reaction button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onReactClick() }
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.Lightbulb,
                        contentDescription = null,
                        tint = if (caseStudy.isReacted) Color(0xFFF59E0B) else subtitleColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "प्रेरित ${caseStudy.reactionCount}" else "Inspired ${caseStudy.reactionCount}",
                        fontSize = 11.sp,
                        fontWeight = if (caseStudy.isReacted) FontWeight.Bold else FontWeight.Normal,
                        color = if (caseStudy.isReacted) Color(0xFFF59E0B) else subtitleColor
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Views count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Lucide.Eye, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${caseStudy.viewCount}", fontSize = 11.sp, color = subtitleColor)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Explore button
                TextButton(
                    onClick = onExploreClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = AppColors.EmeraldGreen),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isHindi) "देखें →" else "Explore →",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// 2. Experience Card Composable matching design
@Composable
fun ExperienceFeedCard(
    experience: Experience,
    cardBgColor: Color,
    textColor: Color,
    subtitleColor: Color,
    dividerColor: Color,
    isHindi: Boolean,
    onReactClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .border(0.5.dp, dividerColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFFAE8FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.User,
                        contentDescription = null,
                        tint = Color(0xFFA21CAF),
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "अनुभव" else "EXPERIENCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subtitleColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(imageVector = Lucide.Ellipsis, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(14.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Row: Text + image thumbnail
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = experience.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = experience.description,
                        fontSize = 12.sp,
                        color = subtitleColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }

                if (!experience.coverImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = experience.coverImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(dividerColor.copy(alpha = 0.5f)))
            Spacer(modifier = Modifier.height(8.dp))

            // Footer Row: Inspired button only
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onReactClick() }
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.ThumbsUp,
                        contentDescription = null,
                        tint = if (experience.isInspired) Color(0xFFF59E0B) else subtitleColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "प्रेरित ${experience.inspiredCount}" else "Inspired ${experience.inspiredCount}",
                        fontSize = 11.sp,
                        fontWeight = if (experience.isInspired) FontWeight.Bold else FontWeight.Normal,
                        color = if (experience.isInspired) Color(0xFFF59E0B) else subtitleColor
                    )
                }
            }
        }
    }
}

// 3. Quick Card in Feed Composable matching design
@Composable
fun QuickFeedCard(
    quick: Quick,
    cardBgColor: Color,
    textColor: Color,
    subtitleColor: Color,
    dividerColor: Color,
    isHindi: Boolean,
    onReactClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .border(0.5.dp, dividerColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFECFDF5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Zap,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "क्विक" else "QUICK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subtitleColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AI",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Lucide.Ellipsis, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(14.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Row: Thumbnail on left, title on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!quick.coverImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = quick.coverImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quick.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(dividerColor.copy(alpha = 0.5f)))
            Spacer(modifier = Modifier.height(8.dp))

            // Footer Row: Helpful count, Views count, Clock/Time left
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onReactClick() }
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.ThumbsUp,
                        contentDescription = null,
                        tint = if (quick.isHelpful) AppColors.EmeraldGreen else subtitleColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "मददगार ${quick.helpfulCount}" else "Helpful ${quick.helpfulCount}",
                        fontSize = 11.sp,
                        fontWeight = if (quick.isHelpful) FontWeight.Bold else FontWeight.Normal,
                        color = if (quick.isHelpful) AppColors.EmeraldGreen else subtitleColor
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Lucide.Eye, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${quick.viewsCount}", fontSize = 11.sp, color = subtitleColor)
                }

                Spacer(modifier = Modifier.weight(1f))

                Icon(imageVector = Lucide.Clock, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = getHoursLeft(quick.expiresAt),
                    fontSize = 11.sp,
                    color = subtitleColor
                )
            }
        }
    }
}

private fun getHoursLeft(expiresAtStr: String): String {
    return try {
        val expiresInstant = java.time.Instant.parse(expiresAtStr)
        val nowInstant = java.time.Instant.now()
        val diff = expiresInstant.epochSecond - nowInstant.epochSecond
        if (diff <= 0) {
            "Expired"
        } else {
            val hours = diff / 3600
            if (hours == 0L) {
                val minutes = diff / 60
                "${minutes}m left"
            } else {
                "${hours}h left"
            }
        }
    } catch (e: Exception) {
        "12h left"
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
    val maxPull = 300f // max drag distance in pixels
    val coroutineScope = rememberCoroutineScope()
    
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
                    pullOffset = (pullOffset + available.y).coerceAtMost(maxPull)
                    return androidx.compose.ui.geometry.Offset(0f, available.y)
                }
                return androidx.compose.ui.geometry.Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: androidx.compose.ui.unit.Velocity,
                available: androidx.compose.ui.unit.Velocity
            ): androidx.compose.ui.unit.Velocity {
                if (pullOffset >= maxPull * 0.5f && !isRefreshing) {
                    onRefresh()
                }
                pullOffset = 0f
                return super.onPostFling(consumed, available)
            }
        }
    }

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationY = pullOffset
                }
        ) {
            content()
        }

        if (pullOffset > 0 || isRefreshing) {
            val progress = (pullOffset / maxPull).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 12.dp + (pullOffset * 0.15f).dp)
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

