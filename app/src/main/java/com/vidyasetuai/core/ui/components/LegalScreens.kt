package com.vidyasetuai.core.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }
    val isHindi = prefs.getString("language", "en") == "hi"
    val isDark = isSystemInDarkTheme()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LegalTopBar(
                title = if (isHindi) "हमारे बारे में" else "About VidyaSetu AI",
                subtitle = if (isHindi) "संस्थागत ऑपरेटिंग सिस्टम" else "Institutional Operating System",
                icon = Lucide.Sparkles,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Core Vision
            LegalSectionHeader(
                title = if (isHindi) "मूल विज़न (Core Vision)" else "Core Vision",
                icon = Lucide.Target
            )
            Text(
                text = if (isHindi) {
                    "VidyasetuAI शैक्षणिक संगठनों के लिए एक केंद्रीय ऑपरेटिंग सिस्टम के रूप में कार्य करता है। यह एक मानकीकृत वैश्विक शैक्षणिक डेटाबेस परत को परिभाषित करता है जिसे किसी भी स्कूल, कॉलेज या कोचिंग संस्थान द्वारा जोड़ा जा सकता है, जबकि उनके व्यक्तिगत परिचालन डेटा को पूरी तरह से सुरक्षित और अलग रखा जाता है।"
                } else {
                    "VidyasetuAI acts as a central operating system for educational organizations. It defines a standardized global academic database layer that can be plugged in by any school, college, or coaching institute while maintaining isolated tenant operational data."
                },
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 28.dp, bottom = 24.dp)
            )

            // Pricing Plans
            LegalSectionHeader(
                title = if (isHindi) "विशेष मूल्य निर्धारण और ऑफर" else "Exclusive Pricing & Offers",
                icon = Lucide.Sparkles
            )
            Column(
                modifier = Modifier
                    .padding(start = 28.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Plan 1: Institute Plan
                PricingCard(
                    title = if (isHindi) "संस्थान योजना (अधिकतम 10 स्कूल/कोचिंग)" else "Institute Plan (Up to 10 Schools/Coachings)",
                    originalPrice = "₹1,49,999",
                    offerPrice = if (isHindi) "₹89,999 / वर्ष" else "₹89,999 / Year",
                    description = if (isHindi) {
                        "★ प्रीमियम माइग्रेशन ऑफर: अधिकतम पिछले 5 वर्षों का संपूर्ण डेटा माइग्रेशन (यदि CSV उपलब्ध हो) और वर्तमान सत्र डेटा का ऑनबोर्डिंग बिना किसी अतिरिक्त शुल्क के शामिल है।"
                    } else {
                        "★ Premium Migration Offer: Complete data migration of up to the last 5 years (if CSV is available) plus onboarding for the current session data is included at zero cost."
                    },
                    isPremium = true,
                    isDark = isDark
                )

                // Plan 2: Single Org Plan
                PricingCard(
                    title = if (isHindi) "एकल संगठन योजना" else "Single Organization Plan",
                    originalPrice = "₹49,999",
                    offerPrice = if (isHindi) "₹39,999 / वर्ष" else "₹39,999 / Year",
                    description = if (isHindi) {
                        "एकल स्कूलों या व्यक्तिगत कोचिंग सेंटरों के संचालन को पूरी तरह से ऑटोमेट करने के लिए सर्वोत्तम योजना।"
                    } else {
                        "Perfect for standalone schools or individual coaching centres to completely automate operations."
                    },
                    isPremium = false,
                    isDark = isDark
                )
            }

            // Leadership Team
            LegalSectionHeader(
                title = if (isHindi) "नेतृत्व टीम (Leadership Team)" else "Leadership Team",
                icon = Lucide.Users
            )
            Column(
                modifier = Modifier
                    .padding(start = 28.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column {
                    Text(
                        text = if (isHindi) "संस्थापक" else "Founder",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                    Text(
                        text = "AnivaR Meedhari",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column {
                    Text(
                        text = if (isHindi) "सह-संस्थापक" else "Co-Founders",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                    Text(
                        text = "Jaswant Suthar\nRohitash Suthar\nSunil Suthar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Contact Info
            LegalSectionHeader(
                title = if (isHindi) "संपर्क विवरण" else "Contact Info",
                icon = Lucide.Mail
            )
            Column(
                modifier = Modifier
                    .padding(start = 28.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContactRow(
                    icon = Lucide.Phone,
                    text = "+91-7737087094",
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:7737087094"))
                        context.startActivity(intent)
                    }
                )
                ContactRow(
                    icon = Lucide.Mail,
                    text = "support@vidyasetuai.com",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@vidyasetuai.com")
                        }
                        context.startActivity(intent)
                    }
                )
                ContactRow(
                    icon = Lucide.MapPin,
                    text = if (isHindi) {
                        "वार्ड नंबर - 02, हनुमानगढ़, राजस्थान - 335802"
                    } else {
                        "Ward No - 02, Hanumangarh, Rajasthan - 335802"
                    },
                    onClick = {
                        val query = Uri.encode("Ward No - 02, Hanumangarh, Rajasthan - 335802")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query"))
                        context.startActivity(intent)
                    }
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Shield,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "MSME पंजीकृत इकाई" else "MSME Registered Unit",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "UDYAM-RJ-16-0040771",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TermsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }
    val isHindi = prefs.getString("language", "en") == "hi"
    val isDark = isSystemInDarkTheme()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LegalTopBar(
                title = if (isHindi) "नियम और शर्तें" else "Terms & Privacy",
                subtitle = if (isHindi) "कानूनी अनुपालन और सुरक्षा" else "Legal Compliance & Protection",
                icon = Lucide.FileText,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Compliance Alert Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1E2923) else Color(0xFFEBFDF4),
                border = BorderStroke(0.5.dp, AppColors.EmeraldGreen.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.CircleAlert,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) {
                            "सूचना प्रौद्योगिकी अधिनियम, 2000 और भारत के आगामी डिजिटल व्यक्तिगत डेटा संरक्षण (DPDP) अधिनियम का पूर्णतः अनुपालन करता है।"
                        } else {
                            "Fully compliant with the Information Technology Act, 2000 and upcoming Digital Personal Data Protection (DPDP) Act of India."
                        },
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Privacy Policy
            LegalSectionHeader(
                title = if (isHindi) "गोपनीयता नीति (Privacy Policy)" else "Privacy Policy",
                icon = Lucide.ShieldCheck
            )
            Column(
                modifier = Modifier
                    .padding(start = 28.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BulletPoint(
                    title = if (isHindi) "डेटा का स्वामित्व (Data Ownership)" else "Data Ownership",
                    desc = if (isHindi) {
                        "VidyaSetu AI प्लेटफॉर्म प्रदान करता है, लेकिन सारा डेटा (छात्रों, कर्मचारियों, वित्तीय लेनदेन) 100% आपके संस्थान/स्कूल का है। हमारे पास आपके डेटा को एक्सेस करने या बेचने का कोई कानूनी अधिकार नहीं है।"
                    } else {
                        "VidyaSetu AI provides the platform, but all data (students, staff, finances) belongs 100% to the school. We have no legal right to access or sell your data."
                    }
                )
                BulletPoint(
                    title = if (isHindi) "शून्य तृतीय-पक्ष डेटा साझाकरण" else "Zero Third-Party Sharing",
                    desc = if (isHindi) {
                        "हम कभी भी आपका डेटा किसी तीसरे पक्ष या विज्ञापन एजेंसी के साथ साझा नहीं करते हैं।"
                    } else {
                        "We never share your data with any third-party or advertising agency."
                    }
                )
            }

            // Terms of Service
            LegalSectionHeader(
                title = if (isHindi) "सेवा की शर्तें (Terms of Service)" else "Terms of Service",
                icon = Lucide.FileText
            )
            Column(
                modifier = Modifier
                    .padding(start = 28.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BulletPoint(
                    title = if (isHindi) "सेवा की उपलब्धता (Service Availability)" else "Service Availability",
                    desc = if (isHindi) {
                        "हम अपने क्लाउड सर्वर पर 99.9% अपटाइम की गारंटी देते हैं। तकनीकी रुकावटों के कारण आपकी स्कूल की दैनिक गतिविधियां कभी बाधित नहीं होंगी।"
                    } else {
                        "We guarantee 99.9% uptime on our cloud servers. Your school operations will never halt due to technical lag."
                    }
                )
                BulletPoint(
                    title = if (isHindi) "सदस्यता और रद्दीकरण" else "Subscription & Cancellation",
                    desc = if (isHindi) {
                        "कोई गुप्त लॉक-इन अवधि नहीं है। यदि आप सदस्यता रद्द करने का विकल्प चुनते हैं, तो हम आपका संपूर्ण डेटा मानक प्रारूपों (CSV, Excel) में निर्यात करने के लिए 30-दिन का समय प्रदान करते हैं।"
                    } else {
                        "No hidden lock-in periods. If you choose to cancel, we provide a 30-day window to export all your data in standard formats."
                    }
                )

                // Mobile App Legal Disclaimer (Warning Card)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color(0xFF2C2216) else Color(0xFFFFF9E6),
                    border = BorderStroke(0.5.dp, Color(0xFFD97706).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Lucide.CircleAlert,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) {
                                    "मोबाइल ऐप कानूनी अस्वीकरण"
                                } else {
                                    "Mobile App Legal Disclaimer"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFF59E0B) else Color(0xFFB45309)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHindi) {
                                "सदस्यता खरीदने से केवल 'VidyaSetu AI ERP डेस्कटॉप और वेब सॉफ़्टवेयर' के लाइसेंस अधिकार प्राप्त होते हैं। मोबाइल एप्लिकेशन (कर्मचारियों, माता-पिता और छात्रों द्वारा उपयोग किए जाने वाले) एक पूरी तरह से अलग और स्वतंत्र परियोजना हैं। यह केवल एक सहायक सेवा के रूप में दी जा रही है। मोबाइल ऐप की उपलब्धता, लेआउट या सुविधाओं के संबंध में प्लेटफ़ॉर्म के खिलाफ कोई भी कानूनी कार्रवाई (legal action) नहीं की जा सकती है।"
                            } else {
                                "Purchasing a subscription grants licensing rights only for the VidyaSetu AI ERP desktop and web software. The mobile applications (for Android/iOS used by staff, parents, and students) are part of a completely separate, independent project. It is offered as a supplementary service only. Users and schools hold no ownership, code, or service continuity claims over the mobile apps, and no legal action (कानूनी कार्रवाई) can be initiated against the platform regarding mobile app availability, layout, or features."
                            },
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Data Protection
            LegalSectionHeader(
                title = if (isHindi) "डेटा सुरक्षा और अनुपालन" else "Data Protection & Compliance",
                icon = Lucide.Lock
            )
            Column(
                modifier = Modifier
                    .padding(start = 28.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BulletPoint(
                    title = if (isHindi) "DPDP अधिनियम 2023 अनुपालन" else "DPDP Act 2023 Compliance",
                    desc = if (isHindi) {
                        "भारत के डिजिटल व्यक्तिगत डेटा संरक्षण (DPDP) अधिनियम, 2023 के तहत पूर्ण अनुपालन। छात्रों और स्टाफ का डेटा स्कूल द्वारा अधिकृत सहमति के आधार पर ही प्रोसेस किया जाता है।"
                    } else {
                        "Fully compliant with India's Digital Personal Data Protection (DPDP) Act, 2023. Student and staff personal data is processed strictly based on school-authorized consent."
                    }
                )
                BulletPoint(
                    title = if (isHindi) "AES-256 एन्क्रिप्शन सुरक्षा" else "AES-256 Encryption",
                    desc = if (isHindi) {
                        "सभी संवेदनशील डेटा को क्लाउड डेटाबेस पर एईएस-256 बिट एन्क्रिप्शन के साथ सुरक्षित रखा जाता है, जो वैश्विक बैंकिंग प्रणालियों द्वारा उपयोग किया जाने वाला उच्चतम मानक है।"
                    } else {
                        "All sensitive data is encrypted at rest and in transit. This is the same standard used by global banking systems."
                    }
                )
                BulletPoint(
                    title = if (isHindi) "दैनिक बैकअप प्रक्रिया" else "Daily Backups",
                    desc = if (isHindi) {
                        "किसी भी हार्डवेयर विफलता या डेटा हानि को रोकने के लिए हम कई सुरक्षित स्थानों पर डेटा के दैनिक बैकअप बनाए रखते हैं।"
                    } else {
                        "We maintain redundant backups across multiple secure locations to prevent data loss in case of hardware failure."
                    }
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Lock,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DPDP & AES-256 Compliant",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "VidyaSetu Legal OS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Dialog Wrapper for easy overlay invoking from anywhere without navigation routes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AboutScreen(onBack = onDismiss)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TermsScreen(onBack = onDismiss)
        }
    }
}

// Core reusable sub-components
@Composable
private fun LegalTopBar(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AppColors.EmeraldGreen.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppColors.EmeraldGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}

@Composable
private fun LegalSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.EmeraldGreen,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun PricingCard(
    title: String,
    originalPrice: String,
    offerPrice: String,
    description: String,
    isPremium: Boolean,
    isDark: Boolean
) {
    val cardBg = if (isPremium) {
        if (isDark) Color(0xFF0F261D) else Color(0xFFECFDF5)
    } else {
        if (isDark) Color(0xFF1C1C1E) else Color(0xFFF9F9F9)
    }
    val cardBorder = if (isPremium) {
        AppColors.EmeraldGreen.copy(alpha = 0.24f)
    } else {
        if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = cardBg,
        border = BorderStroke(0.5.dp, cardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = originalPrice,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.LineThrough,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = offerPrice,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.EmeraldGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = AppColors.EmeraldGreen,
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BulletPoint(
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Lucide.CircleCheck,
            contentDescription = null,
            tint = AppColors.EmeraldGreen,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(14.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
