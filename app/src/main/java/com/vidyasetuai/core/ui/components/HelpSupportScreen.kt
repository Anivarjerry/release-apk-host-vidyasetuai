package com.vidyasetuai.core.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors

@Composable
fun HelpSupportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }
    val isHindi = prefs.getString("language", "en") == "hi"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "मदद और सहायता" else "Help & Support",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Call Support
            SupportSectionHeader(
                title = if (isHindi) "कॉल सहायता" else "Call Support",
                icon = Lucide.Phone
            )
            
            SupportRowItem(
                icon = Lucide.PhoneCall,
                title = if (isHindi) "सहायता नंबर 1" else "Support Line 1",
                subtitle = "+91-7737088094",
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:7737088094"))
                    context.startActivity(intent)
                }
            )

            SupportRowItem(
                icon = Lucide.PhoneCall,
                title = if (isHindi) "सहायता नंबर 2" else "Support Line 2",
                subtitle = "+91-7340080094",
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:7340080094"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email Support
            SupportSectionHeader(
                title = if (isHindi) "ईमेल सहायता" else "Email Support",
                icon = Lucide.Mail
            )

            SupportRowItem(
                icon = Lucide.Inbox,
                title = if (isHindi) "सपोर्ट ईमेल" else "Support Email",
                subtitle = "support@vidyasetuai.com",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@vidyasetuai.com")
                    }
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Social & Community Channels
            SupportSectionHeader(
                title = if (isHindi) "सोशल मीडिया और चैनल्स" else "Social Media & Channels",
                icon = Lucide.Globe
            )

            SupportRowItem(
                icon = Lucide.MessageCircle,
                title = "WhatsApp Support Group",
                subtitle = if (isHindi) "हमारे व्हाट्सएप ग्रुप में शामिल हों" else "Join our community group",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/KingWyOnciSFOVUTE88j1Z"))
                    context.startActivity(intent)
                }
            )

            SupportRowItem(
                icon = Lucide.Instagram,
                title = "Instagram",
                subtitle = if (isHindi) "हमें इंस्टाग्राम पर फॉलो करें" else "Follow us on Instagram",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/vidyasetu_ai/"))
                    context.startActivity(intent)
                }
            )

            SupportRowItem(
                icon = Lucide.Linkedin,
                title = "LinkedIn",
                subtitle = if (isHindi) "लिंक्डइन पर हमारे साथ जुड़ें" else "Connect with us on LinkedIn",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/anivar-meedhari-975651417?utm_source=share_via&utm_content=profile&utm_medium=member_android"))
                    context.startActivity(intent)
                }
            )

            SupportRowItem(
                icon = Lucide.Youtube,
                title = "YouTube",
                subtitle = if (isHindi) "यूट्यूब पर सब्सक्राइब करें" else "Subscribe to our channel",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@VidyaSetuAI_Foundention"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SupportSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.EmeraldGreen,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen
        )
    }
}

@Composable
private fun SupportRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(AppColors.EmeraldGreen.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.EmeraldGreen,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
