package com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.components

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.util.QrCodeGenerator
import com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.util.StandeePosterExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreQrStandeeBottomSheet(
    isHindi: Boolean = false,
    business: BusinessEntity?,
    mainBranch: BusinessBranchEntity?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val storeSlug = business?.slug?.ifBlank { "store" } ?: "store"
    // Exact Storefront Web URL: https://vidyasetuai.com/store/{slug}
    val storeUrl = "https://vidyasetuai.com/store/$storeSlug"
    val storeName = business?.tradeName?.ifBlank { business.legalName } ?: "VidyaSetu Store"
    val address = mainBranch?.addressLine?.let { "$it, ${mainBranch.city}" } ?: "Main Counter, Jaipur"
    val phone = business?.phone ?: "9829012345"

    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isSavingToGallery by remember { mutableStateOf(false) }

    // Generate real, scannable QR code bitmap
    LaunchedEffect(storeUrl) {
        withContext(Dispatchers.Default) {
            qrBitmap = QrCodeGenerator.generateQrBitmap(
                content = storeUrl,
                sizePx = 600,
                foregroundColor = android.graphics.Color.BLACK,
                backgroundColor = android.graphics.Color.WHITE
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            // Header (Screen Only)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.QrCode,
                            contentDescription = "QR Standee",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isHindi) "प्रिंट करने योग्य स्टोर QR स्टैंडी पोस्टर" else "Printable Store QR Standee Poster",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "डाइनिंग टेबल या कैश काउंटर पर लगाने के लिए" else "Frame & place on dining tables or cash counter for instant ordering",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Body: 1:1 Web App Standee Poster Card Preview
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Printable Standee Card (1:1 Web App replica)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF9FDFB),
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF10B981).copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Top Emerald Pill ("✨ SCAN & ORDER DIRECTLY FROM TABLE")
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF10B981)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Sparkles,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "SCAN & ORDER DIRECTLY FROM TABLE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        // Big Bold Store Title (Uppercase)
                        Text(
                            text = storeName.uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        // Address with Location Pin
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.MapPin,
                                contentDescription = "Location",
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = address,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Center HD Scannable QR Code Box
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color.White,
                                shadowElevation = 6.dp,
                                border = androidx.compose.foundation.BorderStroke(4.dp, Color(0xFF10B981)),
                                modifier = Modifier.size(200.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (qrBitmap != null) {
                                        Image(
                                            bitmap = qrBitmap!!.asImageBitmap(),
                                            contentDescription = "Real Scannable Store QR Code",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(32.dp),
                                            color = Color(0xFF10B981)
                                        )
                                    }
                                }
                            }

                            // Slug Badge at bottom of QR
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF34D399)),
                                modifier = Modifier.offset(y = 12.dp)
                            ) {
                                Text(
                                    text = storeSlug,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Instructions Section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "NO APP DOWNLOAD REQUIRED ⚡",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "1. Open Phone Camera 📷 & Scan QR Code above\n2. Select Delicious Food/Items & Place Order in 30 Seconds!",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }

                        Divider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(top = 4.dp))

                        // Footer (Phone & Powered by VidyaSetu AI)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Phone,
                                    contentDescription = "Phone",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Ph: $phone",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.ShieldCheck,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Powered by VidyaSetu AI",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF059669)
                                )
                            }
                        }
                    }
                }

                // Copy URL Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = storeUrl,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(storeUrl))
                                Toast.makeText(context, "✅ लिंक कॉपी हो गया: $storeUrl", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Lucide.Copy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Action Buttons: 1. Download Poster to Gallery, 2. Share Poster Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Download to Gallery Button
                    OutlinedButton(
                        onClick = {
                            if (qrBitmap != null) {
                                isSavingToGallery = true
                                scope.launch(Dispatchers.Default) {
                                    val posterBitmap = StandeePosterExporter.generateStandeePosterBitmap(
                                        storeName = storeName,
                                        storeSlug = storeSlug,
                                        address = address,
                                        phone = phone,
                                        qrBitmap = qrBitmap!!
                                    )
                                    val isSaved = StandeePosterExporter.savePosterToGallery(
                                        context = context,
                                        bitmap = posterBitmap,
                                        filename = "VidyaSetu_${storeSlug}_Standee.png"
                                    )
                                    withContext(Dispatchers.Main) {
                                        isSavingToGallery = false
                                        if (isSaved) {
                                            Toast.makeText(
                                                context,
                                                if (isHindi) "✅ स्टैंडी पोस्टर गैलरी (Pictures/VidyaSetuStore) में सेव हो गया!" else "✅ Standee Poster saved to Gallery!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                if (isHindi) "इमेज सेव नहीं हो सकी" else "Failed to save image",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        if (isSavingToGallery) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Lucide.Download, contentDescription = "Download", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "पोस्टर डाउनलोड" else "Save to Gallery",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Share Standee Button
                    Button(
                        onClick = {
                            if (qrBitmap != null) {
                                scope.launch(Dispatchers.Default) {
                                    val posterBitmap = StandeePosterExporter.generateStandeePosterBitmap(
                                        storeName = storeName,
                                        storeSlug = storeSlug,
                                        address = address,
                                        phone = phone,
                                        qrBitmap = qrBitmap!!
                                    )
                                    withContext(Dispatchers.Main) {
                                        StandeePosterExporter.shareStandeePoster(
                                            context = context,
                                            bitmap = posterBitmap,
                                            storeName = storeName,
                                            storeUrl = storeUrl
                                        )
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Lucide.Share2, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "शेयर स्टैंडी" else "Share Standee",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
