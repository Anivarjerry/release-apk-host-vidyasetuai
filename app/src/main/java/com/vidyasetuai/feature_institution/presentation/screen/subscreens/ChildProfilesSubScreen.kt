package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState

@Composable
fun ChildProfilesSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onChildClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val bgColor = if (isDark) Color(0xFF121212) else Color(0xFFF2F2F7)
    val textColor = if (isDark) Color.White else Color.Black
    val subTextColor = if (isDark) Color(0xFF8E8E93) else Color(0xFF8E8E93)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = bgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {

            val children = state.guardianStudents

            if (children.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Users,
                            contentDescription = null,
                            tint = subTextColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isHindi) "कोई बच्चा लिंक नहीं है" else "No linked children found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = subTextColor
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(children) { child ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChildClick(child.id) },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Lucide.User,
                                            contentDescription = null,
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = child.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = child.className ?: (if (isHindi) "कक्षा उपलब्ध नहीं" else "Class N/A"),
                                            fontSize = 12.sp,
                                            color = subTextColor
                                        )
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Lucide.ChevronRight,
                                        contentDescription = null,
                                        tint = subTextColor,
                                        modifier = Modifier.size(20.dp)
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
