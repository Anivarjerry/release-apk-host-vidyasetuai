package com.vidyasetuai.feature_store.presentation.screen.role_owner.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreSettingsScreen(
    isHindi: Boolean = false,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val viewModel: StoreSettingsViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return StoreSettingsViewModel(context) as T
        }
    })

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val business = uiState.overview.business
    val settings = uiState.overview.settings
    val mainBranch = uiState.overview.mainBranch

    // Pillar 5: Layered BackHandler Navigation Resilience
    BackHandler(enabled = true) {
        when {
            uiState.isBranchSheetOpen -> viewModel.closeBranchSheet()
            uiState.isBillingSheetOpen -> viewModel.closeBillingSheet()
            uiState.isBankUpiSheetOpen -> viewModel.closeBankUpiSheet()
            uiState.isFeatureSwitchesSheetOpen -> viewModel.closeFeatureSwitchesSheet()
            uiState.isGstEwaySheetOpen -> viewModel.closeGstEwaySheet()
            uiState.isQrStandeeModalOpen -> viewModel.closeQrStandeeModal()
            else -> onBack()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "स्टोर व बिलिंग सेटिंग्स" else "Store & Billing Settings",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = business?.tradeName ?: (if (isHindi) "स्टोर कॉन्फिगरेशन" else "Store Configuration"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Lucide.ArrowLeft, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && business == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF10B981))
            }
        } else {
            // Rule 7: Virtualized Lazy Layout for 60 FPS buttery scrolling
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
            ) {
                // =========================================================================
                // 🌐 1. Public Web Store & Digital Menu Top Banner
                // =========================================================================
                item(key = "store_banner") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Lucide.Store, contentDescription = "Store", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                    }

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = if (isHindi) "डिजिटल वेब स्टोर व मेनू" else "Public Web Store & Menu",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "LIVE 🟢",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF10B981),
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "/store/${business?.slug ?: "shop"}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Banner Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.openQrStandeeModal() },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Lucide.Printer, contentDescription = "Standee", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = if (isHindi) "QR स्टैंडी" else "Print QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val url = "https://vidyasetuai.com/store/${business?.slug ?: "shop"}"
                                        clipboardManager.setText(AnnotatedString(url))
                                        Toast.makeText(context, "लिंक कॉपी हो गया: $url", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Lucide.Copy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = if (isHindi) "कॉपी लिंक" else "Copy Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val url = "https://vidyasetuai.com/store/${business?.slug ?: "shop"}"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = if (isHindi) "स्टोर खोलें" else "Storefront", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(imageVector = Lucide.ChevronRight, contentDescription = "Open", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.surface)
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 🏢 2. Card 1: Primary Branch & Address
                // =========================================================================
                item(key = "card_branch") {
                    SettingsSectionCard(
                        icon = Lucide.Building2,
                        title = if (isHindi) "प्राथमिक शाखा व पता" else "Primary Branch & Address",
                        subtitle = if (isHindi) "बिल व रसीदों पर प्रिंट होने वाला पता" else "Printed at the top of customer bills & receipts",
                        statusBadgeText = "ACTIVE",
                        statusBadgeColor = Color(0xFF10B981),
                        buttonText = if (mainBranch != null) (if (isHindi) "पता संपादित करें" else "Edit Branch Address") else (if (isHindi) "पता जोड़ें" else "Add Branch Address"),
                        onButtonClick = { viewModel.openBranchSheet() }
                    ) {
                        if (mainBranch != null) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column {
                                    Text(
                                        text = if (isHindi) "काउंटर / शाखा" else "COUNTER / BRANCH NAME",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = mainBranch.branchName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (isHindi) "दुकान का पता" else "SHOP ADDRESS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = mainBranch.addressLine,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${mainBranch.city}, ${mainBranch.state} - ${mainBranch.pincode}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "GST State Code:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = business?.stateCode ?: "08",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Store GPS Location:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (mainBranch.lat != null && mainBranch.lng != null) {
                                        Text(
                                            text = "📍 ${String.format("%.4f", mainBranch.lat)}, ${String.format("%.4f", mainBranch.lng)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981)
                                        )
                                    } else {
                                        Text(text = "Not Pinned", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = if (isHindi) "कोई मुख्य शाखा कॉन्फिगर नहीं है।" else "No primary branch configured yet.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // =========================================================================
                // 🧾 3. Card 2: Invoicing & Thermal Printer
                // =========================================================================
                item(key = "card_invoicing") {
                    val isGst = settings?.enableGstBilling == true
                    val prefix = settings?.invoicePrefix ?: "INV-24/"
                    val printerSize = settings?.thermalPrinterSize ?: "3_INCH"

                    SettingsSectionCard(
                        icon = Lucide.Receipt,
                        title = if (isHindi) "इनवॉइसिंग व थर्मल प्रिंटर" else "Invoicing & Thermal Printer",
                        subtitle = if (isHindi) "बिल नंबरिंग, पेपर फॉर्मेट व टैक्स नियम" else "Series numbering, paper format & GST rules",
                        statusBadgeText = if (isGst) "GST BILLING ON" else "NON-GST BILL",
                        statusBadgeColor = if (isGst) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                        buttonText = if (isHindi) "इनवॉइसिंग सेटिंग्स" else "Configure Invoicing",
                        onButtonClick = { viewModel.openBillingSheet() }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "BILL PREFIX", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = prefix, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = "Sample: ${prefix}0001", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "PRINTER WIDTH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = printerSize.replace("_", " "), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = if (printerSize == "3_INCH") "80mm POS Roll" else "Standard Format", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Packing Fee: ₹${String.format("%.2f", settings?.packingChargeDefault ?: 0.0)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Delivery Fee: ₹${String.format("%.2f", settings?.deliveryChargeDefault ?: 0.0)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // =========================================================================
                // 💳 4. Card 3: Payment UPI QR & Bank Account
                // =========================================================================
                item(key = "card_bank_upi") {
                    val upiId = business?.upiId

                    SettingsSectionCard(
                        icon = Lucide.QrCode,
                        title = if (isHindi) "पेमेंट UPI QR व बैंक खाता" else "Payment UPI QR & Bank Account",
                        subtitle = if (isHindi) "बिल पर ऑटो-जनरेटेड पेमेंट QR कोड" else "Auto-generated payment QR code on customer bills",
                        statusBadgeText = if (!upiId.isNullOrBlank()) "UPI READY" else "QR PENDING",
                        statusBadgeColor = if (!upiId.isNullOrBlank()) Color(0xFF10B981) else Color(0xFFEF4444),
                        buttonText = if (!upiId.isNullOrBlank()) (if (isHindi) "बैंक व UPI बदलें" else "Edit Bank & UPI") else (if (isHindi) "UPI सेट करें" else "Setup UPI QR"),
                        onButtonClick = { viewModel.openBankUpiSheet() }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "RECEIPT PAYMENT UPI ID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = upiId ?: "Not Configured Yet",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (!upiId.isNullOrBlank()) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Lucide.QrCode,
                                        contentDescription = "QR",
                                        tint = if (!upiId.isNullOrBlank()) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            if (!business?.bankName.isNullOrBlank()) {
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Bank: ${business?.bankName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text(text = "A/C: ${business?.bankAccountNo ?: "—"}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // ⚙️ 5. Card 4: Active System Modules
                // =========================================================================
                item(key = "card_features") {
                    SettingsSectionCard(
                        icon = Lucide.SlidersHorizontal,
                        title = if (isHindi) "सक्रिय सिस्टम मॉड्यूल्स" else "Active System Modules",
                        subtitle = if (isHindi) "इस वर्कस्पेस के लिए सक्रिय टूल्स" else "Feature switches enabled for this workspace",
                        statusBadgeText = "SYSTEM",
                        statusBadgeColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        buttonText = if (isHindi) "मॉड्यूल्स बदलें" else "Toggle Features",
                        onButtonClick = { viewModel.openFeatureSwitchesSheet() }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                Pair("Kitchen Display System (KDS)", settings?.enableKds == true),
                                Pair("Delivery Rider Tracking", settings?.enableDeliveryTracking == true),
                                Pair("Multi-Branch Inventory", settings?.enableInventoryTracking != false),
                                Pair("Customer Udhar Khata", settings?.enableCustomerKhata != false),
                                Pair("Public Online Storefront", settings?.allowOnlineOrders != false)
                            ).forEach { (name, enabled) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (enabled) Color(0xFF10B981).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = if (enabled) "ENABLED" else "OFF",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (enabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 🛡️ 6. Card 5: Indian GST & E-Way Bill NIC Config
                // =========================================================================
                item(key = "card_gst_eway") {
                    val gstin = business?.gstin

                    SettingsSectionCard(
                        icon = Lucide.ShieldCheck,
                        title = if (isHindi) "GSTIN व E-Way Bill NIC" else "Indian GST & E-Way Bill Config",
                        subtitle = if (isHindi) "ई-वे बिल ऑटो-जनरेशन व NIC सेटअप" else "Direct NIC portal integration & credentials",
                        statusBadgeText = if (!gstin.isNullOrBlank()) "GST READY" else "SETUP REQ",
                        statusBadgeColor = if (!gstin.isNullOrBlank()) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                        buttonText = if (isHindi) "GST क्रेडेंशियल्स सेट करें" else "Configure GST & E-Way",
                        onButtonClick = { viewModel.openGstEwaySheet() }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "GSTIN Number:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = gstin ?: "Not Registered",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!gstin.isNullOrBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "GSP Provider:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = settings?.gspProviderName ?: "CLEARTAX",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Auto E-Way Bill (>₹50k):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = if (settings?.enableAutoEwayBill == true) "Active 🟢" else "Inactive",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (settings?.enableAutoEwayBill == true) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // 🪟 Modals & Bottom Sheets Integration
    // =========================================================================

    if (uiState.isBranchSheetOpen) {
        EditBranchAddressBottomSheet(
            isHindi = isHindi,
            initialBranch = mainBranch,
            isSaving = uiState.isSaving,
            onDismiss = { viewModel.closeBranchSheet() },
            onSave = { name, addr, city, state, pin, lat, lng ->
                viewModel.saveMainBranch(name, addr, city, state, pin, lat, lng)
            }
        )
    }

    if (uiState.isBillingSheetOpen) {
        EditBillingPrinterBottomSheet(
            isHindi = isHindi,
            initialSettings = settings,
            isSaving = uiState.isSaving,
            onDismiss = { viewModel.closeBillingSheet() },
            onSave = { prefix, printer, gst, pack, del, free ->
                viewModel.saveBillingSettings(prefix, printer, gst, pack, del, free)
            }
        )
    }

    if (uiState.isBankUpiSheetOpen) {
        EditBankUpiBottomSheet(
            isHindi = isHindi,
            initialBusiness = business,
            isSaving = uiState.isSaving,
            onDismiss = { viewModel.closeBankUpiSheet() },
            onSave = { upi, bank, acc, ifsc, branch ->
                viewModel.saveBankAndUpi(upi, bank, acc, ifsc, branch)
            }
        )
    }

    if (uiState.isFeatureSwitchesSheetOpen) {
        FeatureSwitchesBottomSheet(
            isHindi = isHindi,
            initialSettings = settings,
            isSaving = uiState.isSaving,
            onDismiss = { viewModel.closeFeatureSwitchesSheet() },
            onSave = { kds, rider, inv, khata, online ->
                viewModel.saveFeatureSwitches(kds, rider, inv, khata, online)
            }
        )
    }

    if (uiState.isGstEwaySheetOpen) {
        GstEwayBillConfigBottomSheet(
            isHindi = isHindi,
            initialBusiness = business,
            initialSettings = settings,
            isSaving = uiState.isSaving,
            onDismiss = { viewModel.closeGstEwaySheet() },
            onSave = { gstin, username, pass, gsp, autoEway ->
                viewModel.saveGstEwayConfig(gstin, username, pass, gsp, autoEway)
            }
        )
    }

    if (uiState.isQrStandeeModalOpen) {
        StoreQrStandeeBottomSheet(
            isHindi = isHindi,
            business = business,
            mainBranch = mainBranch,
            onDismiss = { viewModel.closeQrStandeeModal() }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    statusBadgeText: String,
    statusBadgeColor: Color,
    buttonText: String,
    onButtonClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBadgeColor.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusBadgeColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = statusBadgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusBadgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Body
            Box(modifier = Modifier.padding(14.dp)) {
                content()
            }

            // Footer Button
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onButtonClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Lucide.Pencil, contentDescription = "Edit", modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.surface)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = buttonText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface)
                }
            }
        }
    }
}
