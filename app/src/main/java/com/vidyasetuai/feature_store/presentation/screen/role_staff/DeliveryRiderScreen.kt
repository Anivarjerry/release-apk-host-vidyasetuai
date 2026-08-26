package com.vidyasetuai.feature_store.presentation.screen.role_staff

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * ROLE 3 (Staff): Delivery Rider Portal.
 * Assigned delivery orders, navigation, OTP delivery verification.
 */
@Composable
fun DeliveryRiderScreen(
    isHindi: Boolean = false,
    isDark: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = if (isHindi) "डिलीवरी राइडर पोर्टल (Delivery Rider)" else "Delivery Rider Portal & OTP Verification")
    }
}
