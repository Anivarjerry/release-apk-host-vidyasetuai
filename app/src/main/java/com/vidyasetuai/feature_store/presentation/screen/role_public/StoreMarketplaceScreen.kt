package com.vidyasetuai.feature_store.presentation.screen.role_public

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import kotlinx.coroutines.launch

data class MarketplaceProduct(
    val id: String,
    val name: String,
    val price: Double,
    val unit: String,
    val merchantName: String,
    val distance: String,
    val imageUrl: String? = null,
    val rating: String = "4.8",
    val discountBadge: String? = null,
    var cartQty: Int = 0,
    val businessId: String = "",
    val businessSlug: String = "",
    val branchId: String? = null
)

/**
 * App Session In-Memory Cache Singleton.
 * Persists product items in RAM while switching between tabs (Home, Campus, Store, Profile).
 * Prevents repetitive Supabase RPC network calls until app is killed / cleared from Recent apps.
 */
object StoreMarketplaceCache {
    val cachedProducts = mutableStateListOf<MarketplaceProduct>()
    var isDataLoaded = false
}

/**
 * ROLE 4 (Public Customer): Flagship 2-Column Apple Quiet Luxury Product Marketplace Feed.
 * Features:
 * - 2-Column Grid with Large HD Aspect-Ratio Product Imagery (110dp).
 * - Apple Quiet Luxury Monochromatic Palette (matching ProfileScreen.kt & EditProfileBottomSheet).
 * - Category Filter Chips (All, Food, Sweets, Drinks, Grocery).
 * - 0ms Reactive Cart Count & Proceed Bar via derivedStateOf.
 * - Virtualized 60 FPS scrolling with Zero Layout Shift.
 */
