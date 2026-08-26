package com.vidyasetuai.feature_store.presentation.screen.role_staff.catalog.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.network.SupabaseStorageHelper
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductBottomSheet(
    isHindi: Boolean = false,
    editingItem: ItemWithStockUiModel? = null,
    categories: List<ItemCategoryEntity> = emptyList(),
    isSaving: Boolean = false,
    onDismiss: () -> Unit,
    onOpenCategoryModal: () -> Unit,
    onGenerateSku: (String) -> String,
    onSave: (
        name: String,
        categoryId: String?,
        itemType: String,
        description: String?,
        sku: String?,
        barcode: String?,
        hsnSacCode: String?,
        salePrice: Double,
        mrp: Double?,
        purchasePrice: Double,
        taxRate: Double,
        isTaxInclusive: Boolean,
        unit: String,
        foodType: String,
        isAvailableOnline: Boolean,
        imageUrl: String?,
        extraImages: String?,
        initialStock: Double,
        lowStockThreshold: Double
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Form Field States
    var activeTab by remember { mutableIntStateOf(0) } // 0: General, 1: Pricing & GST, 2: Unit & Stock

    var itemType by remember(editingItem) { mutableStateOf(editingItem?.item?.itemType ?: "PRODUCT") }
    var name by remember(editingItem) { mutableStateOf(editingItem?.item?.name ?: "") }
    var selectedCategoryId by remember(editingItem) { mutableStateOf(editingItem?.item?.categoryId ?: "") }
    var foodType by remember(editingItem) { mutableStateOf(editingItem?.item?.foodType ?: "VEG") }
    var isAvailableOnline by remember(editingItem) { mutableStateOf(editingItem?.item?.isAvailableOnline ?: true) }
    var description by remember(editingItem) { mutableStateOf(editingItem?.item?.description ?: "") }

    var salePriceText by remember(editingItem) { mutableStateOf(if (editingItem != null && editingItem.item.salePrice > 0) editingItem.item.salePrice.toString() else "") }
    var mrpText by remember(editingItem) { mutableStateOf(if (editingItem?.item?.mrp != null && editingItem.item.mrp > 0) editingItem.item.mrp.toString() else "") }
    var purchasePriceText by remember(editingItem) { mutableStateOf(if (editingItem != null && editingItem.item.purchasePrice > 0) editingItem.item.purchasePrice.toString() else "") }
    var taxRate by remember(editingItem) { mutableStateOf(editingItem?.item?.taxRate ?: 5.0) }
    var isTaxInclusive by remember(editingItem) { mutableStateOf(editingItem?.item?.isTaxInclusive ?: true) }
    var hsnSacCode by remember(editingItem) { mutableStateOf(editingItem?.item?.hsnSacCode ?: "") }

    var unit by remember(editingItem) { mutableStateOf(editingItem?.item?.unit ?: "PCS") }
    var sku by remember(editingItem) { mutableStateOf(editingItem?.item?.sku ?: "") }
    var barcode by remember(editingItem) { mutableStateOf(editingItem?.item?.barcode ?: "") }
    var initialStockText by remember(editingItem) { mutableStateOf(if (editingItem != null) editingItem.currentStock.toString() else "0") }
    var lowStockThresholdText by remember(editingItem) { mutableStateOf(if (editingItem != null) editingItem.lowStockThreshold.toString() else "5") }

    // Image & Multi-Photo Gallery States & Launchers
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isUploadingMain by remember { mutableStateOf(false) }
    var uploadingSlotIndex by remember { mutableStateOf<Int?>(null) }
    var targetSlotForPicker by remember { mutableStateOf<Int?>(null) }
    var showManualUrlInput by remember { mutableStateOf(false) }

    var imageUrl by remember(editingItem) { mutableStateOf(editingItem?.item?.imageUrl ?: "") }
    var extraImagesList by remember(editingItem) {
        mutableStateOf(
            try {
                val raw = editingItem?.item?.extraImages ?: "[]"
                val parsed = kotlinx.serialization.json.Json.decodeFromString<List<String>>(raw)
                parsed.filter { it.isNotBlank() }.toMutableList()
            } catch (e: Exception) {
                mutableListOf<String>()
            }
        )
    }

    // Gallery Picker Launcher for Main Product Photo
    val mainPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isUploadingMain = true
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes != null && bytes.isNotEmpty()) {
                        val fileName = "items/${UUID.randomUUID()}.jpg"
                        val uploadedUrl = SupabaseStorageHelper.uploadImage("business-media", fileName, bytes)
                        imageUrl = uploadedUrl
                        Toast.makeText(
                            context,
                            if (isHindi) "मुख्य फोटो सफलतापूर्वक अपलोड हुई!" else "Main photo uploaded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, if (isHindi) "फोटो पढ़ने में असमर्थ।" else "Unable to read image.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        context,
                        if (isHindi) "फोटो अपलोड नहीं हो सकी। कृपया इंटरनेट कनेक्शन जांचें।" else "Failed to upload photo. Check network connection.",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    isUploadingMain = false
                }
            }
        }
    }

    // Gallery Picker Launcher for Additional 4 Slots
    val slotPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val slotIdx = targetSlotForPicker
        if (uri != null && slotIdx != null) {
            coroutineScope.launch {
                uploadingSlotIndex = slotIdx
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes != null && bytes.isNotEmpty()) {
                        val fileName = "items/${UUID.randomUUID()}.jpg"
                        val uploadedUrl = SupabaseStorageHelper.uploadImage("business-media", fileName, bytes)
                        val currentList = extraImagesList.toMutableList()
                        if (slotIdx < currentList.size) {
                            currentList[slotIdx] = uploadedUrl
                        } else {
                            currentList.add(uploadedUrl)
                        }
                        extraImagesList = currentList
                        Toast.makeText(
                            context,
                            if (isHindi) "गैलरी फोटो सफलतापूर्वक जोड़ी गई!" else "Gallery photo added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        context,
                        if (isHindi) "फोटो अपलोड नहीं हो सकी। कृपया इंटरनेट कनेक्शन जांचें।" else "Failed to upload photo. Check network.",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    uploadingSlotIndex = null
                    targetSlotForPicker = null
                }
            }
        } else {
            targetSlotForPicker = null
        }
    }

    var isCategoryDropdownOpen by remember { mutableStateOf(false) }
    var isUnitDropdownOpen by remember { mutableStateOf(false) }

    val uomList = listOf(
        Pair("PCS", "PCS (Pieces / Units)"),
        Pair("KG", "KG (Kilograms)"),
        Pair("GM", "GM (Grams)"),
        Pair("LTR", "LTR (Litres)"),
        Pair("ML", "ML (Millilitres)"),
        Pair("PKT", "PKT (Packets)"),
        Pair("BOX", "BOX (Boxes)"),
        Pair("PORTION", "PORTION (Portions / Servings)"),
        Pair("DOZEN", "DOZEN (Dozens)")
    )

    val gstSlabs = listOf(
        Triple(0.0, "0%", "Exempt"),
        Triple(3.0, "3%", "Gold"),
        Triple(5.0, "5%", "Food"),
        Triple(12.0, "12%", "Std"),
        Triple(18.0, "18%", "Goods"),
        Triple(28.0, "28%", "Luxury")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.Package,
                                contentDescription = "Item",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (editingItem == null) {
                                if (isHindi) "नया सामान / मेनू आइटम" else "Add Product / Menu Item"
                            } else {
                                if (isHindi) "सामान विवरण अपडेट करें" else "Edit Product / Menu Item"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "रिटेल, फूड या सर्विस के लिए यूनिवर्सल कैटलॉग" else "Universal catalog for retail goods, food dishes or services",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- 3 ENTERPRISE TABS (HIG Segmented Control) ---
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AppColors.EmeraldGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = AppColors.EmeraldGreen
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Text(
                            text = if (isHindi) "सामान्य व श्रेणी" else "General & Category",
                            fontSize = 12.sp,
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Text(
                            text = if (isHindi) "मूल्य व GST" else "Pricing & Indian GST",
                            fontSize = 12.sp,
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = {
                        Text(
                            text = if (isHindi) "इकाई, बारकोड व स्टॉक" else "Unit, Barcode & Stock",
                            fontSize = 12.sp,
                            fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }

            // --- SCROLLABLE TAB CONTENT BODY ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (activeTab) {
                        // =========================================================================
                        // TAB 1: GENERAL & CATEGORY
                        // =========================================================================
                        0 -> {
                            // 1. Item Type Selector
                            Text(
                                text = if (isHindi) "सामान का प्रकार (Item Type)" else "Item Type",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Option A: Physical Product
                                ItemTypeCard(
                                    title = if (isHindi) "भौतिक सामान (Physical Product)" else "Physical Product",
                                    subtitle = if (isHindi) "स्टॉक ट्रैकिंग सहित मूर्त सामान" else "Tangible goods with stock tracking",
                                    icon = Lucide.Package,
                                    isSelected = itemType == "PRODUCT",
                                    onClick = { itemType = "PRODUCT" },
                                    modifier = Modifier.weight(1f)
                                )

                                // Option B: Service / Labor
                                ItemTypeCard(
                                    title = if (isHindi) "सेवा / लेबर (Service / Labor)" else "Service / Labor",
                                    subtitle = if (isHindi) "बिना स्टॉक वाली सेवा (रिपेयर, धुलाई आदि)" else "No inventory required (e.g. Repair)",
                                    icon = Lucide.Wrench,
                                    isSelected = itemType == "SERVICE",
                                    onClick = { itemType = "SERVICE" },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // 2. Product Name
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text(if (isHindi) "सामान / उत्पाद का नाम *" else "Item / Product Name *") },
                                placeholder = { Text("e.g. Cheese Veg Burger, Basmati Rice 5kg") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            // 3. Category Selector with Quick Add Link
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "श्रेणी (Category) *" else "Category *",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (isHindi) "+ श्रेणी जोड़ें / प्रबंधित करें" else "+ Add / Manage Categories",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen,
                                        modifier = Modifier.clickable { onOpenCategoryModal() }
                                    )
                                }

                                ExposedDropdownMenuBox(
                                    expanded = isCategoryDropdownOpen,
                                    onExpandedChange = { isCategoryDropdownOpen = !isCategoryDropdownOpen }
                                ) {
                                    val selectedCatName = categories.find { it.id == selectedCategoryId }?.name
                                        ?: if (isHindi) "श्रेणी चुनें..." else "Select Category..."

                                    OutlinedTextField(
                                        value = selectedCatName,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownOpen) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    ExposedDropdownMenu(
                                        expanded = isCategoryDropdownOpen,
                                        onDismissRequest = { isCategoryDropdownOpen = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(if (isHindi) "कोई श्रेणी नहीं (Uncategorized)" else "None (Uncategorized)") },
                                            onClick = {
                                                selectedCategoryId = ""
                                                isCategoryDropdownOpen = false
                                            }
                                        )
                                        categories.forEach { cat ->
                                            DropdownMenuItem(
                                                text = { Text(cat.name) },
                                                onClick = {
                                                    selectedCategoryId = cat.id
                                                    isCategoryDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // 4. Dietary / Food Indicator
                            Text(
                                text = if (isHindi) "डाइटरी / फूड प्रकार (Dietary Indicator)" else "Dietary / Food Indicator",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val foodOptions = listOf(
                                    Triple("VEG", if (isHindi) "शाकाहारी" else "Veg", Color(0xFF16A34A)),
                                    Triple("NON_VEG", if (isHindi) "मांसाहारी" else "Non-Veg", Color(0xFFDC2626)),
                                    Triple("EGG", if (isHindi) "अंडा" else "Egg", Color(0xFFEAB308)),
                                    Triple("NONE", if (isHindi) "सामान्य" else "General", Color(0xFF6B7280))
                                )

                                foodOptions.forEach { (type, label, color) ->
                                    val isSelected = foodType.equals(type, ignoreCase = true)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(color)) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { foodType = type }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            // 5. Show on Online Storefront & KDS
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isHindi) "ऑनलाइन स्टोर व KDS पर दिखाएं" else "Show on Online Storefront & KDS",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isHindi) "वेब स्टोर ग्राहकों और रसोई KDS के लिए उपलब्ध" else "Available for web store customer orders & kitchen tickets",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(
                                        checked = isAvailableOnline,
                                        onCheckedChange = { isAvailableOnline = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = AppColors.EmeraldGreen)
                                    )
                                }
                            }

                            // 6. Description / Ingredients
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text(if (isHindi) "विवरण / सामग्री (वैकल्पिक)" else "Description / Ingredients (Optional)") },
                                placeholder = { Text(if (isHindi) "सामान या डिश के बारे में संक्षिप्त जानकारी..." else "Short details about the product or dish...") },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // 7. Multi-Photo Product Gallery (Main Photo + 4 Extra Slots)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isHindi) "उत्पाद फोटो गैलरी (मुख्य + 4 अतिरिक्त)" else "Product Photos (Main + 4 Extra)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${(if (imageUrl.isNotBlank()) 1 else 0) + extraImagesList.size}/5",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.EmeraldGreen
                                        )
                                    }

                                    // --- 1. MAIN PRODUCT PHOTO PICKER & PREVIEW ---
                                    if (imageUrl.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.EmeraldGreen.copy(alpha = 0.5f)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                coil.compose.AsyncImage(
                                                    model = imageUrl,
                                                    contentDescription = "Main Photo Preview",
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(RoundedCornerShape(12.dp))
                                                )
                                                if (isUploadingMain) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(Color.Black.copy(alpha = 0.5f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                                                    }
                                                }
                                                // Action buttons on top-right
                                                Row(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(8.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    // Change / Replace Photo button
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color.Black.copy(alpha = 0.65f),
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clickable {
                                                                if (!isUploadingMain) {
                                                                    mainPhotoPickerLauncher.launch("image/*")
                                                                }
                                                            }
                                                    ) {
                                                        Icon(
                                                            imageVector = Lucide.Pencil,
                                                            contentDescription = "Change",
                                                            tint = Color.White,
                                                            modifier = Modifier.padding(7.dp)
                                                        )
                                                    }
                                                    // Remove Photo button
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color(0xFFDC2626).copy(alpha = 0.85f),
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clickable {
                                                                imageUrl = ""
                                                            }
                                                    ) {
                                                        Icon(
                                                            imageVector = Lucide.Trash2,
                                                            contentDescription = "Remove",
                                                            tint = Color.White,
                                                            modifier = Modifier.padding(7.dp)
                                                        )
                                                    }
                                                }
                                                // Badge on bottom-left
                                                Surface(
                                                    shape = RoundedCornerShape(topEnd = 8.dp),
                                                    color = AppColors.EmeraldGreen,
                                                    modifier = Modifier.align(Alignment.BottomStart)
                                                ) {
                                                    Text(
                                                        text = if (isHindi) "⭐ मुख्य फोटो" else "⭐ Main Photo",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Empty Main Photo clickable placeholder
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, AppColors.EmeraldGreen.copy(alpha = 0.4f)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(96.dp)
                                                .clickable {
                                                    if (!isUploadingMain) {
                                                        mainPhotoPickerLauncher.launch("image/*")
                                                    }
                                                }
                                        ) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                                if (isUploadingMain) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        CircularProgressIndicator(color = AppColors.EmeraldGreen, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                                                        Text(
                                                            text = if (isHindi) "क्लाउड पर अपलोड हो रही है..." else "Uploading to Cloud...",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = AppColors.EmeraldGreen
                                                        )
                                                    }
                                                } else {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                        modifier = Modifier.padding(14.dp)
                                                    ) {
                                                        Surface(
                                                            shape = CircleShape,
                                                            color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                                            modifier = Modifier.size(46.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Lucide.ImagePlus,
                                                                contentDescription = "Pick Image",
                                                                tint = AppColors.EmeraldGreen,
                                                                modifier = Modifier.padding(11.dp)
                                                            )
                                                        }
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(
                                                                text = if (isHindi) "📷 मुख्य फोटो अपलोड करें (गैलरी से चुनें)" else "📷 Upload Main Photo (Pick from Gallery)",
                                                                fontSize = 13.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onSurface
                                                            )
                                                            Text(
                                                                text = if (isHindi) "टैप करके अपने फोन से मुख्य तस्वीर चुनें" else "Tap to choose product cover image from phone",
                                                                fontSize = 11.sp,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // --- 2. 4 EXTRA GALLERY PHOTO SLOTS ---
                                    Text(
                                        text = if (isHindi) "अतिरिक्त गैलरी तस्वीरें (Extra 4 Gallery Photos):" else "Additional 4 Gallery Photos:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    androidx.compose.foundation.lazy.LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(4) { index ->
                                            val currentUrl = extraImagesList.getOrNull(index)
                                            val isThisSlotUploading = uploadingSlotIndex == index

                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (!currentUrl.isNullOrBlank()) AppColors.EmeraldGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (!currentUrl.isNullOrBlank()) AppColors.EmeraldGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                                ),
                                                modifier = Modifier
                                                    .size(76.dp)
                                                    .clickable {
                                                        if (!isThisSlotUploading) {
                                                            targetSlotForPicker = index
                                                            slotPhotoPickerLauncher.launch("image/*")
                                                        }
                                                    }
                                            ) {
                                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                                    if (isThisSlotUploading) {
                                                        CircularProgressIndicator(
                                                            color = AppColors.EmeraldGreen,
                                                            modifier = Modifier.size(20.dp),
                                                            strokeWidth = 2.dp
                                                        )
                                                    } else if (!currentUrl.isNullOrBlank()) {
                                                        coil.compose.AsyncImage(
                                                            model = currentUrl,
                                                            contentDescription = "Photo ${index + 1}",
                                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .clip(RoundedCornerShape(10.dp))
                                                        )
                                                        // Remove button on top-right of slot thumbnail
                                                        Surface(
                                                            shape = CircleShape,
                                                            color = Color.Black.copy(alpha = 0.7f),
                                                            modifier = Modifier
                                                                .align(Alignment.TopEnd)
                                                                .padding(3.dp)
                                                                .size(20.dp)
                                                                .clickable {
                                                                    val currentList = extraImagesList.toMutableList()
                                                                    if (index < currentList.size) {
                                                                        currentList.removeAt(index)
                                                                        extraImagesList = currentList
                                                                    }
                                                                }
                                                        ) {
                                                            Icon(
                                                                imageVector = Lucide.X,
                                                                contentDescription = "Remove",
                                                                tint = Color.White,
                                                                modifier = Modifier.padding(4.dp)
                                                            )
                                                        }
                                                    } else {
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Lucide.Plus,
                                                                contentDescription = "Add",
                                                                tint = AppColors.EmeraldGreen,
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                            Text(
                                                                text = "+ फोटो ${index + 1}",
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // --- 3. MANUAL URL INPUT OPTION (Collapsible) ---
                                    Text(
                                        text = if (showManualUrlInput) (if (isHindi) "▲ इमेज URL फ़ील्ड छिपाएं" else "▲ Hide Image URL Field") else (if (isHindi) "▼ या इमेज का सीधा URL दर्ज करें" else "▼ Or enter Image URL manually"),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.EmeraldGreen,
                                        modifier = Modifier
                                            .clickable { showManualUrlInput = !showManualUrlInput }
                                            .padding(vertical = 2.dp)
                                    )

                                    if (showManualUrlInput) {
                                        OutlinedTextField(
                                            value = imageUrl,
                                            onValueChange = { imageUrl = it },
                                            label = { Text(if (isHindi) "मुख्य फोटो URL (Main Photo URL)" else "Main Photo URL") },
                                            placeholder = { Text("https://example.com/item.jpg") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // =========================================================================
                        // TAB 2: PRICING & INDIAN GST
                        // =========================================================================
                        1 -> {
                            // 1. Selling Price & MRP
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = salePriceText,
                                        onValueChange = { salePriceText = it },
                                        label = { Text(if (isHindi) "बिक्री मूल्य (₹) *" else "Selling Price (₹) *") },
                                        placeholder = { Text("0.00") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Text(
                                        text = if (isHindi) "ग्राहक से लिया जाने वाला फाइनल बिल मूल्य" else "Final customer billing price",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = mrpText,
                                        onValueChange = { mrpText = it },
                                        label = { Text(if (isHindi) "प्रिंटेड MRP (₹)" else "Printed MRP (₹)") },
                                        placeholder = { Text("0.00") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Text(
                                        text = if (isHindi) "डिस्काउंट मार्जिन गणना हेतु" else "For retail discount margin",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                    )
                                }
                            }

                            // 2. Purchase Cost / Making Price
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = purchasePriceText,
                                    onValueChange = { purchasePriceText = it },
                                    label = { Text(if (isHindi) "खरीद मूल्य / लागत (₹)" else "Purchase Cost / Making Price (₹)") },
                                    placeholder = { Text("0.00") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Text(
                                    text = if (isHindi) "मुनाफ़ा (Profit & Loss) मार्जिन गणना हेतु उपयोगी" else "Used for Daybook profit & loss margin calculations",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            // 3. GST Tax Rate Grid (6 Slabs)
                            Text(
                                text = if (isHindi) "GST टैक्स दर (%)" else "GST Tax Rate (%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                gstSlabs.forEach { (rate, label, sub) ->
                                    val isSelected = taxRate == rate
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(AppColors.EmeraldGreen)) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { taxRate = rate }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "($sub)",
                                                fontSize = 9.sp,
                                                color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            // 4. Tax Inclusive Switch
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isHindi) "मूल्य में GST टैक्स शामिल है (Tax Inclusive)" else "Price Includes GST (Tax Inclusive)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        val sPriceVal = salePriceText.toDoubleOrNull() ?: 0.0
                                        Text(
                                            text = if (isHindi) "बिक्री मूल्य ₹$sPriceVal में पहले से ${taxRate.toInt()}% GST शामिल है" else "Selling price ₹$sPriceVal already includes ${taxRate.toInt()}% GST",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(
                                        checked = isTaxInclusive,
                                        onCheckedChange = { isTaxInclusive = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = AppColors.EmeraldGreen)
                                    )
                                }
                            }

                            // 5. HSN / SAC Code
                            OutlinedTextField(
                                value = hsnSacCode,
                                onValueChange = { hsnSacCode = it },
                                label = { Text(if (isHindi) "HSN / SAC कोड (GST इनवॉइसिंग हेतु)" else "HSN / SAC Code (For Indian GST Invoices)") },
                                placeholder = { Text("e.g. 2106 (Food) or 9987 (Service)") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // =========================================================================
                        // TAB 3: UNIT, BARCODE & STOCK
                        // =========================================================================
                        2 -> {
                            // 1. Unit of Measurement (UOM)
                            ExposedDropdownMenuBox(
                                expanded = isUnitDropdownOpen,
                                onExpandedChange = { isUnitDropdownOpen = !isUnitDropdownOpen }
                            ) {
                                val currentUomLabel = uomList.find { it.first == unit }?.second ?: "$unit (Units)"

                                OutlinedTextField(
                                    value = currentUomLabel,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (isHindi) "माप की इकाई (UOM) *" else "Unit of Measurement (UOM) *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isUnitDropdownOpen) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                ExposedDropdownMenu(
                                    expanded = isUnitDropdownOpen,
                                    onDismissRequest = { isUnitDropdownOpen = false }
                                ) {
                                    uomList.forEach { (key, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                unit = key
                                                isUnitDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            // 2. SKU Code & Auto-Generate Button
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "SKU कोड" else "SKU Code",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (isHindi) "⚡ ऑटो-जनरेट करें" else "Auto-Generate",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen,
                                        modifier = Modifier.clickable {
                                            if (name.isNotBlank()) {
                                                sku = onGenerateSku(name)
                                            }
                                        }
                                    )
                                }

                                OutlinedTextField(
                                    value = sku,
                                    onValueChange = { sku = it },
                                    placeholder = { Text("e.g. BUR-01") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // 3. Barcode / UPC
                            OutlinedTextField(
                                value = barcode,
                                onValueChange = { barcode = it },
                                label = { Text(if (isHindi) "बारकोड / UPC कोड" else "Barcode / UPC") },
                                placeholder = { Text(if (isHindi) "बारकोड टाइप करें..." else "Scan or type barcode") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Lucide.Barcode,
                                        contentDescription = "Barcode",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // 4. Initial Opening Stock & Alert Limit Card (Only for PHYSICAL PRODUCT)
                            if (itemType == "PRODUCT") {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Lucide.Boxes,
                                                contentDescription = "Stock",
                                                tint = AppColors.EmeraldGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = if (isHindi) "प्रारंभिक स्टॉक व अलर्ट सीमा" else "Initial Opening Stock & Alert Limit",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = initialStockText,
                                                onValueChange = { initialStockText = it },
                                                label = { Text(if (isHindi) "ओपनिंग स्टॉक ($unit)" else "Opening Qty ($unit)") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp)
                                            )

                                            OutlinedTextField(
                                                value = lowStockThresholdText,
                                                onValueChange = { lowStockThresholdText = it },
                                                label = { Text(if (isHindi) "लो-स्टॉक अलर्ट ($unit)" else "Low Stock Alert ($unit)") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- STICKY BOTTOM FOOTER BAR ---
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Price & Tax Preview Pill
                val livePrice = salePriceText.toDoubleOrNull() ?: 0.0
                Column {
                    Text(
                        text = "₹${"%.2f".format(livePrice)} • $unit",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${taxRate.toInt()}% GST ${if (isTaxInclusive) "(Incl.)" else "(Excl.)"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Actions: Cancel & Save
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text(if (isHindi) "रद्द करें" else "Cancel")
                    }

                    Button(
                        onClick = {
                            val sPrice = salePriceText.toDoubleOrNull() ?: 0.0
                            val mrpVal = mrpText.toDoubleOrNull()
                            val pPrice = purchasePriceText.toDoubleOrNull() ?: 0.0
                            val initStock = initialStockText.toDoubleOrNull() ?: 0.0
                            val lowStock = lowStockThresholdText.toDoubleOrNull() ?: 5.0
                            val extraJson = try {
                                kotlinx.serialization.json.Json.encodeToString(extraImagesList.filter { it.isNotBlank() })
                            } catch (e: Exception) {
                                "[]"
                            }

                            onSave(
                                name,
                                selectedCategoryId.ifBlank { null },
                                itemType,
                                description.ifBlank { null },
                                sku.ifBlank { null },
                                barcode.ifBlank { null },
                                hsnSacCode.ifBlank { null },
                                sPrice,
                                mrpVal,
                                pPrice,
                                taxRate,
                                isTaxInclusive,
                                unit,
                                foodType,
                                isAvailableOnline,
                                imageUrl.ifBlank { null },
                                extraJson,
                                initStock,
                                lowStock
                            )
                        },
                        enabled = name.isNotBlank() && (salePriceText.toDoubleOrNull() ?: 0.0) >= 0.0 && !isSaving,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                        modifier = Modifier.height(44.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "सेव हो रहा है..." else "Saving...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = if (editingItem == null) {
                                    if (isHindi) "सामान सेव करें" else "Save Product"
                                } else {
                                    if (isHindi) "अपडेट करें" else "Update Product"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemTypeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(AppColors.EmeraldGreen)) else null,
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 12.sp
            )
        }
    }
}
