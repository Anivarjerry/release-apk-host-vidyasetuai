package com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object StandeePosterExporter {

    /**
     * Renders a high-resolution 1080 x 1560 printable poster bitmap.
     * Matches the 1:1 design of the Web App QR Standee Poster.
     */
    fun generateStandeePosterBitmap(
        storeName: String,
        storeSlug: String,
        address: String,
        phone: String,
        qrBitmap: Bitmap
    ): Bitmap {
        val width = 1080
        val height = 1560
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background Fill (Very light mint white)
        val bgPaint = Paint().apply {
            color = Color.parseColor("#F0FDF4") // emerald-50
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. White Card Container with Emerald Border
        val cardMargin = 40f
        val cardRect = RectF(cardMargin, cardMargin, width - cardMargin, height - cardMargin)
        val cardBgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val cardBorderPaint = Paint().apply {
            color = Color.parseColor("#10B981")
            style = Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        val cornerRadius = 48f
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, cardBgPaint)
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, cardBorderPaint)

        // 3. Top Emerald Pill ("✨ SCAN & ORDER DIRECTLY FROM TABLE")
        val pillPaint = Paint().apply {
            color = Color.parseColor("#10B981")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val pillRect = RectF(220f, 100f, width - 220f, 170f)
        canvas.drawRoundRect(pillRect, 35f, 35f, pillPaint)

        val pillTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            letterSpacing = 0.05f
        }
        canvas.drawText("SCAN & ORDER DIRECTLY FROM TABLE", width / 2f, 146f, pillTextPaint)

        // 4. Store Name (Large Bold Uppercase)
        val titlePaint = Paint().apply {
            color = Color.parseColor("#0F172A") // slate-900
            textSize = 52f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val displayTitle = if (storeName.length > 28) storeName.take(28) + "..." else storeName
        canvas.drawText(displayTitle.uppercase(), width / 2f, 260f, titlePaint)

        // 5. Store Address
        val addressPaint = Paint().apply {
            color = Color.parseColor("#475569") // slate-600
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val displayAddress = if (address.length > 55) address.take(55) + "..." else address
        canvas.drawText("📍 $displayAddress", width / 2f, 310f, addressPaint)

        // 6. QR Code Outer Container Box (White with 8px Emerald Border)
        val qrBoxSize = 580f
        val qrBoxLeft = (width - qrBoxSize) / 2f
        val qrBoxTop = 380f
        val qrBoxRect = RectF(qrBoxLeft, qrBoxTop, qrBoxLeft + qrBoxSize, qrBoxTop + qrBoxSize)

        val qrBoxBorderPaint = Paint().apply {
            color = Color.parseColor("#10B981")
            style = Paint.Style.STROKE
            strokeWidth = 10f
            isAntiAlias = true
        }
        canvas.drawRoundRect(qrBoxRect, 36f, 36f, cardBgPaint)
        canvas.drawRoundRect(qrBoxRect, 36f, 36f, qrBoxBorderPaint)

        // Draw the QR Code inside
        val qrPadding = 30f
        val qrDrawRect = Rect(
            (qrBoxLeft + qrPadding).toInt(),
            (qrBoxTop + qrPadding).toInt(),
            (qrBoxLeft + qrBoxSize - qrPadding).toInt(),
            (qrBoxTop + qrBoxSize - qrPadding).toInt()
        )
        canvas.drawBitmap(qrBitmap, null, qrDrawRect, Paint(Paint.FILTER_BITMAP_FLAG))

        // 7. Slug Pill Badge at bottom of QR box
        val slugPillPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val slugBorderPaint = Paint().apply {
            color = Color.parseColor("#34D399")
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        val slugPillRect = RectF(width / 2f - 200f, qrBoxTop + qrBoxSize - 30f, width / 2f + 200f, qrBoxTop + qrBoxSize + 30f)
        canvas.drawRoundRect(slugPillRect, 30f, 30f, slugPillPaint)
        canvas.drawRoundRect(slugPillRect, 30f, 30f, slugBorderPaint)

        val slugTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 24f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(storeSlug, width / 2f, qrBoxTop + qrBoxSize + 10f, slugTextPaint)

        // 8. Instructions Section ("NO APP DOWNLOAD REQUIRED ⚡")
        val noAppPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("NO APP DOWNLOAD REQUIRED ⚡", width / 2f, 1070f, noAppPaint)

        val stepPaint = Paint().apply {
            color = Color.parseColor("#475569")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("1. Open Phone Camera 📷 & Scan QR Code above", width / 2f, 1130f, stepPaint)
        canvas.drawText("2. Select Delicious Food/Items & Place Order in 30 Seconds!", width / 2f, 1180f, stepPaint)

        // 9. Divider Line
        val dividerPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            strokeWidth = 3f
        }
        canvas.drawLine(cardMargin + 40f, 1360f, width - cardMargin - 40f, 1360f, dividerPaint)

        // 10. Bottom Footer (Phone Number & Powered by VidyaSetu AI)
        val footerPhonePaint = Paint().apply {
            color = Color.parseColor("#334155")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        canvas.drawText("📞 Ph: $phone", cardMargin + 50f, 1420f, footerPhonePaint)

        val footerBrandPaint = Paint().apply {
            color = Color.parseColor("#059669") // emerald-600
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText("🛡️ Powered by VidyaSetu AI", width - cardMargin - 50f, 1420f, footerBrandPaint)

        return bitmap
    }

    /**
     * Saves poster bitmap to Android Device Gallery (Pictures/VidyaSetuStore).
     */
    fun savePosterToGallery(
        context: Context,
        bitmap: Bitmap,
        filename: String = "VidyaSetu_Standee_${System.currentTimeMillis()}.png"
    ): Boolean {
        return try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/VidyaSetuStore")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Shares the poster image and link via Android Share Sheet.
     */
    fun shareStandeePoster(
        context: Context,
        bitmap: Bitmap,
        storeName: String,
        storeUrl: String
    ) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "standee_poster.png")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "$storeName - QR Standee Poster")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Order online directly from our digital menu here: $storeUrl\nScan QR Code to order!"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Standee Poster"))
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to text sharing
            val textIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Visit our digital shop & menu here: $storeUrl")
            }
            context.startActivity(Intent.createChooser(textIntent, "Share Store Link"))
        }
    }
}
