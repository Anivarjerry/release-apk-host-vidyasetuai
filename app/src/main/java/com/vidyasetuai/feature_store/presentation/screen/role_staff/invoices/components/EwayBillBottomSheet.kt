package com.vidyasetuai.feature_store.presentation.screen.role_staff.invoices.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.domain.model.InvoiceWithDetailsUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EwayBillBottomSheet(
    invoiceWithDetails: InvoiceWithDetailsUiModel,
    isHindi: Boolean = false,
    isSubmitting: Boolean = false,
    onDismiss: () -> Unit,
    onSaveEwayBill: (ewayNo: String?, vehicleNo: String?, transportName: String?, distanceKm: Int, ewayStatus: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val invoice = invoiceWithDetails.invoice

    var ewayNumber by remember(invoiceWithDetails) { mutableStateOf(invoice.ewayBillNumber ?: "") }
    var vehicleNumber by remember(invoiceWithDetails) { mutableStateOf(invoice.vehicleNumber ?: "") }
    var transporterName by remember(invoiceWithDetails) { mutableStateOf(invoice.transportName ?: "") }
    var distanceKmText by remember(invoiceWithDetails) { mutableStateOf(if (invoice.distanceKm > 0) invoice.distanceKm.toString() else "50") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.Truck,
                                contentDescription = "E-Way",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (isHindi) "ई-वे बिल प्रबंधन (E-Way Bill)" else "E-Way Bill Details",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Invoice #${invoice.invoiceNumber} (₹${"%.2f".format(invoice.grandTotal)})",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            // --- INPUT FIELDS ---
            OutlinedTextField(
                value = ewayNumber,
                onValueChange = { ewayNumber = it },
                label = { Text(if (isHindi) "ई-वे बिल नंबर (12 Digit E-Way No)" else "E-Way Bill Number") },
                placeholder = { Text("e.g. 121098421098") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = vehicleNumber,
                onValueChange = { vehicleNumber = it.uppercase() },
                label = { Text(if (isHindi) "गाड़ी / वाहन नंबर (Vehicle No) *" else "Vehicle Number *") },
                placeholder = { Text("e.g. RJ14-GB-9821") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = transporterName,
                onValueChange = { transporterName = it },
                label = { Text(if (isHindi) "ट्रांसपोर्टर का नाम / कंपनी" else "Transporter Name / Logistics") },
                placeholder = { Text("e.g. VRL Logistics / Delhivery") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = distanceKmText,
                onValueChange = { distanceKmText = it },
                label = { Text(if (isHindi) "दूरी (Distance in KM)" else "Distance (Approx KM)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // --- ACTION BUTTONS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }

                Button(
                    onClick = {
                        val km = distanceKmText.toIntOrNull() ?: 0
                        val status = if (ewayNumber.isNotBlank()) "GENERATED" else "PENDING"
                        onSaveEwayBill(
                            ewayNumber.ifBlank { null },
                            vehicleNumber.ifBlank { null },
                            transporterName.ifBlank { null },
                            km,
                            status
                        )
                    },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(46.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isHindi) "सेव हो रहा है..." else "Saving...", fontSize = 13.sp, color = Color.White)
                    } else {
                        Text(if (isHindi) "ई-वे बिल सेव करें" else "Save E-Way Bill", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
