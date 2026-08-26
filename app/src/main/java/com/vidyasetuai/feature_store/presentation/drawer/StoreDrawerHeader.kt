package com.vidyasetuai.feature_store.presentation.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Store
import com.vidyasetuai.core.ui.colors.AppColors

@Composable
fun StoreDrawerHeader(
    businessName: String? = null,
    roleTitle: String? = null,
    isHindi: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.EmeraldGreen.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AppColors.EmeraldGreen,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Lucide.Store,
                        contentDescription = "Store Profile",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = businessName ?: if (isHindi) "मार्केटप्लेस मोड" else "Public Store Mode",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = roleTitle ?: if (isHindi) "ग्राहक" else "Shopper",
                    fontSize = 12.sp,
                    color = AppColors.EmeraldGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
