package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Building2
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Navigation
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.X
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchManagementBottomSheet(
    isHindi: Boolean = false,
    branches: List<BusinessBranchEntity>,
    isSubmitting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSaveBranch: (
        branchName: String,
        addressLine: String,
        city: String,
        state: String,
        pincode: String,
        lat: Double?,
        lng: Double?,
        isMainBranch: Boolean
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showAddForm by remember { mutableStateOf(false) }
    var branchName by remember { mutableStateOf("") }
    var addressLine by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Jaipur") }
    var state by remember { mutableStateOf("Rajasthan") }
    var pincode by remember { mutableStateOf("302001") }
    var lat by remember { mutableStateOf<Double?>(null) }
    var lng by remember { mutableStateOf<Double?>(null) }
    var isDetectingGps by remember { mutableStateOf(false) }
    var gpsSuccess by remember { mutableStateOf(false) }
    var isMainBranch by remember { mutableStateOf(false) }

    val locationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager }

    val onLocationReceived: (Double, Double) -> Unit = { latitude, longitude ->
        lat = latitude
        lng = longitude
        gpsSuccess = true
        isDetectingGps = false

        // Reverse Geocode using standard Android Geocoder
        scope.launch(Dispatchers.IO) {
            try {
                if (Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        withContext(Dispatchers.Main) {
                            if (addressLine.isBlank()) {
                                addressLine = addr.getAddressLine(0)
                                    ?: listOfNotNull(addr.subThoroughfare, addr.thoroughfare, addr.subLocality).joinToString(", ")
                            }
                            if (city.isBlank() || city == "Jaipur") {
                                addr.locality?.let { city = it }
                            }
                            if (state.isBlank() || state == "Rajasthan") {
                                addr.adminArea?.let { state = it }
                            }
                            if (pincode.isBlank() || pincode == "302001") {
                                addr.postalCode?.let { pincode = it }
                            }
                            Toast.makeText(
                                context,
                                if (isHindi) "📍 GPS स्थान एवं पता दर्ज हुआ!" else "📍 GPS Location & Address auto-detected!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val startLocationFetch = {
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

        if (!isGpsEnabled && !isNetworkEnabled) {
            Toast.makeText(
                context,
                if (isHindi) "कृपया डिवाइस का GPS / Location चालू करें" else "Please turn on Device Location / GPS",
                Toast.LENGTH_LONG
            ).show()
            try {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            isDetectingGps = true
            try {
                val lastLoc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if (lastLoc != null) {
                    onLocationReceived(lastLoc.latitude, lastLoc.longitude)
                }

                val listener = object : LocationListener {
                    override fun onLocationChanged(loc: Location) {
                        onLocationReceived(loc.latitude, loc.longitude)
                        locationManager?.removeUpdates(this)
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                if (isGpsEnabled) {
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L,
                        0f,
                        listener,
                        Looper.getMainLooper()
                    )
                } else if (isNetworkEnabled) {
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0L,
                        0f,
                        listener,
                        Looper.getMainLooper()
                    )
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                isDetectingGps = false
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            startLocationFetch()
        }
    }

    val detectLocation = {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            startLocationFetch()
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
                .fillMaxHeight(0.88f)
        ) {
            // Fixed Header Bar (Always visible)
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
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Building2,
                            contentDescription = "Branches",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isHindi) "शाखाएं एवं आउटलेट (${branches.size})" else "Store Branches & Outlets (${branches.size})",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "दुकान आउटलेट और वेयरहाउस लोकेशन" else "Manage physical shop outlets and inventory warehouses",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Scrollable Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Section 1: Configured Branches List
                Text(
                    text = if (isHindi) "कॉन्फिगर की गई शाखाएं (CONFIGURED BRANCHES)" else "CONFIGURED BRANCHES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            Spacer(modifier = Modifier.height(8.dp))

            branches.forEach { branch ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (branch.isMainBranch) Color(0xFF10B981).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = branch.branchName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (branch.isMainBranch) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = if (isHindi) "मुख्य शाखा (Primary Counter)" else "Primary Counter",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.MapPin,
                                contentDescription = "Address",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(13.dp)
                                    .padding(top = 2.dp)
                            )
                            Text(
                                text = "${branch.addressLine}, ${branch.city}, ${branch.state} - ${branch.pincode}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 2: New Branch Form Toggle Card
            if (!showAddForm) {
                OutlinedButton(
                    onClick = { showAddForm = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = "Add Branch",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "+ नई शाखा / आउटलेट जोड़ें" else "+ Add New Branch Outlet",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "🏢 नई शाखा विवरण" else "🏢 New Branch Details",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            TextButton(onClick = { showAddForm = false }) {
                                Text(
                                    text = if (isHindi) "रद्द करें" else "Cancel",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = branchName,
                            onValueChange = { branchName = it },
                            label = { Text(if (isHindi) "शाखा / आउटलेट का नाम *" else "Branch / Outlet Name *") },
                            placeholder = { Text("e.g. Warehouse 1, Sector-14 Outlet") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = addressLine,
                            onValueChange = { addressLine = it },
                            label = { Text(if (isHindi) "पूरा पता *" else "Full Street Address *") },
                            placeholder = { Text("Street / Area / Landmark") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = state,
                                onValueChange = { state = it },
                                label = { Text("State") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = pincode,
                                onValueChange = { if (it.length <= 6) pincode = it },
                                label = { Text("Pincode") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // GPS Auto Detect Button
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.MapPin,
                                        contentDescription = "GPS",
                                        tint = if (lat != null) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (lat != null) "GPS: ${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}" else "Store GPS Location (Optional)",
                                        fontSize = 11.sp,
                                        fontWeight = if (lat != null) FontWeight.Bold else FontWeight.Normal,
                                        color = if (lat != null) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = { detectLocation() },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    if (isDetectingGps) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                                    } else {
                                        Text(
                                            text = if (lat != null) "📍 Re-Detect" else "📍 Auto-Detect GPS",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Primary Branch Checkbox
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isMainBranch = !isMainBranch }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isMainBranch,
                                onCheckedChange = { isMainBranch = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHindi) "बिलिंग के लिए मुख्य शाखा बनाएं (Primary Main Branch)" else "Set as Primary Main Branch for Invoicing",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Save Outlet CTA
                        Button(
                            onClick = {
                                onSaveBranch(
                                    branchName,
                                    addressLine,
                                    city,
                                    state,
                                    pincode,
                                    lat,
                                    lng,
                                    isMainBranch
                                )
                                showAddForm = false
                                branchName = ""
                                addressLine = ""
                            },
                            enabled = !isSubmitting && branchName.isNotBlank() && addressLine.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = if (isHindi) "आउटलेट सुरक्षित करें (Save Outlet)" else "Save Outlet",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
 }
}
