package com.vidyasetuai.feature_feed.presentation.screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors

data class TournamentItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val xpReward: Int,
    val participants: Int,
    val timeInfoEn: String,
    val timeInfoHi: String,
    val status: String, // "ongoing" or "upcoming"
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    // Pre-configured mock quests for interactive preview
    val questsEn: List<String>,
    val questsHi: List<String>,
    val questXps: List<Int>
)

data class LeaderboardMockStudent(
    val rank: Int,
    val name: String,
    val prepEn: String,
    val prepHi: String,
    val achievementEn: String,
    val achievementHi: String,
    val metricEn: String,
    val metricHi: String,
    val isStreak: Boolean
)

@Composable
fun TournamentEvent(
    currentLanguage: String,
    currentTheme: String,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (currentTheme) {
        "dark" -> true
        "light" -> false
        else -> isSystemDark
    }

    var activeTab by remember { mutableStateOf("challenges") } // "challenges" or "leaderboard"
    var selectedTournament by remember { mutableStateOf<TournamentItem?>(null) }
    var notificationSubscribedMap by remember { mutableStateOf(mapOf<String, Boolean>()) }

    val tournaments = listOf(
        TournamentItem(
            id = "1",
            titleEn = "Weekly Coding Arena",
            titleHi = "साप्ताहिक कोडिंग अखाड़ा",
            xpReward = 500,
            participants = 120,
            timeInfoEn = "Ends in 2 days",
            timeInfoHi = "2 दिनों में समाप्त होगा",
            status = "ongoing",
            icon = Lucide.Code,
            questsEn = listOf(
                "Solve 2 Kotlin Loop problems",
                "Complete the conditional statement challenge",
                "1v1 Code Duel with a classmate"
            ),
            questsHi = listOf(
                "कोटलिन में 2 लूप्स के प्रश्न हल करें",
                "कंडीशनल स्टेटमेंट चुनौती पूरी करें",
                "सहपाठी के साथ 1v1 कोड मुकाबला करें"
            ),
            questXps = listOf(150, 200, 150)
        ),
        TournamentItem(
            id = "2",
            titleEn = "National Science Olympiad Prep",
            titleHi = "राष्ट्रीय विज्ञान ओलंपियाड तैयारी",
            xpReward = 1000,
            participants = 340,
            timeInfoEn = "Starts tomorrow at 6 PM",
            timeInfoHi = "कल शाम 6 बजे शुरू होगा",
            status = "upcoming",
            icon = Lucide.Trophy,
            questsEn = listOf(
                "Solve 5 Physics Ray Optics MCQs",
                "Score 90% in daily Chemistry formula test",
                "Solve last year's Olympiad section A"
            ),
            questsHi = listOf(
                "भौतिकी किरण प्रकाशिकी के 5 MCQs हल करें",
                "दैनिक रसायन सूत्र परीक्षण में 90% स्कोर करें",
                "पिछले वर्ष का ओलंपियाड सेक्शन A हल करें"
            ),
            questXps = listOf(300, 400, 300)
        ),
        TournamentItem(
            id = "3",
            titleEn = "Mathematics Speed Challenge",
            titleHi = "गणित गति चुनौती",
            xpReward = 300,
            participants = 95,
            timeInfoEn = "Ends in 6 hours",
            timeInfoHi = "6 घंटे में समाप्त होगा",
            status = "ongoing",
            icon = Lucide.Calculator,
            questsEn = listOf(
                "Complete 10 Algebra calculations under 60s",
                "Answer 5 Quadratic Equations correctly",
                "Beat your high score in Daily Speed Run"
            ),
            questsHi = listOf(
                "60 सेकंड में 10 बीजगणित गणनाएं पूरी करें",
                "5 द्विघात समीकरणों के सही उत्तर दें",
                "दैनिक स्पीड रन में अपना हाई स्कोर तोड़ें"
            ),
            questXps = listOf(100, 100, 100)
        )
    )

    val leaderboardStudents = listOf(
        LeaderboardMockStudent(
            rank = 1,
            name = if (isHindi) "राहुल शर्मा" else "Rahul Sharma",
            prepEn = "Class 12 Boards",
            prepHi = "12वीं बोर्ड परीक्षा",
            achievementEn = "Board Prep Star",
            achievementHi = "बोर्ड टॉपर दावेदार",
            metricEn = "45 Hrs Deep Focus",
            metricHi = "45 घंटे एकाग्रता",
            isStreak = false
        ),
        LeaderboardMockStudent(
            rank = 2,
            name = if (isHindi) "प्रिया पटेल" else "Priya Patel",
            prepEn = "NEET Aspirant",
            prepHi = "NEET की तैयारी",
            achievementEn = "Biology Master",
            achievementHi = "बायोलॉजी मास्टर",
            metricEn = "32-Day Study Streak",
            metricHi = "लगातार 32 दिन",
            isStreak = true
        ),
        LeaderboardMockStudent(
            rank = 3,
            name = if (isHindi) "अमित वर्मा" else "Amit Verma",
            prepEn = "JEE Prep",
            prepHi = "JEE की तैयारी",
            achievementEn = "Physics Solver",
            achievementHi = "फिजिक्स सॉल्वर",
            metricEn = "150 Doubts Solved",
            metricHi = "150 डाउट हल किए",
            isStreak = false
        ),
        LeaderboardMockStudent(
            rank = 4,
            name = if (isHindi) "स्नेहा गुप्ता" else "Sneha Gupta",
            prepEn = "Class 10 Boards",
            prepHi = "10वीं बोर्ड परीक्षा",
            achievementEn = "Maths Wizard",
            achievementHi = "गणित विज़ार्ड",
            metricEn = "25-Day Study Streak",
            metricHi = "लगातार 25 दिन",
            isStreak = true
        ),
        LeaderboardMockStudent(
            rank = 5,
            name = if (isHindi) "विक्रम सिंह" else "Vikram Singh",
            prepEn = "NDA Aspirant",
            prepHi = "NDA की तैयारी",
            achievementEn = "General Ability Pro",
            achievementHi = "जनरल एबिलिटी प्रो",
            metricEn = "85 Practice Sets",
            metricHi = "85 प्रैक्टिस सेट",
            isStreak = false
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Premium Header Row with Glowing "Coming Soon" Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isHindi) "टूर्नामेंट अखाड़ा" else "Tournament Arena",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isHindi) "खेलें, सीखें और आगे बढ़ें!" else "Play, Learn & Level Up!",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(100.dp),
                color = if (isDark) Color(0xFF2C1E0A) else Color(0xFFFEF3C7),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) Color(0xFFD97706).copy(alpha = 0.5f) else Color(0xFFF59E0B)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.Sparkles,
                        contentDescription = null,
                        tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "जल्द आ रहा है" else "Coming Soon",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Premium Segmented Pill Tabs Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(if (isDark) Color(0xFF1E1E20) else Color(0xFFF2F2F7))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (activeTab == "challenges") {
                            if (isDark) AppColors.EmeraldGreen.copy(alpha = 0.2f) else AppColors.EmeraldGreen
                        } else Color.Transparent
                    )
                    .clickable { activeTab = "challenges" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isHindi) "चुनौतियां" else "Challenges",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (activeTab == "challenges") {
                        if (isDark) AppColors.EmeraldGreen else Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (activeTab == "leaderboard") {
                            if (isDark) AppColors.EmeraldGreen.copy(alpha = 0.2f) else AppColors.EmeraldGreen
                        } else Color.Transparent
                    )
                    .clickable { activeTab = "leaderboard" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isHindi) "लीडरबोर्ड प्रीव्यू" else "Leaderboard Preview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (activeTab == "leaderboard") {
                        if (isDark) AppColors.EmeraldGreen else Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeTab == "challenges") {
            // Challenges Tab Layout
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Interactive Vision Banner
                item {
                    val gradientBrush = Brush.verticalGradient(
                        colors = if (isDark) {
                            listOf(Color(0xFF0C241E), Color(0xFF071512))
                        } else {
                            listOf(Color(0xFFE8F8F5), Color(0xFFF1FDF8))
                        }
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            AppColors.EmeraldGreen.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .background(gradientBrush)
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(AppColors.EmeraldGreen.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.Sparkles,
                                        contentDescription = null,
                                        tint = AppColors.EmeraldGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isHindi) "पढ़ाई बनेगी अब खेल! 🎮" else "Study turns into a Game! 🎮",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (isHindi) {
                                    "हर रोज़ अपनी क्लास के सिलेबस से जुड़े क्विज़ और कोडिंग टास्क खेलें, XP कमाएं, मेडल जीतें और परीक्षा में टॉप करें!"
                                } else {
                                    "Solve daily challenges mapped to your actual syllabus, earn XP, unlock achievements, and excel in your exams!"
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Headers
                item {
                    Text(
                        text = if (isHindi) "सक्रिय प्रतियोगिताएं (मॉक)" else "Active Arenas (Mock)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                }

                items(tournaments) { tourney ->
                    TournamentCard(
                        item = tourney,
                        isHindi = isHindi,
                        isDark = isDark,
                        onCardClick = {
                            selectedTournament = tourney
                        }
                    )
                }
            }
        } else {
            // Relatable Indian Student Leaderboard Tab Layout
            Column(modifier = Modifier.fillMaxSize()) {
                // Intro Text
                Text(
                    text = if (isHindi) {
                        "यह लीडरबोर्ड उन उपलब्धियों को ट्रैक करता है जो सच में मायने रखती हैं। लगातार पढ़ाई (Streak) और एकाग्रता से ऊपर पहुँचें!"
                    } else {
                        "This leaderboard rewards real study consistency and focus. Build streaks and deep study hours to climb the top!"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 14.dp),
                    lineHeight = 18.sp
                )

                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(leaderboardStudents) { student ->
                            val isMe = false
                            val itemBg = if (isDark) Color(0xFF1C1C1E) else Color.White
                            val itemBorder = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, itemBorder),
                                color = itemBg
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Rank Medal or Circle
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                color = when (student.rank) {
                                                    1 -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                                    2 -> Color(0xFF94A3B8).copy(alpha = 0.15f)
                                                    3 -> Color(0xFFB45309).copy(alpha = 0.15f)
                                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                                },
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = student.rank.toString(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = when (student.rank) {
                                                1 -> Color(0xFFD97706)
                                                2 -> Color(0xFF475569)
                                                3 -> Color(0xFF9A3412)
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Student Name & Prep Tag
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = student.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            // Prep Target Tag
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = AppColors.EmeraldGreen.copy(alpha = 0.08f),
                                                modifier = Modifier.padding(start = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (isHindi) student.prepHi else student.prepEn,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = AppColors.EmeraldGreen,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = if (isHindi) student.achievementHi else student.achievementEn,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Metric (Streak or Hours)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (student.isStreak) Lucide.Flame else Lucide.Clock,
                                            contentDescription = null,
                                            tint = if (student.isStreak) Color(0xFFF97316) else AppColors.EmeraldGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isHindi) student.metricHi else student.metricEn,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (student.isStreak) Color(0xFFEA580C) else AppColors.EmeraldGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Sticky User Card: "Your Rank: Lock (Coming Soon)"
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        AppColors.EmeraldGreen.copy(alpha = 0.4f)
                    ),
                    color = if (isDark) Color(0xFF0F241F) else Color(0xFFECFDF5)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(AppColors.EmeraldGreen.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Lock,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "आपकी रैंक" else "Your Rank",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = if (isHindi) "प्रतिस्पर्धा शुरू होने पर अनलॉक होगी" else "Unlocks when tournaments go live",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = if (isHindi) "लॉन्च जल्द" else "Locked",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen
                        )
                    }
                }
            }
        }
    }

    // Interactive Preview Modal Dialog (Styled like a modern bottom sheet popup)
    selectedTournament?.let { tourney ->
        Dialog(
            onDismissRequest = { selectedTournament = null }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                )
            ) {
                // Local state to track checkbox clicks and animate the XP progress bar
                var task1Completed by remember { mutableStateOf(false) }
                var task2Completed by remember { mutableStateOf(false) }
                var task3Completed by remember { mutableStateOf(false) }

                val earnedXp = (if (task1Completed) tourney.questXps[0] else 0) +
                               (if (task2Completed) tourney.questXps[1] else 0) +
                               (if (task3Completed) tourney.questXps[2] else 0)

                val progress = earnedXp.toFloat() / tourney.xpReward.toFloat()
                val isSubscribed = notificationSubscribedMap[tourney.id] == true

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Header Row with Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(AppColors.EmeraldGreen.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tourney.icon,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) tourney.titleHi else tourney.titleEn,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = if (isHindi) "पूर्वावलोकन और उद्देश्य" else "Preview & Motive",
                                    fontSize = 11.sp,
                                    color = AppColors.EmeraldGreen
                                )
                            }
                        }

                        IconButton(
                            onClick = { selectedTournament = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Gamification Explanation / Vision
                    Text(
                        text = if (isHindi) "हमारा विज़न (Our Motive):" else "Our Motive:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) {
                            "इस फीचर का लक्ष्य आपकी बोर्ड और प्रतियोगी परीक्षाओं की तैयारी को एक गेम की तरह मजेदार बनाना है। सिलेबस के दैनिक छोटे-छोटे टास्क्स पूरे करके आप हर हफ्ते लीडरबोर्ड में भाग ले पाएंगे।"
                        } else {
                            "Our goal is to make your board and exam preparation fun like a game. By completing small syllabus-aligned daily tasks, you can earn medals and compete in weekly challenges."
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Interactive Preview Section
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0xFF121214) else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF232325) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isHindi) {
                                    "टास्क पूरा करने का अनुभव लें (टैप करें):"
                                } else {
                                    "Try completing quests (Tap to test):"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Interactive Quests Checkboxes
                            InteractiveQuestRow(
                                title = if (isHindi) tourney.questsHi[0] else tourney.questsEn[0],
                                xp = tourney.questXps[0],
                                isCompleted = task1Completed,
                                isDark = isDark,
                                onCheckedChange = { task1Completed = it }
                            )

                            InteractiveQuestRow(
                                title = if (isHindi) tourney.questsHi[1] else tourney.questsEn[1],
                                xp = tourney.questXps[1],
                                isCompleted = task2Completed,
                                isDark = isDark,
                                onCheckedChange = { task2Completed = it }
                            )

                            InteractiveQuestRow(
                                title = if (isHindi) tourney.questsHi[2] else tourney.questsEn[2],
                                xp = tourney.questXps[2],
                                isCompleted = task3Completed,
                                isDark = isDark,
                                onCheckedChange = { task3Completed = it }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive Progress indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHindi) "प्रगति: $earnedXp / ${tourney.xpReward} XP" else "Progress: $earnedXp / ${tourney.xpReward} XP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.EmeraldGreen
                                )

                                if (progress >= 1f) {
                                    Text(
                                        text = if (isHindi) "चुनौती पूरी! 🎉" else "Completed! 🎉",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEAB308)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = if (progress >= 1f) Color(0xFFEAB308) else AppColors.EmeraldGreen,
                                trackColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE2E8F0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Notify Me or Subscribed state
                    if (isSubscribed) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0xFF0F2C24) else Color(0xFFECFDF5),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp,
                                AppColors.EmeraldGreen.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) {
                                        "सूचित किया जाएगा! लॉन्च होने पर अलर्ट मिलेगा।"
                                    } else {
                                        "You're registered! Alert will be sent on launch."
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.EmeraldGreen
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                val updatedMap = notificationSubscribedMap.toMutableMap()
                                updatedMap[tourney.id] = true
                                notificationSubscribedMap = updatedMap
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.EmeraldGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Bell,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "मुझे लॉन्च होने पर सूचित करें" else "Notify Me on Launch",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveQuestRow(
    title: String,
    xp: Int,
    isCompleted: Boolean,
    isDark: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isCompleted) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCompleted) Lucide.Check else Lucide.Circle,
            contentDescription = null,
            tint = if (isCompleted) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "+$xp XP",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCompleted) AppColors.EmeraldGreen.copy(alpha = 0.6f) else AppColors.EmeraldGreen
        )
    }
}

