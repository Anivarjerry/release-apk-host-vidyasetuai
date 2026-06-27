package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyasetuai.core.auth.SessionManager
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.presentation.screen.CaseStudyListScreen
import com.vidyasetuai.feature_feed.presentation.viewmodel.ExperienceViewModel

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus

@Composable
fun HomeScreen(
    currentLanguage: String,
    experienceViewModel: ExperienceViewModel,
    onCaseStudyClick: (String) -> Unit,
    onExploreClick: () -> Unit,
    onUploadCaseStudyClick: () -> Unit,
    onUploadExperienceClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId() ?: ""
    val isHindi = currentLanguage == "hi"

    var activeFeedTab by remember { mutableStateOf("case_studies") } // "case_studies" or "experiences"

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Tab Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Case Studies Tab + Upload Button
            val isCaseSelected = activeFeedTab == "case_studies"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { activeFeedTab = "case_studies" }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isHindi) "केस स्टडीज" else "Case Studies",
                    fontSize = 14.sp,
                    fontWeight = if (isCaseSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCaseSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                androidx.compose.material3.IconButton(
                    onClick = onUploadCaseStudyClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = "Upload Case Study",
                        tint = if (isCaseSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Experiences Tab + Upload Button
            val isExpSelected = activeFeedTab == "experiences"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { activeFeedTab = "experiences" }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isHindi) "अनुभव" else "Experiences",
                    fontSize = 14.sp,
                    fontWeight = if (isExpSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isExpSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                androidx.compose.material3.IconButton(
                    onClick = onUploadExperienceClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = "Upload Experience",
                        tint = if (isExpSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        // List View based on selection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (activeFeedTab == "case_studies") {
                CaseStudyListScreen(
                    userId = userId,
                    onCaseStudyClick = onCaseStudyClick,
                    currentLanguage = currentLanguage,
                    onExploreClick = onExploreClick,
                    onAuthorClick = onAuthorClick,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ExperienceListScreen(
                    userId = userId,
                    currentLanguage = currentLanguage,
                    viewModel = experienceViewModel,
                    onAuthorClick = onAuthorClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
