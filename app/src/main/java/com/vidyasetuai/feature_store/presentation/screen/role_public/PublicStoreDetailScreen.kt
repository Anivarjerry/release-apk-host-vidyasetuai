package com.vidyasetuai.feature_store.presentation.screen.role_public

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors

data class CatalogItem(val id: String, val name: String, val price: Double, val unit: String, var cartQty: Int = 0)

@Composable
fun PublicStoreDetailScreen(
    storeId: String = "1",
    isHindi: Boolean = false,
    onBack: () -> Unit = {},
    onOpenCartCheckout: () -> Unit = {}
) {
    var cartItemsCount by remember { mutableStateOf(0) }
    var cartTotalAmount by remember { mutableStateOf(0.0) }

    val mockItems = remember {
        mutableStateListOf(
            CatalogItem("101", "Gulab Jamun Box (500g)", 220.0, "500g"),
            CatalogItem("102", "Special Kaju Katli (250g)", 350.0, "250g"),
            CatalogItem("103", "Paneer Samosa (2 Pcs)", 40.0, "Plate"),
            CatalogItem("104", "Masala Dosa with Sambhar", 120.0, "Plate")
        )
    }

    Scaffold(
        bottomBar = {
            if (cartItemsCount > 0) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.EmeraldGreen,
                    shadowElevation = 6.dp
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
                                text = "$cartItemsCount ${if (isHindi) "सामान" else "Items"} • ₹${cartTotalAmount.toInt()}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isHindi) "कार्ट देखें व ऑर्डर करें" else "View Cart & Checkout",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Lucide.ArrowLeft, contentDescription = "Back")
                }
                Column {
                    Text(
                        text = "Sharma Sweets & Fast Food",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Sweets, Bakery & Snacks • Main Branch",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = if (isHindi) "उपलब्ध सामान (Store Catalog)" else "Store Items & Products",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Items List
            mockItems.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₹${item.price.toInt()} / ${item.unit}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.EmeraldGreen
                            )
                        }

                        // Add to Cart Stepper
                        if (item.cartQty == 0) {
                            Button(
                                onClick = {
                                    mockItems[index] = item.copy(cartQty = 1)
                                    cartItemsCount += 1
                                    cartTotalAmount += item.price
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.EmeraldGreen
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "+ जोड़ें" else "+ ADD",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable {
                                            if (item.cartQty > 1) {
                                                mockItems[index] = item.copy(cartQty = item.cartQty - 1)
                                                cartItemsCount -= 1
                                                cartTotalAmount -= item.price
                                            } else {
                                                mockItems[index] = item.copy(cartQty = 0)
                                                cartItemsCount -= 1
                                                cartTotalAmount -= item.price
                                            }
                                        },
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = "${item.cartQty}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Surface(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable {
                                            mockItems[index] = item.copy(cartQty = item.cartQty + 1)
                                            cartItemsCount += 1
                                            cartTotalAmount += item.price
                                        },
                                    shape = CircleShape,
                                    color = AppColors.EmeraldGreen
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