@Composable
fun TournamentCard(
    item: TournamentItem,
    isHindi: Boolean,
    isDark: Boolean,
    onCardClick: () -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onCardClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (item.status == "ongoing") AppColors.EmeraldGreen.copy(alpha = 0.12f) else Color(0xFF8E8E93).copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = if (item.status == "ongoing") AppColors.EmeraldGreen else Color(0xFF8E8E93),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title and time
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isHindi) item.titleHi else item.titleEn,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isHindi) item.timeInfoHi else item.timeInfoEn,
                    fontSize = 12.sp,
                    color = if (item.status == "ongoing") AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Reward XP badge
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = if (item.status == "ongoing") Color(0xFFE8F8F5) else Color(0xFFF2F2F7),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = "+${item.xpReward} XP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.status == "ongoing") AppColors.EmeraldGreen else Color(0xFF8E8E93),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Participants & action row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Lucide.User,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isHindi) "${item.participants} छात्र जुड़े" else "${item.participants} joined",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { onCardClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item.status == "ongoing") AppColors.EmeraldGreen else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (item.status == "ongoing") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text(
                    text = if (item.status == "ongoing") {
                        if (isHindi) "अभी खेलें" else "Play Now"
                    } else {
                        if (isHindi) "पंजीकरण करें" else "Register"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