@Composable
fun StoreMarketplaceScreen(
    isHindi: Boolean = false,
    isDark: Boolean = false,
    activeOrderOtp: String? = null, // Displayed ONLY when an active order OTP exists
    activeOrderNumber: String? = null,
    onOpenCartCheckout: () -> Unit = {},
    onOpenLiveOtpOrder: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val remoteDataSource = remember { StoreRemoteDataSource() }
    val gridState = rememberLazyGridState()

    var isLoading by remember { mutableStateOf(!StoreMarketplaceCache.isDataLoaded) }
    var isFetchingNextBatch by remember { mutableStateOf(false) }

    val productsList = StoreMarketplaceCache.cachedProducts

    // Active Category Filter: "ALL", "FOOD", "SWEETS", "DRINKS", "GROCERY"
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = remember(isHindi) {
        listOf(
            "ALL" to if (isHindi) "🍽️ सभी सामान" else "🍽️ All",
            "FAST_FOOD" to if (isHindi) "🍕 फास्ट फूड" else "🍕 Fast Food",
            "SWEETS" to if (isHindi) "🍬 मिठाइयां" else "🍬 Sweets",
            "DRINKS" to if (isHindi) "🥤 पेय व चाय" else "🥤 Drinks",
            "GROCERY" to if (isHindi) "📦 किराना व अन्य" else "📦 Grocery"
        )
    }

    // Reactive Derived Cart State (Preserves quantity & bar visibility on back navigation from Checkout)
    val totalCartItems by remember {
        derivedStateOf { productsList.sumOf { it.cartQty } }
    }
    val totalCartPrice by remember {
        derivedStateOf { productsList.sumOf { it.price * it.cartQty } }
    }

    // Fetch initial batch of real products from Supabase RPC ONLY if not already cached in RAM
    LaunchedEffect(Unit) {
        if (!StoreMarketplaceCache.isDataLoaded || StoreMarketplaceCache.cachedProducts.isEmpty()) {
            isLoading = true
            try {
                val remoteDtos = remoteDataSource.fetchPublicMarketplaceProducts(
                    limit = 20,
                    offset = 0,
                    query = null,
                    categoryId = null
                )

                if (remoteDtos.isNotEmpty()) {
                    productsList.clear()
                    remoteDtos.forEach { dto ->
                        val merchantSubtitle = if (!dto.city.isNullOrBlank()) {
                            "by ${dto.merchantName} • ${dto.city}"
                        } else {
                            "by ${dto.merchantName}"
                        }

                        productsList.add(
                            MarketplaceProduct(
                                id = dto.itemId,
                                name = dto.itemName,
                                price = dto.sellingPrice,
                                unit = dto.unit ?: "Pcs",
                                merchantName = dto.merchantName,
                                distance = merchantSubtitle,
                                imageUrl = dto.imageUrl,
                                rating = "4.8",
                                discountBadge = null,
                                businessId = dto.businessId,
                                businessSlug = dto.businessSlug ?: "demo-store",
                                branchId = dto.branchId
                            )
                        )
                    }
                    StoreMarketplaceCache.isDataLoaded = true
                }
            } catch (_: Exception) {
                // Fail-safe
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    // Filtered Products based on Category Chips
    val displayedProducts by remember {
        derivedStateOf {
            when (selectedCategory) {
                "FAST_FOOD" -> productsList.filter {
                    it.name.contains("pizza", ignoreCase = true) ||
                            it.name.contains("dosa", ignoreCase = true) ||
                            it.name.contains("burger", ignoreCase = true) ||
                            it.name.contains("चटपटा", ignoreCase = true) ||
                            it.name.contains("पिज़्ज़ा", ignoreCase = true)
                }
                "SWEETS" -> productsList.filter {
                    it.name.contains("katli", ignoreCase = true) ||
                            it.name.contains("sweet", ignoreCase = true) ||
                            it.name.contains("bhujia", ignoreCase = true) ||
                            it.name.contains("काजू", ignoreCase = true) ||
                            it.name.contains("मिठाई", ignoreCase = true)
                }
                "DRINKS" -> productsList.filter {
                    it.name.contains("chay", ignoreCase = true) ||
                            it.name.contains("tea", ignoreCase = true) ||
                            it.name.contains("coffee", ignoreCase = true) ||
                            it.name.contains("shake", ignoreCase = true) ||
                            it.name.contains("चाय", ignoreCase = true)
                }
                "GROCERY" -> productsList.filter {
                    it.name.contains("oil", ignoreCase = true) ||
                            it.name.contains("packet", ignoreCase = true) ||
                            it.name.contains("rice", ignoreCase = true) ||
                            it.name.contains("atta", ignoreCase = true)
                }
                else -> productsList
            }
        }
    }

    // LazyGrid Endless Scroll Pagination Trigger
    val shouldLoadNextPage by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItem >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadNextPage) {
        if (shouldLoadNextPage && !isLoading && !isFetchingNextBatch && productsList.size >= 15) {
            isFetchingNextBatch = true
            try {
                val nextBatchDtos = remoteDataSource.fetchPublicMarketplaceProducts(
                    limit = 15,
                    offset = productsList.size,
                    query = null,
                    categoryId = null
                )
                if (nextBatchDtos.isNotEmpty()) {
                    nextBatchDtos.forEach { dto ->
                        val merchantSubtitle = if (!dto.city.isNullOrBlank()) {
                            "by ${dto.merchantName} • ${dto.city}"
                        } else {
                            "by ${dto.merchantName}"
                        }

                        productsList.add(
                            MarketplaceProduct(
                                id = dto.itemId,
                                name = dto.itemName,
                                price = dto.sellingPrice,
                                unit = dto.unit ?: "Pcs",
                                merchantName = dto.merchantName,
                                distance = merchantSubtitle,
                                imageUrl = dto.imageUrl,
                                businessId = dto.businessId,
                                businessSlug = dto.businessSlug ?: "demo-store",
                                branchId = dto.branchId
                            )
                        )
                    }
                }
            } catch (_: Exception) {
                // Ignore
            } finally {
                isFetchingNextBatch = false
            }
        }
    }

    // Soft Quiet Luxury Theme Colors (Identical to ProfileScreen.kt)
    val primaryTextColor = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val cardBgColor = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorderColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                top = 2.dp,
                bottom = if (totalCartItems > 0) 86.dp else 16.dp
            )
        ) {
            // --- 1. Active Order & 4-Digit OTP Banner (Span 2 Columns) ---
            if (!activeOrderOtp.isNullOrBlank()) {
                item(span = { GridItemSpan(2) }) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenLiveOtpOrder(activeOrderNumber ?: "") },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDark) Color(0xFF1E293B) else Color(0xFF0F172A),
                        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.18f) else Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.Bike,
                                        contentDescription = "Active Delivery",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = if (isHindi) "सक्रिय डिलीवरी ऑर्डर" else "Active Delivery Order",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${activeOrderNumber ?: "Order"} • ${if (isHindi) "ट्रैक करें" else "Tap to Track"}",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            // OTP Pill
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White
                            ) {
                                Text(
                                    text = "OTP: $activeOrderOtp",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // --- 2. Category Filter Chips Row (Span 2 Columns) ---
            item(span = { GridItemSpan(2) }) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { (key, label) ->
                        val isSelected = (selectedCategory == key)
                        Surface(
                            modifier = Modifier
                                .height(34.dp)
                                .clickable { selectedCategory = key },
                            shape = RoundedCornerShape(17.dp),
                            color = if (isSelected) {
                                if (isDark) Color.White else Color(0xFF0F172A)
                            } else {
                                if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9)
                            },
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) {
                                    if (isDark) Color.White else Color(0xFF0F172A)
                                } else {
                                    if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)
                                }
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        if (isDark) Color(0xFF0F172A) else Color.White
                                    } else {
                                        secondaryTextColor
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // --- 3. Section Title (Span 2 Columns) ---
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "उपलब्ध सामान व उत्पाद" else "Available Items & Dishes",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                    Text(
                        text = "${displayedProducts.size} ${if (isHindi) "उत्पाद" else "items"}",
                        fontSize = 11.5.sp,
                        color = secondaryTextColor
                    )
                }
            }

            // --- 4. 2-Column Luxury Grid Product Cards ---
            if (isLoading) {
                items(6) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ) {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            } else if (displayedProducts.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = cardBgColor,
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Package,
                                contentDescription = "No Products",
                                tint = secondaryTextColor.copy(alpha = 0.5f),
                                modifier = Modifier.size(38.dp)
                            )
                            Text(
                                text = if (isHindi) "इस श्रेणी में कोई उत्पाद नहीं मिला" else "No Items In This Category",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryTextColor
                            )
                            Text(
                                text = if (isHindi) "कृपया कोई अन्य श्रेणी चुनें" else "Try selecting another category chip above",
                                fontSize = 11.5.sp,
                                color = secondaryTextColor
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(displayedProducts, key = { _, item -> item.id }) { index, product ->
                    val globalIndex = productsList.indexOfFirst { it.id == product.id }

                    // Apple Quiet Luxury 2-Column Bento Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = cardBgColor,
                        border = BorderStroke(1.dp, cardBorderColor),
                        shadowElevation = if (isDark) 2.dp else 1.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Top: Large Aspect-Ratio HD Image Container (110dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9))
                            ) {
                                if (!product.imageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = product.imageUrl,
                                        contentDescription = product.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Lucide.ShoppingBag,
                                            contentDescription = "Product",
                                            tint = secondaryTextColor.copy(alpha = 0.4f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                // Soft Dark Gradient Overlay at Bottom of Image for legibility
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(32.dp)
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f))
                                            )
                                        )
                                )

                                // Rating Badge (Top Right Pill)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.55f),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(text = "★", fontSize = 9.sp, color = Color(0xFFFBBF24))
                                        Text(
                                            text = product.rating,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // Bottom: Item Details & Apple Stepper/Pill Button
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = product.name,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryTextColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = product.distance,
                                    fontSize = 10.sp,
                                    color = secondaryTextColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Price & Action Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "₹${product.price.toInt()}",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = primaryTextColor
                                        )
                                        Text(
                                            text = "/ ${product.unit}",
                                            fontSize = 9.5.sp,
                                            color = secondaryTextColor
                                        )
                                    }

                                    // Apple Quiet Luxury "+ ADD" Pill / Stepper
                                    if (product.cartQty == 0) {
                                        Surface(
                                            modifier = Modifier
                                                .height(28.dp)
                                                .clickable {
                                                    if (globalIndex != -1) {
                                                        productsList[globalIndex] = product.copy(cartQty = 1)
                                                    }
                                                },
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
                                            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFCBD5E1))
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(horizontal = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (isHindi) "+ जोड़ें" else "+ ADD",
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = primaryTextColor
                                                )
                                            }
                                        }
                                    } else {
                                        Surface(
                                            modifier = Modifier.height(28.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                                            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.24f) else Color(0xFFCBD5E1))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "-",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = primaryTextColor,
                                                    modifier = Modifier
                                                        .clickable {
                                                            if (globalIndex != -1) {
                                                                if (product.cartQty > 1) {
                                                                    productsList[globalIndex] = product.copy(cartQty = product.cartQty - 1)
                                                                } else {
                                                                    productsList[globalIndex] = product.copy(cartQty = 0)
                                                                }
                                                            }
                                                        }
                                                        .padding(horizontal = 2.dp)
                                                )

                                                Text(
                                                    text = "${product.cartQty}",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = primaryTextColor
                                                )

                                                Text(
                                                    text = "+",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = primaryTextColor,
                                                    modifier = Modifier
                                                        .clickable {
                                                            if (globalIndex != -1) {
                                                                productsList[globalIndex] = product.copy(cartQty = product.cartQty + 1)
                                                            }
                                                        }
                                                        .padding(horizontal = 2.dp)
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

            if (isFetchingNextBatch) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = primaryTextColor,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }

        // Apple Minimalist Matte Dark Bottom Floating Capsule Bar (Animated Visibility + 0ms Live State Sync)
        AnimatedVisibility(
            visible = totalCartItems > 0,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = if (isDark) Color(0xFF1E293B) else Color(0xFF0F172A),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFF334155)),
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCartCheckout() }
                        .padding(horizontal = 18.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "$totalCartItems ${if (isHindi) "सामान" else "Items"} • ₹${totalCartPrice.toInt()}",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isHindi) "कार्ट देखें व चेकआउट करें" else "View Cart & Checkout",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isHindi) "आगे बढ़ें" else "Proceed",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Lucide.ChevronRight,
                            contentDescription = "Proceed",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
