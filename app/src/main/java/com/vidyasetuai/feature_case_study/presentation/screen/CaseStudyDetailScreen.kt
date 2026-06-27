package com.vidyasetuai.feature_case_study.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ThumbsUp
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.data.local.datasource.CaseStudyLocalDataSource
import com.vidyasetuai.feature_case_study.data.remote.datasource.CaseStudyRemoteDataSource
import com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl
import com.vidyasetuai.feature_case_study.domain.usecase.GetCaseStudyDetailUseCase
import com.vidyasetuai.feature_case_study.domain.usecase.ToggleBookmarkUseCase
import com.vidyasetuai.feature_case_study.domain.usecase.ToggleReactionUseCase
import com.vidyasetuai.feature_case_study.presentation.component.ContentBlockRenderer
import com.vidyasetuai.feature_case_study.presentation.event.CaseStudyEvent
import com.vidyasetuai.feature_case_study.presentation.viewmodel.CaseStudyDetailViewModel

@Composable
fun CaseStudyDetailScreen(
    caseStudyId: String,
    userId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val viewModel = remember {
        val db = AppDatabase.getDatabase(context)
        val localDS = CaseStudyLocalDataSource(db.caseStudyDao())
        val remoteDS = CaseStudyRemoteDataSource()
        val repo = CaseStudyRepositoryImpl(localDS, remoteDS)
        val detailUseCase = GetCaseStudyDetailUseCase(repo)
        val reactionUseCase = ToggleReactionUseCase(repo)
        val bookmarkUseCase = ToggleBookmarkUseCase(repo)
        CaseStudyDetailViewModel(detailUseCase, reactionUseCase, bookmarkUseCase)
    }

    LaunchedEffect(caseStudyId, userId) {
        if (caseStudyId.isNotEmpty() && userId.isNotEmpty()) {
            viewModel.loadCaseStudyDetail(caseStudyId, userId)
        }
    }

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.caseStudy?.title ?: "Case Study",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE5E5EA))
                )
            }
        },
        bottomBar = {
            state.caseStudy?.let { caseStudy ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .navigationBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE5E5EA))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.onEvent(CaseStudyEvent.ToggleReaction(caseStudy.id), userId)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.ThumbsUp,
                                contentDescription = "Reaction",
                                tint = if (caseStudy.isReacted) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = if (caseStudy.reactionCount > 0) "${caseStudy.reactionCount} Inspired" else "Inspired",
                                fontSize = 13.sp,
                                fontWeight = if (caseStudy.isReacted) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (caseStudy.isReacted) AppColors.EmeraldGreen else Color(0xFF8E8E93)
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.onEvent(CaseStudyEvent.ToggleBookmark(caseStudy.id), userId)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Bookmark,
                                contentDescription = "Bookmark",
                                tint = if (caseStudy.isBookmarked) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            if (state.isLoading && state.caseStudy == null) {
                CircularProgressIndicator(
                    color = AppColors.EmeraldGreen,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.caseStudy == null && !state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Failed to load case study.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                state.caseStudy?.let { caseStudy ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        item {
                            AsyncImage(
                                model = caseStudy.coverImageUrl,
                                contentDescription = "Cover Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16 / 9f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF2F2F7)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = caseStudy.language.replaceFirstChar { it.uppercase() },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF636366),
                                    modifier = Modifier
                                        .background(Color(0xFFF2F2F7), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                                if (caseStudy.readTimeMinutes != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Clock,
                                            contentDescription = "Read Time",
                                            tint = Color(0xFF8E8E93),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${caseStudy.readTimeMinutes} min read",
                                            fontSize = 12.sp,
                                            color = Color(0xFF8E8E93)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Text(
                                text = caseStudy.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                lineHeight = 30.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = caseStudy.shortDescription,
                                fontSize = 15.sp,
                                color = Color(0xFF636366),
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFFE5E5EA))
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(caseStudy.contentBlocks) { block ->
                            ContentBlockRenderer(block = block)
                        }

                        if (caseStudy.tags.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Tags",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    mainAxisSpacing = 8.dp,
                                    crossAxisSpacing = 8.dp
                                ) {
                                    caseStudy.tags.forEach { tag ->
                                        Text(
                                            text = "#$tag",
                                            fontSize = 12.sp,
                                            color = Color(0xFF636366),
                                            modifier = Modifier
                                                .background(Color(0xFFF2F2F7), RoundedCornerShape(16.dp))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
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

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val layoutWidth = constraints.maxWidth
        val lines = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentLine = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentWidth = 0
        val mainSpacingPx = mainAxisSpacing.roundToPx()
        val crossSpacingPx = crossAxisSpacing.roundToPx()

        placeables.forEach { placeable ->
            if (currentWidth + placeable.width + mainSpacingPx > layoutWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = mutableListOf()
                currentWidth = 0
            }
            currentLine.add(placeable)
            currentWidth += placeable.width + mainSpacingPx
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        var totalHeight = 0
        lines.forEachIndexed { index, line ->
            val maxHeight = line.maxOf { it.height }
            totalHeight += maxHeight
            if (index < lines.size - 1) {
                totalHeight += crossSpacingPx
            }
        }

        layout(layoutWidth, totalHeight) {
            var y = 0
            lines.forEach { line ->
                var x = 0
                val maxHeight = line.maxOf { it.height }
                line.forEach { placeable ->
                    placeable.placeRelative(x, y + (maxHeight - placeable.height) / 2)
                    x += placeable.width + mainSpacingPx
                }
                y += maxHeight + crossSpacingPx
            }
        }
    }
}
