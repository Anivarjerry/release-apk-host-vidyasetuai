package com.vidyasetuai.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bus
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Cloud
import com.composables.icons.lucide.Coins
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Languages
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Megaphone
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.School
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.Sun
import com.composables.icons.lucide.Users
import com.vidyasetuai.R
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.domain.model.Workspace

private data class DrawerNavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun DashboardDrawerContent(
    activeTab: String,
    isHindi: Boolean,
    workspacesList: List<Workspace> = emptyList(),
    activeWorkspace: Workspace? = null,
    onSwitchWorkspace: (Workspace) -> Unit = {},
    onOpenSyncCenter: (() -> Unit)? = null,
    onNavigateToSubScreen: ((String) -> Unit)? = null,
    totalUnsyncedCount: Int = 0,
    onNavigateToSettings: (String?) -> Unit = {},
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isWorkspaceListExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .width(285.dp)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = 16.dp)
        ) {
            // Header: App Logo & Brand Name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bridge_logo),
                        contentDescription = "Logo",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "VidyaSetu AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "स्मार्ट लर्निंग प्लेटफ़ॉर्म" else "Smart Learning Platform",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable Workspace Card (Visible in Institute Tab when Workspaces are available)
            if (activeTab == "institute" && workspacesList.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    val currentSpace = activeWorkspace ?: workspacesList.firstOrNull()

                    // Active Workspace Header Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppColors.EmeraldGreen.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                color = AppColors.EmeraldGreen.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { isWorkspaceListExpanded = !isWorkspaceListExpanded }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "सक्रिय कार्यक्षेत्र (Workspace)" else "Active Workspace",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.EmeraldGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentSpace?.parentOrgName ?: "VidyaSetu",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentSpace?.role?.uppercase() ?: "STUDENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = if (isWorkspaceListExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                            contentDescription = "Expand Workspaces",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Expandable List of All Workspaces
                    AnimatedVisibility(
                        visible = isWorkspaceListExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isHindi) "कार्यक्षेत्र बदलें (Switch Workspace)" else "Switch Workspace",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )

                            workspacesList.forEach { space ->
                                val isActive = space.id == currentSpace?.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isActive) AppColors.EmeraldGreen.copy(alpha = 0.14f)
                                            else Color.Transparent
                                        )
                                        .clickable {
                                            isWorkspaceListExpanded = false
                                            onSwitchWorkspace(space)
                                            onCloseDrawer()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = space.parentOrgName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isActive) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = space.role.uppercase(),
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (isActive) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(AppColors.EmeraldGreen)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // In-Drawer Sync Center Row for Institute Tab
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onCloseDrawer()
                                onOpenSyncCenter?.invoke()
                            }
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(
                                    imageVector = Lucide.Cloud,
                                    contentDescription = "Sync Center",
                                    tint = if (totalUnsyncedCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                if (totalUnsyncedCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .offset(x = 2.dp, y = (-2).dp)
                                            .background(Color.Red, CircleShape)
                                            .size(7.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (isHindi) "ऑफ़लाइन डेटा सिंक (Sync Center)" else "Sync Center (Offline Status)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (totalUnsyncedCount > 0) {
                            Text(
                                text = "$totalUnsyncedCount pending",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Navigation Items List based on Active Role or Tab
            val role = activeWorkspace?.role ?: "Student"
            val context = LocalContext.current
            val isStaff = role != "Guardian" && role != "Student"
            val isAdmin = role in listOf("Admin", "Principal", "Director", "Owner", "System Administrator", "School Administrator", "Org Admin", "SYSTEM ADMINISTRATOR", "SCHOOL ADMINISTRATOR", "ORG ADMIN")

            val navItems = remember(role, isHindi, activeTab) {
                if (activeTab == "institute") {
                    buildList {
                        val studentLabel = when (role) {
                            "Guardian" -> if (isHindi) "बच्चा (Child)" else "Child"
                            "Student" -> if (isHindi) "स्वयं (Profile)" else "Self Profile"
                            else -> if (isHindi) "छात्र सूची (Students)" else "Students Directory"
                        }
                        add(DrawerNavItem(studentLabel, Lucide.Users, "student_directory"))
                        if (isAdmin) {
                            add(DrawerNavItem(if (isHindi) "कर्मचारी निर्देशिका (Staff)" else "Staff Directory", Lucide.Users, "staff_directory"))
                        }
                        add(DrawerNavItem(if (isHindi) "छुट्टियाँ (Leaves)" else "Leaves", Lucide.Calendar, "leave"))
                        add(DrawerNavItem(if (isHindi) "टिप्पणियाँ (Remarks)" else "Remarks", Lucide.MessageCircle, "remarks_show"))
                        if (isAdmin || !isStaff) {
                            add(DrawerNavItem(if (isHindi) "फीस (Fees)" else "Fees", Lucide.CreditCard, "fees"))
                        }
                        if (isStaff) {
                            add(DrawerNavItem(if (isHindi) "सैलरी (Salary)" else "Salary Payouts", Lucide.Coins, "salary_payouts"))
                        }
                        if (role != "Driver") {
                            add(DrawerNavItem(if (isHindi) "परिवहन (Transport)" else "Transport / Bus", Lucide.Bus, "transport"))
                        }
                    }
                } else {
                    buildList {
                        add(DrawerNavItem(if (isHindi) "ऐप लॉक सेटिंग्स (App Lock)" else "App Lock Settings", Lucide.Lock, "action_app_lock"))
                        add(DrawerNavItem(if (isHindi) "थीम बदलें (Choose Theme)" else "Choose Theme", Lucide.Sun, "action_theme"))
                        add(DrawerNavItem(if (isHindi) "भाषा चुनें (Choose Language)" else "Choose Language", Lucide.Languages, "action_language"))
                        add(DrawerNavItem(if (isHindi) "ऐप शेयर करें (Share App)" else "Share App", Lucide.ExternalLink, "action_share"))
                        add(DrawerNavItem(if (isHindi) "मदद व सहायता (Help & Support)" else "Help & Support", Lucide.Info, "action_help"))
                        add(DrawerNavItem(if (isHindi) "कीड़ा रिपोर्ट / फीडबैक" else "Report Bug / Feedback", Lucide.MessageCircle, "action_report"))
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (navItems.isNotEmpty()) {
                    item {
                        Text(
                            text = if (activeTab == "institute") {
                                if (isHindi) "संस्थान सेवाएँ (Services)" else "Institute Services"
                            } else {
                                if (isHindi) "क्विक सेटिंग्स व सहायता" else "Quick Settings & Support"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    items(navItems) { item ->
                        DrawerMenuItem(
                            icon = item.icon,
                            title = item.title,
                            onClick = {
                                onCloseDrawer()
                                when (item.route) {
                                    "action_app_lock" -> onNavigateToSettings("app_lock")
                                    "action_theme" -> onNavigateToSettings("theme")
                                    "action_language" -> onNavigateToSettings("language")
                                    "action_help" -> onNavigateToSettings("help")
                                    "action_share" -> {
                                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_TEXT, "Hey! Check out the VidyaSetu AI app: https://vidyasetuai.com")
                                        }
                                        context.startActivity(android.content.Intent.createChooser(shareIntent, if (isHindi) "ऐप साझा करें" else "Share App via"))
                                    }
                                    "action_report" -> {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://chat.whatsapp.com/KingWyOnciSFOVUTE88j1Z"))
                                        context.startActivity(intent)
                                    }
                                    else -> onNavigateToSubScreen?.invoke(item.route)
                                }
                            }
                        )
                    }
                }
            }

            // Divider before Bottom Settings Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom-most Row: Settings Item
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onCloseDrawer()
                        onNavigateToSettings(null)
                    }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Settings,
                        contentDescription = "Settings",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isHindi) "सेटिंग्स" else "Settings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
