package com.vidyasetuai.feature_store.presentation.sheet

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.presentation.drawer.StoreWorkspaceItem
import com.vidyasetuai.feature_store.presentation.navigation.StoreRole
import kotlinx.coroutines.launch

/**
 * Flagship Apple Floating Island Store & Workspace Role Switcher Sheet.
 * Strictly adheres to MOBILE_BOTTOM_SHEET_DESIGN_SYSTEM.md:
 * - 1:1 Parallel Synchronized Background Un-Blur Motion (Zero Blur Lag)
 * - Edge-to-Edge System Bar Tint Controller (White icons on dark scrim)
 * - Apple Micro-Scale (0.96x -> 1.00x) Soft Landing & Dismissal
 * - Safe Floating Island Padding (bottom = 44.dp for Android 3-button nav bar)
 * - Reads Room Database Flows with Zero Logic Breakage.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreProfileHubSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onStartDismiss: (() -> Unit)? = null,
    isHindi: Boolean = false,
    selectedRoleOverride: StoreRole? = null,
    selectedStaffRoleOverride: String? = null,
    onSelectRole: (StoreRole, String?) -> Unit = { _, _ -> },
    onRegisterNewBusiness: () -> Unit = {},
    onNavigateToPos: () -> Unit = {},
    onNavigateToKds: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToParties: () -> Unit = {},
    onNavigateToStaff: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToActiveOrders: () -> Unit = {},
    onNavigateToPastOrders: () -> Unit = {}
) {
    if (!visible) return

    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val storeDb = remember { StoreDatabase.getDatabase(context) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // --- Live Room Database Observers (Zero query change) ---
    val activeBusinessState by storeDb.businessDao().getAnyActiveBusinessFlow().collectAsState(initial = null)
    val businessId = activeBusinessState?.id ?: ""
    val staffList by storeDb.businessStaffDao().getStaffFlow(businessId).collectAsState(initial = emptyList())

    val workspaceList = remember(activeBusinessState, staffList, selectedRoleOverride, selectedStaffRoleOverride) {
        val list = mutableListOf<StoreWorkspaceItem>()

        if (activeBusinessState != null) {
            val tradeName = activeBusinessState?.tradeName ?: "Store"

            // 1. Master Store Owner Role Profile
            val isOwnerSelected = selectedRoleOverride == StoreRole.BUSINESS_OWNER ||
                    (selectedRoleOverride == null && activeBusinessState != null)
            list.add(
                StoreWorkspaceItem(
                    id = "owner_${activeBusinessState?.id}",
                    businessName = tradeName,
                    roleName = if (isHindi) "स्टोर मालिक (Store Owner)" else "STORE OWNER",
                    roleEnum = StoreRole.BUSINESS_OWNER,
                    staffRole = "OWNER",
                    branchName = if (isHindi) "सभी शाखाएं (All Branches)" else "All Branches",
                    isSelected = isOwnerSelected
                )
            )

            // 2. Store Staff Member Role Profiles
            staffList.forEach { staff ->
                val isStaffSelected = selectedRoleOverride == StoreRole.STORE_STAFF &&
                        selectedStaffRoleOverride == staff.role.uppercase()
                list.add(
                    StoreWorkspaceItem(
                        id = "staff_${staff.id}",
                        businessName = tradeName,
                        roleName = "STAFF (${staff.role.uppercase()})",
                        roleEnum = StoreRole.STORE_STAFF,
                        staffRole = staff.role.uppercase(),
                        staffBranchId = staff.branchId,
                        branchName = staff.name,
                        isSelected = isStaffSelected
                    )
                )
            }
        }

        // 3. Fallback / Switchable Public Shopper Role Profile
        val isShopperSelected = selectedRoleOverride == StoreRole.PUBLIC_CUSTOMER ||
                (selectedRoleOverride == null && activeBusinessState == null)
        list.add(
            StoreWorkspaceItem(
                id = "public_shopper",
                businessName = if (isHindi) "विद्यासेतु स्टोर मार्केटप्लेस" else "VidyaSetu Store Marketplace",
                roleName = if (isHindi) "ग्राहक (Public Shopper)" else "PUBLIC SHOPPER",
                roleEnum = StoreRole.PUBLIC_CUSTOMER,
                staffRole = "PUBLIC_CUSTOMER",
                isSelected = isShopperSelected
            )
        )
        list
    }

    val selectedWorkspace = remember(workspaceList) {
        workspaceList.firstOrNull { it.isSelected } ?: workspaceList.firstOrNull()
    }

    // Synchronized 1:1 Parallel Dismissal Motion
    var isDismissing by remember { mutableStateOf(false) }
    val performSmoothDismiss: (afterDismiss: (() -> Unit)?) -> Unit = { callback ->
        if (!isDismissing) {
            isDismissing = true
            onStartDismiss?.invoke()
            coroutineScope.launch {
                try {
                    sheetState.hide()
                } finally {
                    onDismiss()
                    callback?.invoke()
                }
            }
        }
    }

    // Rule 5: Layered BackHandler Navigation
    BackHandler(enabled = visible) {
        performSmoothDismiss(null)
    }

    // Rule 3: Edge-to-Edge System Bar Tint Controller
    val view = LocalView.current
    DisposableEffect(visible) {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            val originalLightStatus = insetsController.isAppearanceLightStatusBars
            val originalLightNav = insetsController.isAppearanceLightNavigationBars

            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false

            onDispose {
                insetsController.isAppearanceLightStatusBars = originalLightStatus
                insetsController.isAppearanceLightNavigationBars = originalLightNav
            }
        } else {
            onDispose { }
        }
    }

    // Apple Micro-Scale (0.96x -> 1.00x) Soft Landing
    var isLandingComplete by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isLandingComplete = true
    }

    val contentScale by animateFloatAsState(
        targetValue = if (isLandingComplete && !isDismissing) 1.00f else 0.96f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "sheet_scale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (isLandingComplete && !isDismissing) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "sheet_alpha"
    )

    // Palette & Tokens
    val cardBgColor = if (isDark) Color(0xFF131B2E) else Color.White
    val cardBorder = BorderStroke(
        width = 1.dp,
        color = if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFE2E8F0)
    )
    val primaryTextColor = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    ModalBottomSheet(
        onDismissRequest = { performSmoothDismiss(null) },
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color.Black.copy(alpha = 0.50f),
        dragHandle = null,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 44.dp)
                .graphicsLayer {
                    scaleX = contentScale
                    scaleY = contentScale
                    alpha = contentAlpha
                },
            shape = RoundedCornerShape(28.dp),
            color = cardBgColor,
            border = cardBorder,
            shadowElevation = if (isDark) 16.dp else 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                // 1. Centered Top Drag Handle Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color.White.copy(alpha = 0.30f) else Color(0xFFCBD5E1))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "स्टोर कार्यक्षेत्र व भूमिकाएं" else "Store Workspace & Roles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = if (isHindi) "सक्रिय भूमिका या शाखा बदलें" else "Switch active workspace or role",
                            fontSize = 11.5.sp,
                            color = secondaryTextColor
                        )
                    }

                    // Sleek [ ✕ ] Dismiss Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFF1F5F9))
                            .border(
                                0.5.dp,
                                if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFE2E8F0),
                                CircleShape
                            )
                            .clickable { performSmoothDismiss(null) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Close",
                            tint = if (isDark) Color.White else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 3. Workspace List Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Active Store Card Banner (Quiet Luxury Monochromatic)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Store,
                                    contentDescription = null,
                                    tint = if (isDark) Color.White else Color(0xFF0F172A),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedWorkspace?.businessName ?: "Store",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryTextColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)
                                    ) {
                                        Text(
                                            text = selectedWorkspace?.roleName ?: "STORE OWNER",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else Color(0xFF0F172A),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    if (selectedWorkspace?.branchName != null) {
                                        Text(
                                            text = "• ${selectedWorkspace.branchName}",
                                            fontSize = 11.sp,
                                            color = secondaryTextColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = if (isHindi) "उपलब्ध वर्कस्पेस व रोल चुनें" else "Switch Active Workspace or Role",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )

                    // List of Switchable Workspaces (Soft Gray Quiet Luxury matching Edit Profile)
                    workspaceList.forEach { item ->
                        val isSelected = item.isSelected
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectRole(item.roleEnum, item.staffRole)
                                    performSmoothDismiss(null)
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) {
                                if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                            } else {
                                if (isDark) Color.White.copy(alpha = 0.04f) else Color(0xFFF8FAFC)
                            },
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) {
                                    if (isDark) Color.White.copy(alpha = 0.24f) else Color(0xFFCBD5E1)
                                } else {
                                    if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) {
                                                    if (isDark) Color.White.copy(alpha = 0.14f) else Color(0xFFE2E8F0)
                                                } else {
                                                    if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (item.roleEnum == StoreRole.PUBLIC_CUSTOMER) Lucide.ShoppingBag else Lucide.Store,
                                            contentDescription = null,
                                            tint = if (isSelected) {
                                                if (isDark) Color.White else Color(0xFF0F172A)
                                            } else secondaryTextColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = item.businessName,
                                            fontSize = 13.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = primaryTextColor
                                        )
                                        Text(
                                            text = "${item.roleName}${if (item.branchName != null) " • ${item.branchName}" else ""}",
                                            fontSize = 11.sp,
                                            color = secondaryTextColor
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) Color.White else Color(0xFF0F172A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Check,
                                            contentDescription = "Selected",
                                            tint = if (isDark) Color(0xFF0F172A) else Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Register New Store / Branch Button (Quiet Luxury Minimalist)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                performSmoothDismiss {
                                    onRegisterNewBusiness()
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Plus,
                                contentDescription = null,
                                tint = if (isDark) Color.White else Color(0xFF0F172A),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (isHindi) "+ नया स्टोर या शाखा जोड़ें" else "+ Register New Store / Branch",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }
        }
    }
}
