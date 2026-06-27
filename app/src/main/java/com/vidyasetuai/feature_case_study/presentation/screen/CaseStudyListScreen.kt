package com.vidyasetuai.feature_case_study.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.data.local.datasource.CaseStudyLocalDataSource
import com.vidyasetuai.feature_case_study.data.remote.datasource.CaseStudyRemoteDataSource
import com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl
import com.vidyasetuai.feature_case_study.domain.usecase.GetCaseStudyListUseCase
import com.vidyasetuai.feature_case_study.domain.usecase.ToggleBookmarkUseCase
import com.vidyasetuai.feature_case_study.domain.usecase.ToggleReactionUseCase
import com.vidyasetuai.feature_case_study.presentation.component.CaseStudyCard
import com.vidyasetuai.feature_case_study.presentation.event.CaseStudyEvent
import com.vidyasetuai.feature_case_study.presentation.viewmodel.CaseStudyListViewModel

@Composable
fun CaseStudyListScreen(
    userId: String,
    onCaseStudyClick: (String) -> Unit,
    currentLanguage: String,
    onExploreClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel = remember {
        val db = AppDatabase.getDatabase(context)
        val localDS = CaseStudyLocalDataSource(db.caseStudyDao())
        val remoteDS = CaseStudyRemoteDataSource()
        val repo = CaseStudyRepositoryImpl(localDS, remoteDS)
        val listUseCase = GetCaseStudyListUseCase(repo)
        val reactionUseCase = ToggleReactionUseCase(repo)
        val bookmarkUseCase = ToggleBookmarkUseCase(repo)
        CaseStudyListViewModel(listUseCase, reactionUseCase, bookmarkUseCase)
    }

    LaunchedEffect(userId) {
        android.util.Log.d("VidyaSetu_CaseStudyList", "loadCaseStudies called for userId: '$userId'")
        if (userId.isNotEmpty()) {
            viewModel.loadCaseStudies(userId)
        }
    }

    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading && state.caseStudies.isEmpty()) {
            CircularProgressIndicator(
                color = AppColors.EmeraldGreen,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.caseStudies.isEmpty() && !state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (currentLanguage == "hi") "कोई केस स्टडी उपलब्ध नहीं है।" else "No case studies available.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.caseStudies, key = { it.id }) { caseStudy ->
                    CaseStudyCard(
                        caseStudy = caseStudy,
                        onClick = { onCaseStudyClick(caseStudy.id) },
                        onReactionClick = {
                            viewModel.onEvent(CaseStudyEvent.ToggleReaction(caseStudy.id), userId)
                        },
                        onBookmarkClick = {
                            viewModel.onEvent(CaseStudyEvent.ToggleBookmark(caseStudy.id), userId)
                        },
                        currentLanguage = currentLanguage,
                        onExploreClick = { onCaseStudyClick(caseStudy.id) },
                        onAuthorClick = onAuthorClick
                    )
                }
            }
        }
    }
}
