package com.vidyasetuai.feature_auth.presentation.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bug
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Headphones
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.Trash2
import com.vidyasetuai.core.ui.colors.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndLegalBottomSheet(
    onDismiss: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenAbout: () -> Unit,
    onRequestAccountDeletion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppColors.EmeraldGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.Headphones,
                    contentDescription = "Help & Support",
                    tint = AppColors.EmeraldGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Help & Legal Center",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Compliance, Support & Account Policies",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Terms & Privacy Policy
            HelpOptionItem(
                icon = Lucide.FileText,
                iconBgColor = AppColors.EmeraldGreen.copy(alpha = 0.1f),
                iconTint = AppColors.EmeraldGreen,
                title = "Terms & Privacy Policy",
                subtitle = "Read DPDP & IT Act legal compliance",
                onClick = {
                    onDismiss()
                    onOpenTerms()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. WhatsApp Direct Support
            HelpOptionItem(
                icon = Lucide.MessageCircle,
                iconBgColor = Color(0xFF25D366).copy(alpha = 0.12f),
                iconTint = Color(0xFF25D366),
                title = "WhatsApp Support",
                subtitle = "Chat directly with customer support",
                onClick = {
                    onDismiss()
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917340080094?text=Hello%20VidyaSetu%20AI%20Support"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Could not launch WhatsApp support", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Report a Bug & Email
            HelpOptionItem(
                icon = Lucide.Bug,
                iconBgColor = Color(0xFF3B82F6).copy(alpha = 0.12f),
                iconTint = Color(0xFF3B82F6),
                title = "Report a Bug & Feedback",
                subtitle = "Send crash reports & suggestions via email",
                onClick = {
                    onDismiss()
                    try {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@vidyasetuai.com")
                            putExtra(Intent.EXTRA_SUBJECT, "VidyaSetu AI Support & Bug Report")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Could not open email client", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Request Account Deletion (Google Play Compliance)
            HelpOptionItem(
                icon = Lucide.Trash2,
                iconBgColor = Color(0xFFEF4444).copy(alpha = 0.12f),
                iconTint = Color(0xFFEF4444),
                title = "Request Account Deletion",
                subtitle = "Submit unauthenticated account deletion request",
                onClick = {
                    onDismiss()
                    onRequestAccountDeletion()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5. About & App Version
            HelpOptionItem(
                icon = Lucide.Info,
                iconBgColor = MaterialTheme.colorScheme.surfaceVariant,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                title = "About VidyaSetu AI",
                subtitle = "Institutional OS • Version 1.0.0",
                onClick = {
                    onDismiss()
                    onOpenAbout()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HelpOptionItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Lucide.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
    }
}
