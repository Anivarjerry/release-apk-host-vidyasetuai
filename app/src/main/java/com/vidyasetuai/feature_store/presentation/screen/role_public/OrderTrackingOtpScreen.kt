package com.vidyasetuai.feature_store.presentation.screen.role_public

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors

/**
 * ROLE 4 (Public Customer): Order Status & Live 4-Digit Delivery OTP Screen.
 */
@Composable
fun OrderTrackingOtpScreen(
    orderNumber: String = "ORD-984210",
    deliveryOtp: String = "4892",
    isHindi: Boolean = false,
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Lucide.ArrowLeft, contentDescription = "Back")
            }
            Text(
                text = if (isHindi) "लाइव ऑर्डर स्टेटस व OTP" else "Live Order Status & OTP",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Live 4-Digit Delivery OTP Display Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.EmeraldGreen.copy(alpha = 0.12f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                color = AppColors.EmeraldGreen
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isHindi) "डिलीवरी सत्यापन कोड (OTP)" else "Share this Delivery OTP with Rider",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Big 4-Digit OTP Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AppColors.EmeraldGreen
                ) {
                    Text(
                        text = deliveryOtp,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 8.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                    )
                }

                Text(
                    text = if (isHindi) "ड्राइवर को केवल डिलीवरी मिलते समय OTP बताएं" else "Give this 4-digit code to the delivery rider when receiving items",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Order Progress Status Tracker
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "$orderNumber • Status Timeline",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Lucide.Check, contentDescription = "Done", tint = AppColors.EmeraldGreen)
                    Text(text = if (isHindi) "ऑर्डर स्वीकार किया गया" else "Order Confirmed by Store", fontSize = 13.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Lucide.Check, contentDescription = "Done", tint = AppColors.EmeraldGreen)
                    Text(text = if (isHindi) "सामान पैक हो रहा है" else "Items Packed & Ready", fontSize = 13.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Lucide.Truck, contentDescription = "On the way", tint = AppColors.EmeraldGreen)
                    Text(
                        text = if (isHindi) "डिलीवरी बॉय रास्ते में है" else "Rider Out for Delivery",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                }
            }
        }
    }
}
