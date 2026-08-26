package com.vidyasetuai.feature_store.presentation.screen.role_staff.delivery.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.domain.model.DriverDeliveryOrderUiModel

@Composable
fun DriverDeliveryCard(
    order: DriverDeliveryOrderUiModel,
    isHindi: Boolean = false,
    onOpenOtpSheet: (DriverDeliveryOrderUiModel) -> Unit
) {
    val context = LocalContext.current
    val isCod = order.paymentMethod in listOf("CASH_ON_DELIVERY", "COD", "CASH")
    val isDelivered = order.orderStatus == "DELIVERED"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- 1. Top Header: Order Number & Delivery Status Badge ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AppColors.EmeraldGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "#${order.orderNumber}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "• ${order.itemCount} ${if (isHindi) "सामान" else "items"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Payment Status Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCod) Color(0xFFF59E0B).copy(alpha = 0.15f) else AppColors.EmeraldGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isCod) (if (isHindi) "💵 COD: ₹${"%.2f".format(order.grandTotal)}" else "💵 COD: ₹${"%.2f".format(order.grandTotal)}")
                        else (if (isHindi) "🟢 भुगतान हो चुका (₹${"%.2f".format(order.grandTotal)})" else "🟢 PAID (₹${"%.2f".format(order.grandTotal)})"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCod) Color(0xFFD97706) else AppColors.EmeraldGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // --- 2. Customer Profile & 1-Tap Phone Call Button ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.customerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "📞 ${order.customerPhone}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (order.customerPhone.isNotBlank() && order.customerPhone != "N/A") {
                    FilledTonalIconButton(
                        onClick = { launchPhoneCall(context, order.customerPhone) },
                        modifier = Modifier.size(38.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                            contentColor = AppColors.EmeraldGreen
                        )
                    ) {
                        Icon(
                            imageVector = Lucide.Phone,
                            contentDescription = "Call Customer",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // --- 3. Delivery Address Container ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Lucide.MapPin,
                        contentDescription = "Address",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Column {
                        Text(
                            text = if (isHindi) "डिलीवरी का पता:" else "Delivery Address:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = order.deliveryAddress,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // --- 4. Order Items Summary ---
            if (order.itemsSummary.isNotBlank()) {
                Text(
                    text = "📦 ${order.itemsSummary}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            // --- 5. Action Buttons (Google Maps Intent & OTP Handover) ---
            if (!isDelivered) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Button 1: 100% Free Google Maps Turn-by-Turn GPS Navigation
                    OutlinedButton(
                        onClick = { launchGoogleMapsNavigation(context, order.latitude, order.longitude, order.deliveryAddress) },
                        modifier = Modifier
                            .weight(1.1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.EmeraldGreen)
                    ) {
                        Icon(
                            imageVector = Lucide.Navigation,
                            contentDescription = "Map",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "मैप नेविगेशन" else "Google Maps",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Button 2: OTP Verification & Handover
                    Button(
                        onClick = { onOpenOtpSheet(order) },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen)
                    ) {
                        Icon(
                            imageVector = Lucide.KeyRound,
                            contentDescription = "OTP",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "OTP दर्ज करें" else "Enter OTP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Delivered Badge
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = AppColors.EmeraldGreen.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = "Delivered",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "सफलतापूर्वक डिलीवर हो चुका है" else "Successfully Delivered",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen
                        )
                    }
                }
            }
        }
    }
}

/**
 * 100% Free Turn-by-Turn GPS Voice Navigation using Android Native Google Maps Intent.
 * Zero API Key / Billing Cost.
 */
private fun launchGoogleMapsNavigation(
    context: Context,
    lat: Double?,
    lng: Double?,
    address: String
) {
    try {
        val uri = if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
            Uri.parse("google.navigation:q=$lat,$lng&mode=d")
        } else {
            val encodedAddress = Uri.encode(address)
            Uri.parse("google.navigation:q=$encodedAddress&mode=d")
        }

        val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback to web browser maps
            val fallbackUri = if (lat != null && lng != null) {
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
            } else {
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(address)}")
            }
            context.startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open Google Maps", Toast.LENGTH_SHORT).show()
    }
}

private fun launchPhoneCall(context: Context, phone: String) {
    try {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(callIntent)
    } catch (_: Exception) {
        Toast.makeText(context, "Unable to open dialer", Toast.LENGTH_SHORT).show()
    }
}
