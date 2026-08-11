package com.vidyasetuai.feature_institution.presentation.component

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.util.AudioBeaconGenerator
import kotlinx.coroutines.launch

import androidx.compose.foundation.isSystemInDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfAttendanceBottomSheet(
    audioCode: String?,
    onDismissRequest: () -> Unit,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val containerBg = if (isDark) Color(0xFF111827) else MaterialTheme.colorScheme.surface
    val cardBg = if (isDark) Color(0xFF1F2937) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val primaryText = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
    val secondaryText = if (isDark) Color(0xFF9CA3AF) else MaterialTheme.colorScheme.onSurfaceVariant
    val headerPillBg = if (isDark) Color(0xFF374151) else MaterialTheme.colorScheme.outlineVariant

    /* =========================================================================
       OLD AUDIO PROXIMITY ATTENDANCE STATE & ANIMATION LOGIC (COMMENTED OUT)
       =========================================================================
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(3) }

    val infiniteTransition = rememberInfiniteTransition(label = "rippleTransition")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveScale"
    )

    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveAlpha"
    )
    ========================================================================= */

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = containerBg,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Indicator Pill
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(headerPillBg)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Glowing Icon Badge
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AppColors.EmeraldGreen.copy(alpha = 0.25f),
                                AppColors.EmeraldGreen.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        color = AppColors.EmeraldGreen.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.Sparkles,
                    contentDescription = "Upcoming Feature",
                    tint = AppColors.EmeraldGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Badge Tag
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                border = BorderStroke(0.8.dp, AppColors.EmeraldGreen.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "🚀 UPCOMING FEATURE",
                    color = AppColors.EmeraldGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title
            Text(
                text = "Staff Self Attendance",
                color = primaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "स्टाफ स्व-उपस्थिति सुविधा",
                color = secondaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Description Card
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "यह सुविधा जल्द ही लॉन्च होगी!",
                        color = primaryText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "इसके माध्यम से संस्थान के सभी स्टाफ सदस्य अपनी उपस्थिति आसानी से दर्ज कर सकेंगे। वर्तमान में इस फीचर में कुछ आवश्यक सुधार किए जा रहे हैं।",
                        color = secondaryText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Dismiss Button
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Got It",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* =========================================================================
               OLD AUDIO ATTENDANCE UI (COMMENTED OUT FOR FUTURE REFERENCE)
               =========================================================================
            // Title
            Text(
                text = "Audio Proximity Attendance",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ध्वनि द्वारा उपस्थिति दर्ज करें",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Pulse / Ripple Wave Animation Area
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .scale(waveScale)
                            .clip(CircleShape)
                            .background(Color(0xFF3B82F6).copy(alpha = waveAlpha))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = if (isSuccess) listOf(Color(0xFF10B981), Color(0xFF059669))
                                else if (isPlaying) listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                                else listOf(Color(0xFF1F2937), Color(0xFF111827))
                            )
                        )
                        .border(
                            2.dp,
                            if (isSuccess) Color(0xFF10B981) else if (isPlaying) Color(0xFF60A5FA) else Color(0xFF374151),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Lucide.Check else Lucide.Volume2,
                        contentDescription = "Audio Wave",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Push Attendance Action Button
            Button(
                onClick = {
                    if (!isPlaying) {
                        isPlaying = true
                        isSuccess = false
                        countdownSeconds = 3

                        coroutineScope.launch {
                            triggerVibration(context)

                            val timerJob = launch {
                                for (sec in 3 downTo 1) {
                                    countdownSeconds = sec
                                    kotlinx.coroutines.delay(1000)
                                }
                            }

                            val activeCode = audioCode ?: "1001"
                            val played = AudioBeaconGenerator.playBeaconTone(activeCode, 3000)
                            timerJob.cancel()

                            isPlaying = false
                            if (played) {
                                isSuccess = true
                                triggerVibration(context)
                                kotlinx.coroutines.delay(1500)
                                onDismissRequest()
                            }
                        }
                    }
                },
                enabled = !isPlaying,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    disabledContainerColor = Color(0xFF1D4ED8)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(
                    imageVector = Lucide.Volume2,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPlaying) "Transmitting Sound Signal..." else if (isSuccess) "Completed ✓" else "Push Audio Attendance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            ========================================================================= */
        }
    }
}

/* =========================================================================
   OLD VIBRATION HELPER FUNCTION (COMMENTED OUT)
   =========================================================================
private fun triggerVibration(context: Context) {
    try {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        }
    } catch (e: Exception) {
        // Ignore
    }
}
========================================================================= */
