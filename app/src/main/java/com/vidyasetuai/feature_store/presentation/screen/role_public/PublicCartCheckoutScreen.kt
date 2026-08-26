package com.vidyasetuai.feature_store.presentation.screen.role_public

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.auth.PermissionManager
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.Locale

enum class OrderFulfillmentType {
    DINE_IN,
    TAKEAWAY,
    DELIVERY
}

data class SearchedAddressItem(
    val title: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * ROLE 4 (Public Customer): Flagship Apple Minimalist Flat HIG Public Cart Checkout Screen.
 * 
 * Design System Compliance:
 * 1. Soft Monochromatic Quiet Luxury Palette (Slate Charcoal #1E293B, Soft Gray #F8FAFC / #F1F5F9).
 * 2. Apple Segmented Control for Order Fulfillment (Dine-In, Takeaway, Delivery).
 * 3. 48dp Squircle Product Thumbnails with 0ms Instant State Reactivity.
 * 4. Split Frosted Contact Details (+91 country code prefix with vector icons).
 * 5. Translucent Action Chips for GPS Auto-Detect & Map Location Picker.
 * 6. Receipt-style Bill Breakdown with Free Delivery & Payment Mode Cards.
 * 7. Safe `.navigationBarsPadding()` Floating Island Place Order Bar.
 * 8. Layered BackHandler Navigation (dismisses open dialogs before exiting).
 */
@Composable
fun PublicCartCheckoutScreen(
    isHindi: Boolean = false,
    isDark: Boolean = isSystemInDarkTheme(),
    onBack: () -> Unit = {},
    onOrderPlacedSuccess: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val remoteDataSource = remember { StoreRemoteDataSource() }

    val cartItems = remember { mutableStateListOf<MarketplaceProduct>() }

    // Load active items from cache on launch
    LaunchedEffect(Unit) {
        cartItems.clear()
        cartItems.addAll(StoreMarketplaceCache.cachedProducts.filter { it.cartQty > 0 })
    }

    var selectedFulfillment by remember { mutableStateOf(OrderFulfillmentType.DELIVERY) }

    // Form State Fields
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var tableNumber by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var specialInstructions by remember { mutableStateOf("") }

    // Location State
    var isGpsDetecting by remember { mutableStateOf(false) }
    var pinnedCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var showGpsDisabledAlert by remember { mutableStateOf(false) }
    var showLocationSearchDialog by remember { mutableStateOf(false) }

    var isPlacingOrder by remember { mutableStateOf(false) }

    val itemTotal = cartItems.sumOf { it.price * it.cartQty }
    val deliveryFee = 0.0
    val grandTotal = itemTotal + deliveryFee

    // Smart Form Validation: Checks required fields dynamically based on fulfillment type
    val isFormValid by remember(
        customerName, customerPhone, selectedFulfillment, tableNumber, deliveryAddress, cartItems.size
    ) {
        derivedStateOf {
            val hasValidName = customerName.trim().isNotBlank()
            val hasValidPhone = customerPhone.trim().length == 10 && customerPhone.trim().all { it.isDigit() }
            val hasValidFulfillment = when (selectedFulfillment) {
                OrderFulfillmentType.DINE_IN -> tableNumber.trim().isNotBlank()
                OrderFulfillmentType.DELIVERY -> deliveryAddress.trim().isNotBlank()
                OrderFulfillmentType.TAKEAWAY -> true
            }
            hasValidName && hasValidPhone && hasValidFulfillment && cartItems.isNotEmpty()
        }
    }

    // Layered BackHandler
    BackHandler(enabled = true) {
        if (showLocationSearchDialog) {
            showLocationSearchDialog = false
        } else if (showGpsDisabledAlert) {
            showGpsDisabledAlert = false
        } else {
            onBack()
        }
    }

    // ── Real GPS Retrieval & Reverse Geocoding Function ──
    val fetchRealGpsLocation: () -> Unit = {
        val isGpsOn = PermissionManager.isLocationServicesEnabled(context)
        if (!isGpsOn) {
            showGpsDisabledAlert = true
        } else {
            isGpsDetecting = true
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val processLocation: (Location) -> Unit = { loc ->
                    val lat = loc.latitude
                    val lng = loc.longitude
                    pinnedCoordinates = Pair(lat, lng)

                    // Background Reverse Geocoding via Android Geocoder
                    scope.launch(Dispatchers.IO) {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(lat, lng, 1)
                            val resolvedAddress = if (!addresses.isNullOrEmpty()) {
                                val addr = addresses[0]
                                val line = addr.getAddressLine(0)
                                    ?: "${addr.subLocality ?: ""}, ${addr.locality ?: ""}, ${addr.adminArea ?: ""}".trim().trim(',')
                                line
                            } else {
                                "Lat: %.5f, Lng: %.5f".format(lat, lng)
                            }

                            withContext(Dispatchers.Main) {
                                deliveryAddress = resolvedAddress
                                isGpsDetecting = false
                                Toast.makeText(
                                    context,
                                    if (isHindi) "स्थान सफलतापूर्वक प्राप्त हुआ!" else "Live GPS Location Detected!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                deliveryAddress = "Lat: %.5f, Lng: %.5f".format(lat, lng)
                                isGpsDetecting = false
                                Toast.makeText(
                                    context,
                                    if (isHindi) "GPS स्थान प्राप्त हुआ!" else "GPS Coordinates Received!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                // 1. Try Last Known Location
                val lastKnown = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                } else null

                if (lastKnown != null) {
                    processLocation(lastKnown)
                }

                // 2. Request Fresh GPS Update
                val provider = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    LocationManager.GPS_PROVIDER
                } else {
                    LocationManager.NETWORK_PROVIDER
                }

                locationManager.requestLocationUpdates(
                    provider,
                    0L,
                    0f,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            processLocation(location)
                            locationManager.removeUpdates(this)
                        }
                        override fun onStatusChanged(p: String?, s: Int, e: Bundle?) {}
                        override fun onProviderEnabled(p: String) {}
                        override fun onProviderDisabled(p: String) {}
                    },
                    Looper.getMainLooper()
                )

                // Timeout safe-stop after 8 seconds
                scope.launch {
                    delay(8000L)
                    if (isGpsDetecting) {
                        isGpsDetecting = false
                    }
                }
            } catch (e: SecurityException) {
                isGpsDetecting = false
                Toast.makeText(context, if (isHindi) "स्थान अनुमति नहीं है!" else "Location permission missing!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                isGpsDetecting = false
                Toast.makeText(context, "GPS Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Permission launcher configuration for GPS
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                fetchRealGpsLocation()
            } else {
                Toast.makeText(
                    context,
                    if (isHindi) "स्थान सेवा की अनुमति आवश्यक है!" else "Location permission is required!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    val startLocationRetrieval = {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            fetchRealGpsLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Color Palette Tokens
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val inputBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = cardBg,
                shadowElevation = 0.dp,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Apple Circular Back Button Pill
                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { onBack() },
                        shape = CircleShape,
                        color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Back",
                                tint = textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "चेकआउट (${cartItems.sumOf { it.cartQty }} सामान)" else "Checkout (${cartItems.sumOf { it.cartQty }} items)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.EmeraldGreen)
                            )
                            Text(
                                text = if (isHindi) "फास्ट चेकआउट • 3-मिनट में तैयार" else "Fast Checkout • Instant Order",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = textSecondary
                            )
                        }
                    }

                    // Cart item count badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, AppColors.EmeraldGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "₹${grandTotal.toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding() // Safe clearance from phone system 3-button / gesture bar
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.Transparent,
                    shadowElevation = if (isFormValid) 4.dp else 0.dp
                ) {
                    val activeGradient = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                    val disabledBackground = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .then(
                                if (isFormValid) Modifier.background(activeGradient)
                                else Modifier
                                    .background(disabledBackground)
                                    .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), RoundedCornerShape(22.dp))
                            )
                            .clickable(enabled = isFormValid && !isPlacingOrder) {
                                isPlacingOrder = true
                                scope.launch {
                                    try {
                                        // 1. Extract Merchant Slug from cart items or fallback
                                        val targetBusinessSlug = cartItems.firstOrNull()?.businessSlug?.ifBlank { null }
                                            ?: "sharma-sweets"

                                        // 2. Build verified items JSON payload
                                        val itemsJsonArr = buildJsonArray {
                                            cartItems.forEach { item ->
                                                add(buildJsonObject {
                                                    put("item_id", item.id)
                                                    put("quantity", item.cartQty)
                                                    put("name", item.name)
                                                    put("sale_price", item.price)
                                                })
                                            }
                                        }

                                        // 3. Build delivery address JSON payload if Delivery selected
                                        val addressJsonObj = if (selectedFulfillment == OrderFulfillmentType.DELIVERY) {
                                            buildJsonObject {
                                                put("address_line", deliveryAddress)
                                                pinnedCoordinates?.let { (lat, lng) ->
                                                    put("latitude", lat)
                                                    put("longitude", lng)
                                                }
                                            }
                                        } else null

                                        // 4. Map Fulfillment Type String
                                        val orderTypeString = when (selectedFulfillment) {
                                            OrderFulfillmentType.DINE_IN -> "DINE_IN"
                                            OrderFulfillmentType.TAKEAWAY -> "TAKEAWAY"
                                            OrderFulfillmentType.DELIVERY -> "DELIVERY"
                                        }

                                        // 5. Call Battle-Tested Production Supabase RPC `fn_place_public_customer_order`
                                        val orderResponse = remoteDataSource.placePublicCustomerOrder(
                                            businessSlug = targetBusinessSlug,
                                            customerName = customerName.trim(),
                                            customerPhone = customerPhone.trim(),
                                            orderType = orderTypeString,
                                            tableOrTokenNo = if (selectedFulfillment == OrderFulfillmentType.DINE_IN) tableNumber.trim() else null,
                                            deliveryAddressJson = addressJsonObj,
                                            cartItems = itemsJsonArr,
                                            orderNotes = specialInstructions.trim().ifBlank { null }
                                        )

                                        if (orderResponse.success && !orderResponse.orderNumber.isNullOrBlank()) {
                                            val realOrderNumber = orderResponse.orderNumber
                                            val realDeliveryOtp = orderResponse.deliveryOtp ?: "0000"

                                            // Clear Active Cart Items
                                            StoreMarketplaceCache.cachedProducts.forEach { item ->
                                                item.cartQty = 0
                                            }

                                            Toast.makeText(
                                                context,
                                                if (isHindi) "ऑर्डर सफलतापूर्वक भेजा गया! Delivery OTP: $realDeliveryOtp" else "Order Placed! OTP: $realDeliveryOtp",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            onOrderPlacedSuccess(realOrderNumber, realDeliveryOtp)
                                            onBack()
                                        } else {
                                            val errMsg = orderResponse.error ?: "Order failed"
                                            Toast.makeText(context, if (isHindi) "ऑर्डर एरर: $errMsg" else "Order Error: $errMsg", Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("StoreOrder", "Error placing order: ${e.message}", e)
                                        Toast.makeText(
                                            context,
                                            if (isHindi) "ऑर्डर नहीं हो सका: ${e.localizedMessage}" else "Order failed: ${e.localizedMessage}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } finally {
                                        isPlacingOrder = false
                                    }
                                }
                            }
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Place Order • ₹${grandTotal.toInt()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFormValid) Color.White else (if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8))
                                )
                                Text(
                                    text = if (!isFormValid) {
                                        if (isHindi) "ज़रूरी फ़ील्ड्स भरें (नाम, फ़ोन, पता) *" else "Fill required details to place order *"
                                    } else {
                                        if (isHindi) "कैश ऑन डिलीवरी / UPI • तुरंत पुष्टि" else "Cash on Delivery / UPI • Instant Dispatch"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (!isFormValid) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isFormValid) Color(0xFF94A3B8) else (if (isDark) Color(0xFF64748B) else Color(0xFF64748B))
                                )
                            }

                            if (isPlacingOrder) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isFormValid) AppColors.EmeraldGreen else (if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Lucide.ArrowRight,
                                            contentDescription = "Place Order",
                                            tint = if (isFormValid) Color.White else (if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Section 1: Apple Fluid Segmented Control (Dine-In, Takeaway, Delivery) ──
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isHindi) "ऑर्डर का प्रकार (Order Type)" else "Order Type",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // 1. Dine-In Tab
                        val isDineIn = selectedFulfillment == OrderFulfillmentType.DINE_IN
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedFulfillment = OrderFulfillmentType.DINE_IN },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDineIn) (if (isDark) Color(0xFF334155) else Color.White) else Color.Transparent,
                            border = BorderStroke(1.dp, if (isDineIn) (if (isDark) Color(0xFF475569) else Color(0xFFE2E8F0)) else Color.Transparent),
                            shadowElevation = if (isDineIn && !isDark) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.Utensils,
                                    contentDescription = "Dine-In",
                                    tint = if (isDineIn) (if (isDark) Color.White else Color(0xFF0F172A)) else textSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "डाइन-इन" else "Dine-In",
                                    fontSize = 12.sp,
                                    fontWeight = if (isDineIn) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isDineIn) (if (isDark) Color.White else Color(0xFF0F172A)) else textSecondary
                                )
                            }
                        }

                        // 2. Takeaway Tab
                        val isTakeaway = selectedFulfillment == OrderFulfillmentType.TAKEAWAY
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedFulfillment = OrderFulfillmentType.TAKEAWAY },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isTakeaway) (if (isDark) Color(0xFF334155) else Color.White) else Color.Transparent,
                            border = BorderStroke(1.dp, if (isTakeaway) (if (isDark) Color(0xFF475569) else Color(0xFFE2E8F0)) else Color.Transparent),
                            shadowElevation = if (isTakeaway && !isDark) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.Package,
                                    contentDescription = "Takeaway",
                                    tint = if (isTakeaway) (if (isDark) Color.White else Color(0xFF0F172A)) else textSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "टेकअवे" else "Takeaway",
                                    fontSize = 12.sp,
                                    fontWeight = if (isTakeaway) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTakeaway) (if (isDark) Color.White else Color(0xFF0F172A)) else textSecondary
                                )
                            }
                        }

                        // 3. Delivery Tab
                        val isDelivery = selectedFulfillment == OrderFulfillmentType.DELIVERY
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedFulfillment = OrderFulfillmentType.DELIVERY },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDelivery) (if (isDark) Color(0xFF334155) else Color.White) else Color.Transparent,
                            border = BorderStroke(1.dp, if (isDelivery) (if (isDark) Color(0xFF475569) else Color(0xFFE2E8F0)) else Color.Transparent),
                            shadowElevation = if (isDelivery && !isDark) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.Truck,
                                    contentDescription = "Delivery",
                                    tint = if (isDelivery) (if (isDark) Color.White else Color(0xFF0F172A)) else textSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "डिलीवरी" else "Delivery",
                                    fontSize = 12.sp,
                                    fontWeight = if (isDelivery) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isDelivery) (if (isDark) Color.White else Color(0xFF0F172A)) else textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // ── Section 2: Conditional Fulfillment Details (Location & Table) ──
            when (selectedFulfillment) {
                OrderFulfillmentType.DINE_IN -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = cardBg,
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Utensils,
                                    contentDescription = "Table",
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isHindi) "टेबल या टोकन नंबर *" else "Table / Token Number *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }

                            OutlinedTextField(
                                value = tableNumber,
                                onValueChange = { tableNumber = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, lineHeight = 18.sp, color = textPrimary),
                                placeholder = { Text(text = "e.g. Table 4, Counter T-12", fontSize = 12.sp, color = textSecondary) },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppColors.EmeraldGreen,
                                    unfocusedBorderColor = cardBorder,
                                    focusedContainerColor = inputBg,
                                    unfocusedContainerColor = inputBg
                                ),
                                singleLine = true
                            )
                        }
                    }
                }
                OrderFulfillmentType.DELIVERY -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = cardBg,
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.MapPin,
                                        contentDescription = "Location",
                                        tint = AppColors.EmeraldGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isHindi) "डिलीवरी का पता व लोकेशन *" else "Delivery Location & Address *",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (pinnedCoordinates != null) AppColors.EmeraldGreen.copy(alpha = 0.12f) else textSecondary.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, if (pinnedCoordinates != null) AppColors.EmeraldGreen.copy(alpha = 0.3f) else cardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(if (pinnedCoordinates != null) AppColors.EmeraldGreen else textSecondary)
                                        )
                                        Text(
                                            text = if (pinnedCoordinates != null) "Pinned" else "Not Pinned",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (pinnedCoordinates != null) AppColors.EmeraldGreen else textSecondary
                                        )
                                    }
                                }
                            }

                            // Clean Action Chips (Auto-Detect GPS & Pick on Map)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Real Auto-Detect GPS Button
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable(enabled = !isGpsDetecting) {
                                            startLocationRetrieval()
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, cardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isGpsDetecting) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(12.dp),
                                                color = AppColors.EmeraldGreen,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHindi) "खोज रहे हैं..." else "Detecting GPS...",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.EmeraldGreen
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Lucide.Navigation,
                                                contentDescription = "GPS",
                                                tint = AppColors.EmeraldGreen,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = if (isHindi) "Auto-Detect GPS" else "Auto-Detect GPS",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                            )
                                        }
                                    }
                                }

                                // Real "Pick on Map" Location Search Dialog Launcher
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            showLocationSearchDialog = true
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, cardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Lucide.MapPin,
                                            contentDescription = "Map Search",
                                            tint = Color(0xFF3B82F6),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = if (isHindi) "Pick on Map" else "Pick on Map",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                    }
                                }
                            }

                            // Address Input Box with 0% text cut-off
                            OutlinedTextField(
                                value = deliveryAddress,
                                onValueChange = { deliveryAddress = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, lineHeight = 18.sp, color = textPrimary),
                                placeholder = { Text(text = "House / Flat No., Street, Landmark, Area *", fontSize = 12.sp, color = textSecondary) },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppColors.EmeraldGreen,
                                    unfocusedBorderColor = cardBorder,
                                    focusedContainerColor = inputBg,
                                    unfocusedContainerColor = inputBg
                                ),
                                singleLine = false,
                                maxLines = 2
                            )
                        }
                    }
                }
                OrderFulfillmentType.TAKEAWAY -> {
                    // No address required for takeaway
                }
            }

            // ── Section 3: Customer Contact Details (Split Frosted Inputs) ──
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isHindi) "ग्राहक संपर्क विवरण (Contact Details)" else "Contact Details",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Name Field
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = cardBg,
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.User,
                                    contentDescription = "Name",
                                    tint = textSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isHindi) "नाम *" else "Name *",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, lineHeight = 18.sp, color = textPrimary),
                                placeholder = { Text(text = "Ramesh Kumar", fontSize = 12.sp, color = textSecondary) },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppColors.EmeraldGreen,
                                    unfocusedBorderColor = cardBorder,
                                    focusedContainerColor = inputBg,
                                    unfocusedContainerColor = inputBg
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Mobile Field with +91 Country Code
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = cardBg,
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Phone,
                                    contentDescription = "Phone",
                                    tint = textSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isHindi) "मोबाइल *" else "Mobile *",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { 
                                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                        customerPhone = it
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, lineHeight = 18.sp, color = textPrimary),
                                placeholder = { Text(text = "9876543210", fontSize = 12.sp, color = textSecondary) },
                                leadingIcon = {
                                    Text(
                                        text = "+91",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textSecondary,
                                        modifier = Modifier.padding(start = 6.dp)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppColors.EmeraldGreen,
                                    unfocusedBorderColor = cardBorder,
                                    focusedContainerColor = inputBg,
                                    unfocusedContainerColor = inputBg
                                ),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // ── Section 4: ORDER ITEMS Card with 48dp Squircle Thumbnails & 0ms Stepper ──
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "ऑर्डर के सामान" else "Order Items",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                    Text(
                        text = "${cartItems.sumOf { it.cartQty }} items",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        cartItems.forEachIndexed { index, product ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // 48dp Squircle Product Image
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, cardBorder)
                                ) {
                                    if (!product.imageUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = product.imageUrl,
                                            contentDescription = product.name,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Lucide.ShoppingBag,
                                                contentDescription = "Item",
                                                tint = textSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                // Product Title & Unit Price
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹${product.price.toInt()} × ${product.cartQty} = ₹${(product.price * product.cartQty).toInt()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textSecondary
                                    )
                                }

                                // Modern Apple Capsule Stepper [ − 1 + ]
                                Surface(
                                    modifier = Modifier.height(30.dp),
                                    shape = CircleShape,
                                    color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, cardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    val newQty = product.cartQty - 1
                                                    if (newQty <= 0) {
                                                        cartItems.removeAt(index)
                                                        val cacheIdx = StoreMarketplaceCache.cachedProducts.indexOfFirst { it.id == product.id }
                                                        if (cacheIdx != -1) {
                                                            StoreMarketplaceCache.cachedProducts[cacheIdx] = StoreMarketplaceCache.cachedProducts[cacheIdx].copy(cartQty = 0)
                                                        }
                                                    } else {
                                                        cartItems[index] = product.copy(cartQty = newQty)
                                                        val cacheIdx = StoreMarketplaceCache.cachedProducts.indexOfFirst { it.id == product.id }
                                                        if (cacheIdx != -1) {
                                                            StoreMarketplaceCache.cachedProducts[cacheIdx] = StoreMarketplaceCache.cachedProducts[cacheIdx].copy(cartQty = newQty)
                                                        }
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "−",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                            )
                                        }

                                        Text(
                                            text = "${product.cartQty}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    val newQty = product.cartQty + 1
                                                    cartItems[index] = product.copy(cartQty = newQty)
                                                    val cacheIdx = StoreMarketplaceCache.cachedProducts.indexOfFirst { it.id == product.id }
                                                    if (cacheIdx != -1) {
                                                        StoreMarketplaceCache.cachedProducts[cacheIdx] = StoreMarketplaceCache.cachedProducts[cacheIdx].copy(cartQty = newQty)
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "+",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.EmeraldGreen
                                            )
                                        }
                                    }
                                }
                            }

                            if (index < cartItems.lastIndex) {
                                Divider(
                                    color = cardBorder,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }

            // ── Section 5: Special Cooking / Delivery Instructions ──
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = cardBg,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.FileText,
                            contentDescription = "Notes",
                            tint = textSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isHindi) "विशेष निर्देश (Special Instructions Optional)" else "Special Instructions (Optional)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary
                        )
                    }

                    OutlinedTextField(
                        value = specialInstructions,
                        onValueChange = { specialInstructions = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, lineHeight = 18.sp, color = textPrimary),
                        placeholder = { Text(text = "e.g. Less spicy, send extra napkins, call upon arrival...", fontSize = 12.sp, color = textSecondary) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.EmeraldGreen,
                            unfocusedBorderColor = cardBorder,
                            focusedContainerColor = inputBg,
                            unfocusedContainerColor = inputBg
                        ),
                        singleLine = true
                    )
                }
            }

            // ── Section 6: Receipt Style Bill Breakdown & Payment Method ──
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = cardBg,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isHindi) "बिल सारांश व भुगतान (Bill Details)" else "Bill Details",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    // Subtotal Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "सामान का कुल मूल्य (Items Total)" else "Items Subtotal",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                        Text(
                            text = "₹${itemTotal.toInt()}.00",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                    }

                    // Delivery Fee Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "डिलीवरी शुल्क (Delivery Fee)" else "Delivery Fee",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, AppColors.EmeraldGreen.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "FREE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.EmeraldGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Payment Mode Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "भुगतान का प्रकार" else "Payment Method",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                        Text(
                            text = "💵 Cash / UPI on Delivery",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }

                    Divider(
                        color = cardBorder,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Grand Total Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "कुल देय राशि (Total To Pay)" else "Total To Pay",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                        Text(
                            text = "₹${grandTotal.toInt()}.00",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppColors.EmeraldGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    // ── Dialog 1: GPS Disabled Alert Dialog ──
    if (showGpsDisabledAlert) {
        AlertDialog(
            onDismissRequest = { showGpsDisabledAlert = false },
            title = {
                Text(
                    text = if (isHindi) "जीपीएस बंद है" else "GPS is Disabled",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = if (isHindi)
                        "सटीक डिलीवरी स्थान प्राप्त करने के लिए कृपया जीपीएस / लोकेशन सेवाएं चालू करें।"
                    else
                        "Please enable GPS / Location services in order to detect your exact delivery address.",
                    color = textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showGpsDisabledAlert = false
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                ) {
                    Text(
                        text = if (isHindi) "सेटिंग्स खोलें" else "Open Settings",
                        color = AppColors.EmeraldGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showGpsDisabledAlert = false }) {
                    Text(text = if (isHindi) "रद्द करें" else "Cancel", color = textSecondary)
                }
            },
            containerColor = cardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ── Dialog 2: Location Search & Map Picker Dialog ("Pick on Map") ──
    if (showLocationSearchDialog) {
        LocationSearchPickerDialog(
            isHindi = isHindi,
            isDark = isDark,
            onDismiss = { showLocationSearchDialog = false },
            onLocationSelected = { addressItem ->
                deliveryAddress = addressItem.fullAddress
                pinnedCoordinates = Pair(addressItem.latitude, addressItem.longitude)
                showLocationSearchDialog = false
                Toast.makeText(
                    context,
                    if (isHindi) "स्थान चुना गया: ${addressItem.title}" else "Location Selected: ${addressItem.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}

/**
 * Real Location Search & Map Picker Dialog using Android Geocoder.
 * Allows searching any city, area, landmark or colony and returns real latitude/longitude + address.
 */
@Composable
fun LocationSearchPickerDialog(
    isHindi: Boolean = false,
    isDark: Boolean = false,
    onDismiss: () -> Unit = {},
    onLocationSelected: (SearchedAddressItem) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val searchResults = remember { mutableStateListOf<SearchedAddressItem>() }

    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val inputBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)

    // Real-time Search using Geocoder with 400ms debounce
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            isSearching = true
            delay(400)
            scope.launch(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val list = geocoder.getFromLocationName(searchQuery, 6)
                    val formatted = list?.mapNotNull { addr ->
                        val line = addr.getAddressLine(0) ?: "${addr.featureName ?: ""}, ${addr.locality ?: ""}".trim().trim(',')
                        val title = addr.featureName ?: addr.subLocality ?: addr.locality ?: "Location"
                        SearchedAddressItem(
                            title = title,
                            fullAddress = line,
                            latitude = addr.latitude,
                            longitude = addr.longitude
                        )
                    } ?: emptyList()

                    withContext(Dispatchers.Main) {
                        searchResults.clear()
                        searchResults.addAll(formatted)
                        isSearching = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isSearching = false
                    }
                }
            }
        } else {
            searchResults.clear()
            isSearching = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(22.dp),
            color = cardBg,
            border = BorderStroke(1.dp, cardBorder),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.MapPin,
                                    contentDescription = "Pin",
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isHindi) "लोकेशन खोजें (Pick on Map)" else "Search & Pick Location",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Lucide.X, contentDescription = "Close", tint = textSecondary)
                    }
                }

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = textPrimary),
                    placeholder = { Text(text = if (isHindi) "इलाका, लैंडमार्क या शहर लिखें..." else "Search area, landmark or city...", fontSize = 12.sp, color = textSecondary) },
                    leadingIcon = {
                        Icon(imageVector = Lucide.Search, contentDescription = "Search", tint = AppColors.EmeraldGreen, modifier = Modifier.size(16.dp))
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.EmeraldGreen,
                        unfocusedBorderColor = cardBorder,
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg
                    ),
                    singleLine = true
                )

                // Results Feed
                if (isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.EmeraldGreen, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                } else if (searchResults.isEmpty() && searchQuery.length >= 3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isHindi) "कोई स्थान नहीं मिला" else "No location found",
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    }
                } else if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isHindi) "सर्च करने के लिए कम से कम 3 अक्षर लिखें" else "Type at least 3 characters to search",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { item ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onLocationSelected(item) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, cardBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.MapPin,
                                        contentDescription = "Pin",
                                        tint = AppColors.EmeraldGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = item.fullAddress,
                                            fontSize = 11.sp,
                                            color = textSecondary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
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
