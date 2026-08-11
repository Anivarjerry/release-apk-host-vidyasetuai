package com.vidyasetuai.feature_campus.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class ChatBubbleShape(val isMe: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerRadius = with(density) { 12.dp.toPx() }
        val tailWidth = with(density) { 6.dp.toPx() }
        val path = Path()

        if (isMe) {
            path.moveTo(cornerRadius, 0f)
            path.lineTo(size.width - tailWidth, 0f)
            path.lineTo(size.width, 0f)
            path.lineTo(size.width - tailWidth, cornerRadius)
            path.lineTo(size.width - tailWidth, size.height - cornerRadius)
            path.quadraticTo(
                size.width - tailWidth,
                size.height,
                size.width - tailWidth - cornerRadius,
                size.height
            )
            path.lineTo(cornerRadius, size.height)
            path.quadraticTo(0f, size.height, 0f, size.height - cornerRadius)
            path.lineTo(0f, cornerRadius)
            path.quadraticTo(0f, 0f, cornerRadius, 0f)
        } else {
            path.moveTo(tailWidth + cornerRadius, 0f)
            path.lineTo(size.width - cornerRadius, 0f)
            path.quadraticTo(size.width, 0f, size.width, cornerRadius)
            path.lineTo(size.width, size.height - cornerRadius)
            path.quadraticTo(size.width, size.height, size.width - cornerRadius, size.height)
            path.lineTo(tailWidth + cornerRadius, size.height)
            path.quadraticTo(tailWidth, size.height, tailWidth, size.height - cornerRadius)
            path.lineTo(tailWidth, cornerRadius)
            path.lineTo(0f, 0f)
            path.lineTo(tailWidth + cornerRadius, 0f)
        }

        path.close()
        return Outline.Generic(path)
    }
}

fun Modifier.whatsappWallpaper(isDark: Boolean): Modifier = this.drawBehind {
    val bgColor = if (isDark) Color(0xFF0B141A) else Color(0xFFEFEAE2)
    drawRect(color = bgColor)
}