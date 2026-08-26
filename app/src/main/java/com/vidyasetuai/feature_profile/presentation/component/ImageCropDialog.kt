package com.vidyasetuai.feature_profile.presentation.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RotateCcw
import com.composables.icons.lucide.X
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

enum class CropShape {
    CIRCLE_AVATAR, // 1:1 Aspect ratio with circular viewport
    RECT_COVER     // ~16:9 Aspect ratio with rectangular viewport
}

/**
 * Enterprise Image Cropper & Pan Adjuster with Layered BackHandler.
 * Allows user to pinch-zoom, pan, and adjust photo before saving.
 */
@Composable
fun ImageCropDialog(
    imageUri: Uri,
    cropShape: CropShape = CropShape.CIRCLE_AVATAR,
    onDismiss: () -> Unit,
    onCropConfirmed: (File) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isProcessing by remember { mutableStateOf(false) }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load original bitmap
    LaunchedEffect(imageUri) {
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(imageUri)?.use { stream ->
                    originalBitmap = BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    BackHandler(enabled = true) {
        if (!isProcessing) onDismiss()
    }

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isProcessing,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Cancel",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = if (cropShape == CropShape.CIRCLE_AVATAR) "Adjust Profile Photo" else "Adjust Cover Photo",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )

                    IconButton(
                        onClick = {
                            scale = 1f
                            offset = Offset.Zero
                        },
                        enabled = !isProcessing,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Lucide.RotateCcw,
                            contentDescription = "Reset",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Center Crop Workspace Viewport
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .onGloballyPositioned { containerSize = it.size }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.7f, 4.0f)
                                offset = Offset(
                                    x = offset.x + pan.x,
                                    y = offset.y + pan.y
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (originalBitmap != null) {
                        val bmp = originalBitmap!!
                        val imgBitmap = remember(bmp) { bmp.asImageBitmap() }

                        androidx.compose.foundation.Image(
                            bitmap = imgBitmap,
                            contentDescription = "Cropping Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                ),
                            contentScale = ContentScale.Fit
                        )

                        // Mask Overlay with transparent viewport window
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val cropRect = if (cropShape == CropShape.CIRCLE_AVATAR) {
                                val radius = (w.coerceAtMost(h) * 0.40f)
                                val cx = w / 2f
                                val cy = h / 2f
                                Rect(cx - radius, cy - radius, cx + radius, cy + radius)
                            } else {
                                val cropW = w * 0.92f
                                val cropH = cropW * (9f / 16f)
                                val cx = w / 2f
                                val cy = h / 2f
                                Rect(cx - cropW / 2f, cy - cropH / 2f, cx + cropW / 2f, cy + cropH / 2f)
                            }

                            // Dark Dimmed Scrim
                            val scrimPath = Path().apply {
                                fillType = PathFillType.EvenOdd
                                addRect(Rect(0f, 0f, w, h))
                                if (cropShape == CropShape.CIRCLE_AVATAR) {
                                    addOval(cropRect)
                                } else {
                                    addRect(cropRect)
                                }
                            }
                            drawPath(scrimPath, Color.Black.copy(alpha = 0.65f))

                            // Viewport Border Line
                            if (cropShape == CropShape.CIRCLE_AVATAR) {
                                drawCircle(
                                    color = Color.White,
                                    radius = cropRect.width / 2f,
                                    center = cropRect.center,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            } else {
                                drawRect(
                                    color = Color.White,
                                    topLeft = cropRect.topLeft,
                                    size = cropRect.size,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    } else {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                // Bottom Controls & Confirm Button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Pinch to zoom • Drag to adjust position",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    )

                    Button(
                        onClick = {
                            if (originalBitmap != null && containerSize.width > 0 && containerSize.height > 0) {
                                isProcessing = true
                                coroutineScope.launch {
                                    val croppedFile = withContext(Dispatchers.IO) {
                                        cropAndSaveBitmap(
                                            context = context,
                                            bitmap = originalBitmap!!,
                                            containerSize = containerSize,
                                            scale = scale,
                                            offset = offset,
                                            cropShape = cropShape
                                        )
                                    }
                                    isProcessing = false
                                    if (croppedFile != null) {
                                        onCropConfirmed(croppedFile)
                                    } else {
                                        onDismiss()
                                    }
                                }
                            }
                        },
                        enabled = !isProcessing && originalBitmap != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apply & Save",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Mathematically transforms and extracts the exact user-framed area as a high-quality JPEG File.
 */
private fun cropAndSaveBitmap(
    context: Context,
    bitmap: Bitmap,
    containerSize: IntSize,
    scale: Float,
    offset: Offset,
    cropShape: CropShape
): File? {
    return try {
        val targetWidth = if (cropShape == CropShape.CIRCLE_AVATAR) 800 else 1280
        val targetHeight = if (cropShape == CropShape.CIRCLE_AVATAR) 800 else 720

        val outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        canvas.drawColor(AndroidColor.TRANSPARENT)

        val cw = containerSize.width.toFloat()
        val ch = containerSize.height.toFloat()

        // 1. Calculate how Image(contentScale = Fit) is positioned inside container
        val srcW = bitmap.width.toFloat()
        val srcH = bitmap.height.toFloat()
        val fitScale = Math.min(cw / srcW, ch / srcH)
        val fittedW = srcW * fitScale
        val fittedH = srcH * fitScale

        // Center of fitted image in container coordinates
        val imgCenterX = cw / 2f + offset.x
        val imgCenterY = ch / 2f + offset.y

        // 2. Viewport window in container coordinates
        val viewportWidth: Float
        val viewportHeight: Float

        if (cropShape == CropShape.CIRCLE_AVATAR) {
            val radius = (cw.coerceAtMost(ch) * 0.40f)
            viewportWidth = radius * 2f
            viewportHeight = radius * 2f
        } else {
            viewportWidth = cw * 0.92f
            viewportHeight = viewportWidth * (9f / 16f)
        }

        val viewportCenterX = cw / 2f
        val viewportCenterY = ch / 2f

        // 3. Map container viewport back to output bitmap
        val totalScaleOnScreen = fitScale * scale
        val scaleToOutput = targetWidth / viewportWidth

        val matrix = Matrix()
        // Translate image so center of viewport aligns with center of output
        val dx = (imgCenterX - viewportCenterX) * scaleToOutput + (targetWidth / 2f) - (srcW * totalScaleOnScreen * scaleToOutput / 2f)
        val dy = (imgCenterY - viewportCenterY) * scaleToOutput + (targetHeight / 2f) - (srcH * totalScaleOnScreen * scaleToOutput / 2f)

        matrix.postScale(totalScaleOnScreen * scaleToOutput, totalScaleOnScreen * scaleToOutput)
        matrix.postTranslate(dx, dy)

        canvas.drawBitmap(bitmap, matrix, null)

        // 4. Save to persistent profile_media cache directory
        val mediaDir = File(context.filesDir, "profile_media").apply { mkdirs() }
        val prefix = if (cropShape == CropShape.CIRCLE_AVATAR) "avatar" else "cover"
        val outputFile = File(mediaDir, "${prefix}_${UUID.randomUUID().toString().take(8)}.jpg")

        FileOutputStream(outputFile).use { out ->
            outputBitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        outputBitmap.recycle()

        outputFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
