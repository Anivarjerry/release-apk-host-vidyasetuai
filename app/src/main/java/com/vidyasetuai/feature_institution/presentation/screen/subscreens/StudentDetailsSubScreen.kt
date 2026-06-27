package com.vidyasetuai.feature_feed.presentation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailsSubScreen(
    state: InstitutionUiState,
    studentId: String,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    var actionNumbers by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var pendingActionType by remember { mutableStateOf<String?>(null) } // "call" or "whatsapp"
    var showNumberSelector by remember { mutableStateOf(false) }

    fun getWhatsAppNumber(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return if (digits.length == 10) "91$digits" else digits
    }

    var currentStudentId by remember { mutableStateOf(studentId) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var data by remember { mutableStateOf<JsonObject?>(null) }

    // Toggle expansion states
    var isAdditionalExpanded by remember { mutableStateOf(false) }
    var expandedExamId by remember { mutableStateOf<String?>(null) }
    var isAttendanceExpanded by remember { mutableStateOf(false) }

    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val inputBg = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)

    // Load details when studentId changes
    LaunchedEffect(currentStudentId) {
        if (currentStudentId.isNotEmpty()) {
            scope.launch {
                try {
                    isLoading = true
                    errorMsg = null
                    val result = withContext(Dispatchers.IO) {
                        SupabaseClient.client.postgrest.rpc(
                            "get_student_directory_profile",
                            mapOf("p_student_id" to currentStudentId)
                        ).decodeAs<JsonObject>()
                    }
                    data = result
                } catch (e: Exception) {
                    Log.e("StudentDetails", "Error loading profile details", e)
                    errorMsg = e.message ?: "Failed to load details"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // Helper functions to safely parse nested JSON
    fun JsonObject.getObj(key: String): JsonObject? {
        val el = this[key]
        return if (el != null && el !is JsonNull) el.jsonObject else null
    }
    fun JsonObject.getArray(key: String): JsonArray? {
        val el = this[key]
        return if (el != null && el !is JsonNull) el.jsonArray else null
    }
    fun JsonElement?.strVal(key: String, fallback: String = "—"): String {
        if (this == null || this is JsonNull || this !is JsonObject) return fallback
        return this[key]?.jsonPrimitive?.contentOrNull ?: fallback
    }
    fun JsonElement?.doubleVal(key: String, fallback: Double = 0.0): Double {
        if (this == null || this is JsonNull || this !is JsonObject) return fallback
        return this[key]?.jsonPrimitive?.doubleOrNull ?: fallback
    }
    fun JsonElement?.intVal(key: String, fallback: Int = 0): Int {
        if (this == null || this is JsonNull || this !is JsonObject) return fallback
        return this[key]?.jsonPrimitive?.intOrNull ?: fallback
    }
    fun JsonElement?.boolVal(key: String, fallback: Boolean = false): Boolean {
        if (this == null || this is JsonNull || this !is JsonObject) return fallback
        return this[key]?.jsonPrimitive?.booleanOrNull ?: fallback
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = AppColors.EmeraldGreen, modifier = Modifier.size(36.dp))
            } else if (errorMsg != null || data == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Lucide.CircleAlert,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isHindi) "लोड करने में असमर्थ" else "Failed to load details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = errorMsg ?: "Unknown error",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val info = data!!.getObj("student_info")
                val additional = data!!.getObj("additional_details")
                val guardian = data!!.getObj("guardian_info")
                val bus = data!!.getObj("bus_assignment")
                val fee = data!!.getObj("fee_summary")
                val exams = data!!.getArray("exams") ?: JsonArray(emptyList())
                val siblings = data!!.getArray("siblings") ?: JsonArray(emptyList())
                val attSummary = data!!.getObj("attendance_summary")
                val recentAtt = data!!.getArray("recent_attendance") ?: JsonArray(emptyList())

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Header Profile Box
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(inputBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val img = info.strVal("image_url", "")
                                        if (img.isNotEmpty()) {
                                            AsyncImage(
                                                model = img,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Lucide.User,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = info.strVal("name", "Student").uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = "SR: ${info.strVal("sr_number")} | Roll: ${info.strVal("roll_number")}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                            modifier = Modifier.padding(top = 6.dp)
                                        ) {
                                            val rawClass = info.strVal("class_name", "N/A")
                                            val rawSection = info.strVal("section_name", "A")
                                            val displayClass = if (rawClass == "null" || rawClass.isBlank()) "N/A" else rawClass
                                            val displaySection = if (rawSection == "null" || rawSection.isBlank()) "A" else rawSection
                                            val classText = if (displayClass != "N/A") "$displayClass - $displaySection" else "N/A"
                                            Text(
                                                text = classText,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.EmeraldGreen,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                val gImage = info.strVal("guardian_image_url", "")
                                if (gImage.isNotEmpty() || guardian != null) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(inputBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (gImage.isNotEmpty()) {
                                                AsyncImage(model = gImage, contentDescription = null)
                                            } else {
                                                Icon(imageVector = Lucide.Users, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = guardian.strVal("name", "Guardian"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                            Text(text = if (isHindi) "पंजीकृत अभिभावक" else "Registered Guardian", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                // Whatsapp and Call actions
                                Spacer(modifier = Modifier.height(14.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // WhatsApp Button
                                    Button(
                                        onClick = {
                                            val validNumbers = listOfNotNull(
                                                guardian.strVal("mobile_number").takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "अभिभावक" else "Guardian") + " ($it)" to it },
                                                additional.strVal("father_mobile").takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "पिता" else "Father") + " ($it)" to it },
                                                additional.strVal("mother_mobile").takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "माता" else "Mother") + " ($it)" to it }
                                            ).distinctBy { it.second }
                                            
                                            if (validNumbers.isEmpty()) {
                                                Toast.makeText(context, if (isHindi) "कोई मोबाइल नंबर उपलब्ध नहीं है" else "No mobile numbers available", Toast.LENGTH_SHORT).show()
                                            } else if (validNumbers.size == 1) {
                                                val number = validNumbers.first().second
                                                val formatted = getWhatsAppNumber(number)
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    this.data = Uri.parse("https://api.whatsapp.com/send?phone=$formatted")
                                                }
                                                context.startActivity(intent)
                                            } else {
                                                actionNumbers = validNumbers
                                                pendingActionType = "whatsapp"
                                                showNumberSelector = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(imageVector = Lucide.MessageSquareCode, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                            Text(text = if (isHindi) "व्हाट्सएप" else "WhatsApp", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    
                                    // Direct Call Button
                                    Button(
                                        onClick = {
                                            val validNumbers = listOfNotNull(
                                                guardian.strVal("mobile_number").takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "अभिभावक" else "Guardian") + " ($it)" to it },
                                                additional.strVal("father_mobile").takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "पिता" else "Father") + " ($it)" to it },
                                                additional.strVal("mother_mobile").takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "माता" else "Mother") + " ($it)" to it }
                                            ).distinctBy { it.second }
                                            
                                            if (validNumbers.isEmpty()) {
                                                Toast.makeText(context, if (isHindi) "कोई मोबाइल नंबर उपलब्ध नहीं है" else "No mobile numbers available", Toast.LENGTH_SHORT).show()
                                            } else if (validNumbers.size == 1) {
                                                val number = validNumbers.first().second
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    this.data = Uri.parse("tel:$number")
                                                }
                                                context.startActivity(intent)
                                            } else {
                                                actionNumbers = validNumbers
                                                pendingActionType = "call"
                                                showNumberSelector = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(imageVector = Lucide.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                            Text(text = if (isHindi) "कॉल करें" else "Call", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Expandable Additional Details
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "छात्र प्रोफाइल विवरण" else "Student Profile Details",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AppColors.EmeraldGreen
                                    )
                                    IconButton(
                                        onClick = { isAdditionalExpanded = !isAdditionalExpanded },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isAdditionalExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                            contentDescription = null,
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DetailRow(label = if (isHindi) "लिंग:" else "Gender:", value = info.strVal("gender"))
                                    DetailRow(label = if (isHindi) "जन्म तिथि:" else "Date of Birth:", value = info.strVal("date_of_birth"))
                                    DetailRow(label = if (isHindi) "राष्ट्रीयता:" else "Nationality:", value = additional.strVal("nationality", "Indian"))
                                    DetailRow(label = if (isHindi) "धर्म:" else "Religion:", value = additional.strVal("religion"))

                                    if (isAdditionalExpanded) {
                                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                        DetailRow(label = if (isHindi) "मातृभाषा:" else "Mother Tongue:", value = additional.strVal("mother_tongue_text"))
                                        DetailRow(label = if (isHindi) "एकल कन्या:" else "Single Girl Child:", value = if (additional.boolVal("is_single_girl_child")) (if (isHindi) "हाँ" else "Yes") else (if (isHindi) "नहीं" else "No"))
                                        DetailRow(label = if (isHindi) "पहचान चिन्ह:" else "ID Mark:", value = additional.strVal("identification_mark"))
                                        DetailRow(label = if (isHindi) "छात्र आधार:" else "Student Aadhaar:", value = additional.strVal("student_aadhar"))

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = if (isHindi) "स्थायी पता" else "Permanent Address", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = inputBg,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = additional.strVal("permanent_address_details"),
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = if (isHindi) "बैंक खाता विवरण" else "Bank Account Details", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(inputBg, RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            DetailRow(label = if (isHindi) "बैंक का नाम:" else "Bank:", value = additional.strVal("bank_name"))
                                            DetailRow(label = if (isHindi) "खाता संख्या:" else "Account No:", value = additional.strVal("bank_account_number"))
                                            DetailRow(label = "IFSC:", value = additional.strVal("bank_ifsc"))
                                            DetailRow(label = if (isHindi) "खाताधारक:" else "Holder:", value = additional.strVal("bank_account_holder"))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Transport Assignment
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) "आवंटित परिवहन" else "Assigned Transport",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AppColors.EmeraldGreen,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                if (bus != null && !bus.strVal("bus_number").isNullOrEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(inputBg, RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = Lucide.Bus, contentDescription = null, tint = AppColors.EmeraldGreen, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = bus.strVal("bus_number"), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                                            Text(text = "${bus.strVal("bus_name")} • Route: ${bus.strVal("route_name")}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                } else {
                                    Text(
                                        text = if (isHindi) "कोई बस आवंटित नहीं है" else "No bus route assigned.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    )
                                }
                            }
                        }
                    }

                    // 4. Fee Summary Ledger
                    item {
                        val totalExp = fee.doubleVal("total_expected")
                        val totalPaid = fee.doubleVal("total_paid")
                        val totalPending = fee.doubleVal("total_pending")

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) "शुल्क विवरण" else "Fee Ledger Summary",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AppColors.EmeraldGreen,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Expected
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(inputBg, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = if (isHindi) "कुल अपेक्षित" else "Expected", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "₹${totalExp.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                    }
                                    // Paid
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(AppColors.EmeraldGreen.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                            .border(0.5.dp, AppColors.EmeraldGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = if (isHindi) "जमा शुल्क" else "Collected", fontSize = 9.sp, color = AppColors.EmeraldGreen)
                                        Text(text = "₹${totalPaid.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                    }
                                    // Pending
                                    val pendingColor = if (totalPending > 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                                    val pendingBg = if (totalPending > 0) Color(0xFFEF4444).copy(alpha = 0.08f) else inputBg
                                    val pendingBorder = if (totalPending > 0) Color(0xFFEF4444).copy(alpha = 0.2f) else borderVal
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(pendingBg, RoundedCornerShape(8.dp))
                                            .border(0.5.dp, pendingBorder, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = if (isHindi) "लंबित" else "Pending", fontSize = 9.sp, color = pendingColor)
                                        Text(text = "₹${totalPending.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = pendingColor)
                                    }
                                }

                                if (totalExp > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val ratio = (totalPaid / totalExp).toFloat()
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = if (isHindi) "संग्रह अनुपात" else "Collection Ratio", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "${(ratio * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = ratio,
                                        color = AppColors.EmeraldGreen,
                                        trackColor = inputBg,
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                                    )
                                }
                            }
                        }
                    }

                    // 5. Assigned Examinations (Accordion Subjects & Marks)
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) "परीक्षा सूची (विवरण हेतु क्लिक करें)" else "Examinations (Click for Marks)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AppColors.EmeraldGreen,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                if (exams.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        exams.forEach { examEl ->
                                            val exam = examEl.jsonObject
                                            val examId = exam["id"]?.jsonPrimitive?.contentOrNull ?: ""
                                            val isExpanded = expandedExamId == examId
                                            val subjects = exam["subjects"]?.jsonArray ?: JsonArray(emptyList())

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .border(0.5.dp, borderVal, RoundedCornerShape(8.dp))
                                                    .clip(RoundedCornerShape(8.dp))
                                            ) {
                                                // Trigger Row
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { expandedExamId = if (isExpanded) null else examId }
                                                        .padding(12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(text = exam.strVal("name", "Exam"), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                                                        Text(text = exam.strVal("exam_type_name", "Regular"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(text = exam.strVal("start_date"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Icon(imageVector = if (isExpanded) Lucide.ChevronUp else Lucide.ChevronDown, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }

                                                // Subjects detail table
                                                if (isExpanded) {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(inputBg)
                                                            .padding(10.dp),
                                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        // Headers
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                            Text(text = if (isHindi) "विषय" else "Subject", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Text(text = if (isHindi) "अंक" else "Marks", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Text(text = if (isHindi) "स्थिति" else "Status", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        }
                                                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))

                                                        subjects.forEach { subEl ->
                                                            val sub = subEl.jsonObject
                                                            val hasMarks = sub["obtained_marks"]?.jsonPrimitive?.contentOrNull != null
                                                            val obt = sub.doubleVal("obtained_marks")
                                                            val max = sub.doubleVal("max_marks", 100.0)
                                                            val min = sub.doubleVal("minimum_passing_marks", 33.0)
                                                            val isAbs = sub.boolVal("is_absent")
                                                            val isMed = sub.boolVal("is_medical_leave")
                                                            val isPassed = hasMarks && obt >= min

                                                            val statusText = when {
                                                                isAbs -> if (isHindi) "अनुपस्थित" else "Absent"
                                                                isMed -> if (isHindi) "अवकाश" else "Medical"
                                                                hasMarks -> if (isPassed) (if (isHindi) "उत्तीर्ण" else "Passed") else (if (isHindi) "अनुत्तीर्ण" else "Failed")
                                                                else -> if (isHindi) "लंबित" else "Pending"
                                                            }
                                                            val statusColor = when {
                                                                isAbs -> Color(0xFFEF4444)
                                                                isMed -> Color(0xFFF59E0B)
                                                                hasMarks -> if (isPassed) AppColors.EmeraldGreen else Color(0xFFEF4444)
                                                                else -> Color(0xFF8E8E93)
                                                            }

                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(text = sub.strVal("subject_name"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                                                                Text(
                                                                    text = if (isAbs || isMed) "—" else "${obt.toInt()}/${max.toInt()}",
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.onBackground
                                                                )
                                                                Surface(
                                                                    shape = RoundedCornerShape(4.dp),
                                                                    color = statusColor.copy(alpha = 0.12f),
                                                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, statusColor.copy(alpha = 0.3f))
                                                                ) {
                                                                    Text(
                                                                        text = statusText,
                                                                        fontSize = 8.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = statusColor,
                                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        text = if (isHindi) "कोई परीक्षा निर्धारित नहीं है" else "No exams scheduled.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    )
                                }
                            }
                        }
                    }

                    // 6. Parents Info
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) "माता-पिता का विवरण" else "Parents Information",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AppColors.EmeraldGreen,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Father
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(inputBg, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = if (isHindi) "पिता" else "Father", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                        Text(text = additional.strVal("father_name"), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(text = additional.strVal("father_mobile"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = additional.strVal("father_occupation"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    // Mother
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(inputBg, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = if (isHindi) "माता" else "Mother", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                        Text(text = additional.strVal("mother_name"), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(text = additional.strVal("mother_mobile"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = additional.strVal("mother_occupation"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }

                    // 7. Siblings Details (Dynamic click navigation reload)
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) "सहोदर (भाई/बहन) विवरण" else "Siblings (Brother/Sister) Details",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AppColors.EmeraldGreen,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                if (siblings.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        siblings.forEach { sibEl ->
                                            val sib = sibEl.jsonObject
                                            val sibId = sib["id"]?.jsonPrimitive?.contentOrNull ?: ""
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { currentStudentId = sibId } // Switch profile dynamically!
                                                    .background(inputBg, RoundedCornerShape(8.dp))
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(borderVal),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    val sImg = sib.strVal("image_url", "")
                                                    if (sImg.isNotEmpty()) {
                                                        AsyncImage(model = sImg, contentDescription = null)
                                                    } else {
                                                        Icon(imageVector = Lucide.User, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = sib.strVal("name"), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                                                    val cls = sib.strVal("class_name").takeIf { it != "null" && it.isNotBlank() } ?: "N/A"
                                                    Text(text = "Class: $cls • SR: ${sib.strVal("sr_number")}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Icon(imageVector = Lucide.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        text = if (isHindi) "डेटाबेस में कोई सहोदर लिंक नहीं है" else "No siblings linked.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    )
                                }
                            }
                        }
                    }

                    // 8. Attendance Overview & Logs
                    item {
                        val present = attSummary.intVal("present_count")
                        val absent = attSummary.intVal("absent_count")
                        val leave = attSummary.intVal("leave_count")
                        val totalDays = attSummary.intVal("total_days")
                        val attPercent = if (totalDays > 0) ((present.toFloat() / totalDays) * 100).toInt() else 0

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "उपस्थिति इतिहास" else "Attendance History Ledger",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AppColors.EmeraldGreen
                                    )
                                    IconButton(
                                        onClick = { isAttendanceExpanded = !isAttendanceExpanded },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isAttendanceExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                            contentDescription = null,
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .border(3.dp, AppColors.EmeraldGreen.copy(alpha = 0.2f), CircleShape)
                                            .padding(3.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "$attPercent%", fontSize = 11.sp, fontWeight = FontWeight.Black, color = AppColors.EmeraldGreen)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f).background(inputBg, RoundedCornerShape(6.dp)).padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = if (isHindi) "उपस्थित" else "Present", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(text = present.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                        }
                                        Column(modifier = Modifier.weight(1f).background(inputBg, RoundedCornerShape(6.dp)).padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = if (isHindi) "अनुपस्थित" else "Absent", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(text = absent.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                        }
                                        Column(modifier = Modifier.weight(1f).background(inputBg, RoundedCornerShape(6.dp)).padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = if (isHindi) "अवकाश" else "Leaves", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(text = leave.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                        }
                                    }
                                }

                                if (isAttendanceExpanded) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (recentAtt.isNotEmpty()) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            recentAtt.forEach { logEl ->
                                                val log = logEl.jsonObject
                                                val status = log.strVal("status")
                                                val statusColor = when (status) {
                                                    "Present" -> AppColors.EmeraldGreen
                                                    "Absent" -> Color(0xFFEF4444)
                                                    "Late", "Half Day" -> Color(0xFFF59E0B)
                                                    else -> Color(0xFF8E8E93)
                                                }

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(inputBg, RoundedCornerShape(6.dp))
                                                        .padding(vertical = 8.dp, horizontal = 10.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = log.strVal("date"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                                                    val remarks = log.strVal("remarks", "")
                                                    if (remarks.isNotEmpty()) {
                                                        Text(text = "($remarks)", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = statusColor.copy(alpha = 0.12f),
                                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, statusColor.copy(alpha = 0.3f))
                                                    ) {
                                                        Text(text = status, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Text(text = if (isHindi) "कोई उपस्थिति रिकॉर्ड नहीं है" else "No attendance logs found.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNumberSelector && pendingActionType != null) {
        val actionLabel = if (pendingActionType == "call") {
            if (isHindi) "कॉल करने के लिए नंबर चुनें" else "Select number to call"
        } else {
            if (isHindi) "व्हाट्सएप चैट के लिए नंबर चुनें" else "Select number for WhatsApp"
        }
        
        AlertDialog(
            onDismissRequest = { 
                showNumberSelector = false
                pendingActionType = null
            },
            title = { Text(actionLabel, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    actionNumbers.forEach { pair ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showNumberSelector = false
                                    val number = pair.second
                                    if (pendingActionType == "call") {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            this.data = Uri.parse("tel:$number")
                                        }
                                        context.startActivity(intent)
                                    } else {
                                        val formatted = getWhatsAppNumber(number)
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            this.data = Uri.parse("https://api.whatsapp.com/send?phone=$formatted")
                                        }
                                        context.startActivity(intent)
                                    }
                                    pendingActionType = null
                                }
                                .border(0.5.dp, borderVal, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            ) {
                                Text(
                                    text = pair.first, 
                                    fontSize = 13.sp, 
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Icon(
                                    imageVector = if (pendingActionType == "call") Lucide.Phone else Lucide.MessageSquareCode,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showNumberSelector = false
                    pendingActionType = null
                }) {
                    Text(if (isHindi) "बंद करें" else "Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    }
}
