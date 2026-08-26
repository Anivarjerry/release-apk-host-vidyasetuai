package com.vidyasetuai.feature_store.presentation.screen.role_public

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ROLE 4 (Public Customer): Apple-Grade Flat Minimalist Fullscreen Dedicated Search Screen.
 * Opened when user taps the Search icon (🔍) in Top App Bar.
 * 0% heavy shadows, 0% glassmorphism, sleek pill search bar, and flat product cards with hairline borders.
 */
@Composable
fun PublicStoreSearchScreen(
    isHindi: Boolean = false,
    onBack: () -> Unit = {},
    onOpenCartCheckout: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Synchronous keyboard-first dismissal handler (Zero lingering keyboard lag)
    val handleClose = {
        keyboardController?.hide()
        focusManager.clearFocus()
        onBack()
    }

    // Intercept System/Hardware Back Gesture to close keyboard + search screen synchronously
    BackHandler(enabled = true) {
        handleClose()
    }

    val scope = rememberCoroutineScope()
    val remoteDataSource = remember { StoreRemoteDataSource() }
    val focusRequester = remember { FocusRequester() }

    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val searchResults = remember { mutableStateListOf<MarketplaceProduct>() }

    var totalCartItems by remember { mutableStateOf(0) }
    var totalCartPrice by remember { mutableStateOf(0.0) }

    // Auto-focus keyboard on screen launch (WhatsApp style)
    LaunchedEffect(Unit) {
        delay(200)
        try {
            focusRequester.requestFocus()
        } catch (e: Exception) {
            // Safe focus request
        }
    }

    // 300ms Debounced Real-time Search calling Supabase RPC fn_fetch_public_marketplace_products
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            isLoading = true
            delay(300) // 300ms Debounce

            scope.launch {
                try {
                    val dtos = remoteDataSource.fetchPublicMarketplaceProducts(
                        limit = 20,
                        offset = 0,
                        query = searchQuery.ifBlank { null },
                        categoryId = null
                    )

                    searchResults.clear()
                    dtos.forEach { dto ->
                        val merchantSubtitle = if (!dto.city.isNullOrBlank()) {
                            "by ${dto.merchantName} • ${dto.city}"
                        } else {
                            "by ${dto.merchantName}"
                        }

                        searchResults.add(
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
                } catch (e: Exception) {
                    // Ignore
                } finally {
                    isLoading = false
                }
            }
        } else {
            searchResults.clear()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // WhatsApp-style Back Arrow (<-)
                    IconButton(onClick = handleClose) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Sleek Minimalist Pill Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        placeholder = {
                            Text(
                                text = if (isHindi) "सामान..." else "Search...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Lucide.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Lucide.X,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        singleLine = true
                    )
                }
            }
        },
        bottomBar = {
            if (totalCartItems > 0) {
                // Apple Minimalist Matte Dark Bottom Capsule Bar (0% heavy shadows)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF111111),
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenCartCheckout() }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "$totalCartItems ${if (isHindi) "सामान" else "Items"} • ₹${totalCartPrice.toInt()}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isHindi) "कार्ट देखें व ऑर्डर करें" else "View Cart & Checkout",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isHindi) "आगे बढ़ें" else "Proceed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Lucide.ChevronRight,
                                contentDescription = "Next",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Results Feed / Placeholder
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AppColors.EmeraldGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else if (searchResults.isEmpty() && searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.SearchX,
                            contentDescription = "No Results",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (isHindi) "\"$searchQuery\" से कोई उत्पाद नहीं मिला" else "No products found for \"$searchQuery\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isHindi) "उत्पाद खोजने के लिए नाम लिखें..." else "Type a product name to search...",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(searchResults) { index, product ->
                        // Apple Flat Minimalist Product Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    if (!product.imageUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = product.imageUrl,
                                            contentDescription = product.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(10.dp))
                                        )
                                    } else {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Lucide.Package,
                                                contentDescription = "Product",
                                                tint = AppColors.EmeraldGreen,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = product.distance,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "₹${product.price.toInt()}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AppColors.EmeraldGreen
                                        )
                                        Text(
                                            text = "/ ${product.unit}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                // Apple Compact Outlined "+ ADD" Pill Button
                                if (product.cartQty == 0) {
                                    Surface(
                                        modifier = Modifier
                                            .height(30.dp)
                                            .clickable {
                                                searchResults[index] = product.copy(cartQty = 1)
                                                totalCartItems += 1
                                                totalCartPrice += product.price
                                            },
                                        shape = CircleShape,
                                        color = AppColors.EmeraldGreen.copy(alpha = 0.08f),
                                        border = BorderStroke(1.dp, AppColors.EmeraldGreen.copy(alpha = 0.6f))
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isHindi) "+ जोड़ें" else "+ ADD",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = AppColors.EmeraldGreen
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        modifier = Modifier.height(30.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "-",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier
                                                    .clickable {
                                                        if (product.cartQty > 1) {
                                                            searchResults[index] = product.copy(cartQty = product.cartQty - 1)
                                                            totalCartItems -= 1
                                                            totalCartPrice -= product.price
                                                        } else {
                                                            searchResults[index] = product.copy(cartQty = 0)
                                                            totalCartItems -= 1
                                                            totalCartPrice -= product.price
                                                        }
                                                    }
                                                    .padding(horizontal = 4.dp)
                                            )

                                            Text(
                                                text = "${product.cartQty}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Text(
                                                text = "+",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.EmeraldGreen,
                                                modifier = Modifier
                                                    .clickable {
                                                        searchResults[index] = product.copy(cartQty = product.cartQty + 1)
                                                        totalCartItems += 1
                                                        totalCartPrice += product.price
                                                    }
                                                    .padding(horizontal = 4.dp)
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
    }
}
