package com.vidyasetuai.feature_store.presentation.screen.role_staff.parties.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowDownLeft
import com.composables.icons.lucide.ArrowUpRight
import com.composables.icons.lucide.MessageSquare
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Share2
import com.composables.icons.lucide.X
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyLedgerEntryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyPassbookBottomSheet(
    isHindi: Boolean = false,
    party: PartyEntity,
    ledgerEntries: List<PartyLedgerEntryEntity>,
    isLoading: Boolean,
    storeName: String,
    onGetWhatsAppUrl: (PartyEntity, String) -> String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val context = LocalContext.current
    val currentBal = party.currentBalance

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
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "खाता पासबुक व लेन-देन विवरण" else "Party Passbook & Ledger",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${party.name} • 📞 ${party.phone}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Balance Card & WhatsApp Reminder Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = when {
                    currentBal > 0 -> Color(0xFFFEE2E2)
                    currentBal < 0 -> Color(0xFFDCFCE7)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when {
                        currentBal > 0 -> Color(0xFFEF4444).copy(alpha = 0.4f)
                        currentBal < 0 -> Color(0xFF10B981).copy(alpha = 0.4f)
                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when {
                                currentBal > 0 -> if (isHindi) "कुल बकाया (Net Due):" else "Net Receivable (Due):"
                                currentBal < 0 -> if (isHindi) "एडवांस जमा (Advance):" else "Net Advance Paid:"
                                else -> if (isHindi) "खाता चुकता (Settled):" else "Net Balance Settled:"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "₹${String.format("%.2f", Math.abs(currentBal))}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when {
                                currentBal > 0 -> Color(0xFFDC2626)
                                currentBal < 0 -> Color(0xFF166534)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    // 1-Tap WhatsApp Reminder CTA
                    Button(
                        onClick = {
                            val waUrl = onGetWhatsAppUrl(party, storeName)
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.MessageSquare,
                            contentDescription = "WhatsApp",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "तकादा भेजें" else "WhatsApp Reminder",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Passbook History Title
            Text(
                text = if (isHindi) "लेन-देन इतिहास (Transaction Audit Trail)" else "Transaction Audit Trail",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF10B981),
                        strokeWidth = 2.dp
                    )
                }
            } else if (ledgerEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isHindi) "इस पार्टी का कोई पिछला लेन-देन नहीं है।" else "No transaction history found for this party.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(ledgerEntries, key = { it.id }) { entry ->
                        LedgerEntryCard(entry = entry, isHindi = isHindi)
                    }
                }
            }
        }
    }
}

@Composable
private fun LedgerEntryCard(
    entry: PartyLedgerEntryEntity,
    isHindi: Boolean
) {
    val isCredit = entry.entryType == "CREDIT"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Direction Icon Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isCredit) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCredit) Lucide.ArrowDownLeft else Lucide.ArrowUpRight,
                        contentDescription = entry.entryType,
                        tint = if (isCredit) Color(0xFF166534) else Color(0xFF991B1B),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isCredit) {
                                if (isHindi) "जमा (Payment In)" else "CREDIT (In)"
                            } else {
                                if (isHindi) "उधार / नामे (Bill / Out)" else "DEBIT (Bill)"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCredit) Color(0xFF166534) else Color(0xFF991B1B)
                        )

                        // Ref Type Badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = entry.referenceType,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = entry.description ?: entry.createdAt.take(10),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}₹${String.format("%.2f", entry.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCredit) Color(0xFF166534) else Color(0xFF991B1B)
                )

                Text(
                    text = "${if (isHindi) "शेष:" else "Bal:"} ₹${String.format("%.2f", entry.balanceAfter)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
