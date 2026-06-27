package com.vidyasetuai.feature_journey.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.vidyasetuai.feature_journey.domain.model.JourneyOwnerType
import com.vidyasetuai.feature_journey.domain.model.JourneyTemplate
import com.vidyasetuai.feature_journey.domain.model.JourneyTask
import com.vidyasetuai.feature_journey.domain.model.JourneyMcq
import com.vidyasetuai.feature_journey.domain.model.UserJourney
import com.vidyasetuai.feature_journey.domain.model.LeaderboardEntry
import com.vidyasetuai.feature_journey.presentation.event.JourneyEvent
import com.vidyasetuai.feature_journey.presentation.state.JourneyUiState
import com.vidyasetuai.feature_journey.presentation.viewmodel.JourneyViewModel
import com.vidyasetuai.core.ui.colors.AppColors
import kotlinx.coroutines.delay

@Composable
fun JourneyScreen(
    viewModel: JourneyViewModel,
    currentLanguage: String,
    currentTheme: String,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isHindi = currentLanguage == "hi"
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (currentTheme) {
        "dark" -> true
        "light" -> false
        else -> isSystemDark
    }

    // Local state to track whether the user is browsing templates to start a journey
    var isBrowsingTemplates by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AppColors.EmeraldGreen
            )
        } else if (isBrowsingTemplates) {
            // Screen 2: Template Selection Screen (Shown when enrolling in a journey)
            TemplateSelectionScreen(
                templates = uiState.templates,
                isHindi = isHindi,
                isDark = isDark,
                onBack = { isBrowsingTemplates = false },
                onEnroll = { templateId, ownerType, time ->
                    viewModel.onEvent(
                        JourneyEvent.EnrollInJourney(
                            context = context,
                            templateId = templateId,
                            ownerType = ownerType,
                            notificationTime = time
                        )
                    )
                    isBrowsingTemplates = false
                }
            )
        } else if (uiState.activeJourney == null) {
            // Screen 1: Welcome Landing Screen (Shown when no active journey exists)
            WelcomeLandingScreen(
                isHindi = isHindi,
                isDark = isDark,
                onStartClick = { isBrowsingTemplates = true }
            )
        } else {
            // Screen 3: Active Journey Dashboard
            ActiveJourneyDashboard(
                uiState = uiState,
                isHindi = isHindi,
                isDark = isDark,
                onStartNewJourney = { isBrowsingTemplates = true },
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
fun WelcomeLandingScreen(
    isHindi: Boolean,
    isDark: Boolean,
    onStartClick: () -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1A1A1A) else Color(0xFFF4F9F6)
    val cardBorder = if (isDark) Color(0xFF2C2C2E) else Color(0xFFC8E6C9)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🌱",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = if (isHindi) "अपनी यात्रा शुरू करें" else "Start Your Journey",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isHindi) "अपने व्यक्तिगत सीखने के पथ को शुरू करने के लिए एक टेम्पलेट चुनें।" else "Choose a template to begin your personalized learning path.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = if (isHindi) "शुरू करें" else "Start",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TemplateSelectionScreen(
    templates: List<JourneyTemplate>,
    isHindi: Boolean,
    isDark: Boolean,
    onBack: () -> Unit,
    onEnroll: (String, JourneyOwnerType, String) -> Unit
) {
    var selectedTemplate by remember { mutableStateOf<JourneyTemplate?>(null) }
    var notificationTime by remember { mutableStateOf("20:00") }
    val times = listOf("18:00", "19:00", "20:00", "21:00", "22:00")
    val dark = isDark

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "अपनी जर्नी चुनें" else "Select Your Journey",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(templates) { template ->
                val isSelected = selectedTemplate?.id == template.id
                val borderCol = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.outlineVariant
                val bgCol = if (isSelected) {
                    if (dark) Color(0xFF1E2F28) else Color(0xFFF4F6F5)
                } else {
                    MaterialTheme.colorScheme.surface
                }
                val badgeBg = if (dark) Color(0xFF2C2C2C) else Color(0xFFF2F2F7)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            border = BorderStroke(width = 1.dp, color = borderCol),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = bgCol,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedTemplate = template }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = template.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .background(badgeBg, shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "${template.durationDays} दिन" else "${template.durationDays} Days",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = template.description ?: (if (isHindi) "कोई विवरण उपलब्ध नहीं है।" else "No description available."),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = template.ownerType.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.EmeraldGreen
                                )
                            }
                            if (!template.category.isNullOrEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .background(badgeBg, shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = template.category,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedTemplate != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isHindi) "एमसीक्यू नोटिफिकेशन का समय सेट करें" else "Set MCQ Notification Time",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    times.forEach { time ->
                        val isTimeSelected = notificationTime == time
                        val badgeBg = if (isTimeSelected) AppColors.EmeraldGreen else (if (dark) Color(0xFF2C2C2C) else Color(0xFFF2F2F7))
                        val badgeTextCol = if (isTimeSelected) Color.White else MaterialTheme.colorScheme.onBackground
                        Box(
                            modifier = Modifier
                                .background(
                                    color = badgeBg,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { notificationTime = time }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = time,
                                fontSize = 12.sp,
                                color = badgeTextCol,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val template = selectedTemplate!!
                        onEnroll(template.id, template.ownerType, notificationTime)
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(42.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    shape = RoundedCornerShape(21.dp)
                ) {
                    Text(
                        text = if (isHindi) "जर्नी शुरू करें" else "Start Journey", 
                        color = Color.White, 
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveJourneyDashboard(
    uiState: JourneyUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onStartNewJourney: () -> Unit,
    onEvent: (JourneyEvent) -> Unit
) {
    var activeSubTab by remember { mutableStateOf("tasks") }
    val journey = uiState.activeJourney ?: return
    val dark = isDark
    val dropBg = if (dark) Color(0xFF1E1E1E) else Color(0xFFF2F2F7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Dropdown Selector & start new journey button row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var dropdownExpanded by remember { mutableStateOf(false) }
            val currentTemplateTitle = uiState.templates.find { it.id == journey.templateId }?.title ?: "Journey"

            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .background(dropBg, shape = RoundedCornerShape(8.dp))
                        .clickable { dropdownExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentTemplateTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "▼",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    uiState.activeJourneys.forEach { active ->
                        val templateTitle = uiState.templates.find { it.id == active.templateId }?.title ?: "Journey"
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (isHindi) "$templateTitle (दिन ${active.currentDay})" else "$templateTitle (Day ${active.currentDay})",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                dropdownExpanded = false
                                onEvent(JourneyEvent.SelectActiveJourney(active.id))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = onStartNewJourney,
                modifier = Modifier
                    .size(36.dp)
                    .background(AppColors.EmeraldGreen, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = "Start New Journey",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Upper stats banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isHindi) "दिन (Day) ${journey.currentDay}" else "Day ${journey.currentDay}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isHindi) "लकीर: 🔥 ${journey.currentStreak} दिन" else "Streak: 🔥 ${journey.currentStreak} days",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier = Modifier
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isHindi) "स्कोर: ${journey.totalScore} pts" else "Score: ${journey.totalScore} pts",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(dropBg, shape = RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            val tabs = listOf(
                "tasks" to (if (isHindi) "दैनिक कार्य" else "Daily Tasks"), 
                "practice" to (if (isHindi) "एमसीक्यू" else "MCQs"), 
                "leaderboard" to (if (isHindi) "लीडरबोर्ड" else "Leaderboard")
            )
            tabs.forEach { (key, label) ->
                val isSelected = activeSubTab == key
                val bgCol = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = bgCol,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { activeSubTab = key }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Screen Body depending on sub tab
        when (activeSubTab) {
            "tasks" -> TaskTabScreen(
                tasks = uiState.dailyTasks,
                progress = uiState.dailyTaskProgress,
                analytics = uiState.analytics,
                isHindi = isHindi,
                isDark = dark,
                onToggle = { taskId, completed ->
                    onEvent(JourneyEvent.ToggleTaskCompletion(taskId, completed))
                }
            )
            "practice" -> PracticeTabScreen(
                mcqs = uiState.dailyMcqs,
                answers = uiState.dailyMcqAnswers,
                corrections = uiState.dailyMcqCorrections,
                notificationTime = journey.notificationTime ?: "20:00",
                isHindi = isHindi,
                isDark = dark,
                onSubmitAll = { answers ->
                    onEvent(JourneyEvent.SubmitAllMCQOptions(answers))
                }
            )
            "leaderboard" -> LeaderboardTabScreen(
                leaderboard = uiState.leaderboard,
                isHindi = isHindi,
                isDark = dark
            )
        }
    }
}

@Composable
fun TaskTabScreen(
    tasks: List<JourneyTask>,
    progress: Map<String, Boolean>,
    analytics: com.vidyasetuai.feature_journey.domain.usecase.JourneyDayAnalytics?,
    isHindi: Boolean,
    isDark: Boolean,
    onToggle: (String, Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (analytics != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isHindi) "आज की प्रगति: ${analytics.taskCompletionPercentage}%" else "Today's Progress: ${analytics.taskCompletionPercentage}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isHindi) "${analytics.completedTasksCount}/${analytics.totalTasksCount} कार्य पूरे" else "${analytics.completedTasksCount}/${analytics.totalTasksCount} tasks completed",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            LinearProgressIndicator(
                progress = analytics.taskCompletionPercentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = AppColors.EmeraldGreen,
                trackColor = if (isDark) Color(0xFF222222) else Color(0xFFF2F2F7)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                val isDone = progress[task.id] ?: false
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDone,
                        onCheckedChange = { onToggle(task.id, it) },
                        colors = CheckboxDefaults.colors(checkedColor = AppColors.EmeraldGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = task.taskTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (!task.taskDescription.isNullOrEmpty()) {
                            Text(
                                text = task.taskDescription,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PracticeTabScreen(
    mcqs: List<JourneyMcq>,
    answers: Map<String, String>,
    corrections: Map<String, Boolean>,
    notificationTime: String,
    isHindi: Boolean,
    isDark: Boolean,
    onSubmitAll: (Map<String, String>) -> Unit
) {
    val dark = isDark
    if (mcqs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = if (isHindi) "आज कोई एमसीक्यू उपलब्ध नहीं है।" else "No MCQs available today.", 
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        return
    }

    val isLocked = answers.isEmpty() && isTimeBeforeNotification(notificationTime)

    if (isLocked) {
        val lockCardBg = if (dark) Color(0xFF161618) else Color(0xFFFAFBFB)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = lockCardBg),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔒",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = if (isHindi) "एमसीक्यू अभी लॉक है" else "MCQ is currently locked",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi) "यह आज रात $notificationTime बजे खुलेगा।" else "This will open tonight at $notificationTime.",
                        fontSize = 14.sp,
                        color = AppColors.EmeraldGreen,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isHindi) "कृपया तब तक अपने दैनिक कार्यों (Dainik Karya) को पूरा करें और आज के विषय का अच्छी तरह अभ्यास करें।" else "Please complete your daily tasks (Dainik Karya) and practice today's topic well until then.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        return
    }

    if (answers.isEmpty()) {
        var isQuizStarted by rememberSaveable { mutableStateOf(false) }
        var remainingSeconds by rememberSaveable { mutableStateOf(1200) }
        var tempAnswers by rememberSaveable { mutableStateOf(emptyMap<String, String>()) }

        val submitQuizAction = {
            val finalAnswers = mcqs.associate { mcq ->
                mcq.id to (tempAnswers[mcq.id] ?: "")
            }
            onSubmitAll(finalAnswers)
        }

        LaunchedEffect(key1 = isQuizStarted) {
            if (isQuizStarted) {
                while (remainingSeconds > 0) {
                    delay(1000)
                    remainingSeconds--
                }
                submitQuizAction()
            }
        }

        if (!isQuizStarted) {
            val startCardBg = if (dark) Color(0xFF11221A) else Color(0xFFF4F9F6)
            val startCardBorder = if (dark) Color(0xFF1B4D3E) else Color(0xFFC8E6C9)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = startCardBg),
                    border = BorderStroke(1.dp, startCardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHindi) "📝 आज का एमसीक्यू टेस्ट" else "📝 Today's MCQ Test",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("❓", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "कुल प्रश्न: ${mcqs.size}" else "Total Questions: ${mcqs.size}", 
                                    fontSize = 13.sp, 
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⏱️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "समय सीमा: 20 मिनट" else "Time Limit: 20 minutes", 
                                    fontSize = 13.sp, 
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏆", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "सही उत्तर पर +1 पॉइंट मिलेगा।" else "Correct answer earns +1 point.", 
                                    fontSize = 13.sp, 
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { isQuizStarted = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = if (isHindi) "टेस्‍ट शुरू करें" else "Start Test", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            val timerText = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
            val isTimerLow = remainingSeconds < 180
            val timerBg = if (isTimerLow) Color(0xFFFFEBEE) else (if (dark) Color(0xFF1B2F23) else Color(0xFFE8F5E9))
            val timerBorderCol = if (isTimerLow) Color.Red else AppColors.EmeraldGreen

            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = timerBg,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = timerBorderCol,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "⏱️ शेष समय:" else "⏱️ Time Remaining:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isTimerLow) Color.Red else (if (dark) Color.White else Color.Black)
                    )
                    Text(
                        text = timerText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isTimerLow) Color.Red else AppColors.EmeraldGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(mcqs) { mcq ->
                        val selectedOption = tempAnswers[mcq.id]
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = mcq.questionText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val options = listOf("A" to mcq.optionA, "B" to mcq.optionB, "C" to mcq.optionC, "D" to mcq.optionD)
                            options.forEach { (key, optionText) ->
                                val isSelected = selectedOption == key
                                val optBg = if (isSelected) {
                                    if (dark) Color(0xFF1B3D2A) else Color(0xFFE8F5E9)
                                } else {
                                    if (dark) Color(0xFF161618) else Color(0xFFF2F2F7)
                                }
                                val optBorderCol = if (isSelected) AppColors.EmeraldGreen else Color.Transparent

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(
                                            BorderStroke(width = 1.dp, color = optBorderCol),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            color = optBg,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            tempAnswers = tempAnswers + (mcq.id to key)
                                        }
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "$key. $optionText",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        Button(
                            onClick = { submitQuizAction() },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isHindi) "टेस्ट सबमिट करें" else "Submit Test", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mcqs) { mcq ->
                val selectedOption = answers[mcq.id]
                val isCorrect = corrections[mcq.id]
                val correctOption = mcq.correctOption

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = mcq.questionText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val options = listOf("A" to mcq.optionA, "B" to mcq.optionB, "C" to mcq.optionC, "D" to mcq.optionD)
                    options.forEach { (key, optionText) ->
                        val isSelected = selectedOption == key
                        val isCorrectOption = correctOption == key
                        
                        val optionBgColor = when {
                            isSelected && isCorrect == true -> if (dark) Color(0xFF1B3D2A) else Color(0xFFE8F5E9)
                            isSelected && isCorrect == false -> if (dark) Color(0xFF4C1C1C) else Color(0xFFFFEBEE)
                            isCorrectOption -> if (dark) Color(0xFF1B3D2A) else Color(0xFFE8F5E9)
                            else -> if (dark) Color(0xFF161618) else Color(0xFFF2F2F7)
                        }
                        val optionBorderColor = when {
                            isSelected && isCorrect == true -> AppColors.EmeraldGreen
                            isSelected && isCorrect == false -> Color.Red
                            isCorrectOption -> AppColors.EmeraldGreen
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(BorderStroke(1.dp, optionBorderColor), RoundedCornerShape(8.dp))
                                .background(optionBgColor, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$key. $optionText",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Text(
                                        text = if (isCorrect == true) {
                                            if (isHindi) "✅ सही" else "✅ Correct"
                                        } else {
                                            if (isHindi) "❌ आपका उत्तर" else "❌ Your Answer"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorrect == true) AppColors.EmeraldGreen else Color.Red
                                    )
                                } else if (isCorrectOption) {
                                    Text(
                                        text = if (isHindi) "✅ सही उत्तर" else "✅ Correct Answer",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen
                                    )
                                }
                            }
                        }
                    }

                    if (!mcq.explanation.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHindi) "स्पष्टीकरण (Explanation): ${mcq.explanation}" else "Explanation: ${mcq.explanation}",
                            fontSize = 12.sp,
                            color = AppColors.EmeraldGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun isTimeBeforeNotification(notificationTime: String): Boolean {
    return try {
        val calendar = java.util.Calendar.getInstance()
        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(java.util.Calendar.MINUTE)
        
        val parts = notificationTime.split(":")
        val targetHour = parts.getOrNull(0)?.toIntOrNull() ?: 20
        val targetMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        
        (currentHour < targetHour) || (currentHour == targetHour && currentMinute < targetMinute)
    } catch (e: Exception) {
        false
    }
}

@Composable
fun LeaderboardTabScreen(
    leaderboard: List<LeaderboardEntry>,
    isHindi: Boolean,
    isDark: Boolean
) {
    val dark = isDark
    if (leaderboard.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = if (isHindi) "लीडरबोर्ड अभी खाली है।" else "Leaderboard is currently empty.", 
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        return
    }

    val entryBg = if (dark) Color(0xFF1C1C1E) else Color(0xFFF4F6F5)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(leaderboard.size) { index ->
            val entry = leaderboard[index]
            val rank = index + 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank Pill
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = when (rank) {
                                1 -> Color(0xFFFFD700) // Gold
                                2 -> Color(0xFFC0C0C0) // Silver
                                3 -> Color(0xFFCD7F32) // Bronze
                                else -> if (dark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // User Initials Avatar
                val initials = if (!entry.fullName.isNullOrEmpty() && entry.fullName.length >= 2) {
                    entry.fullName.substring(0, 2).uppercase()
                } else if (!entry.username.isNullOrEmpty() && entry.username.length >= 2) {
                    entry.username.substring(0, 2).uppercase()
                } else {
                    "VS"
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(entryBg, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.fullName ?: entry.username ?: (if (isHindi) "विद्यार्थी" else "Student"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isHindi) "लकीर: 🔥 ${entry.currentStreak} दिन" else "Streak: 🔥 ${entry.currentStreak} days",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                Text(
                    text = "${entry.totalScore} pts",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen
                )
            }
        }
    }
}
