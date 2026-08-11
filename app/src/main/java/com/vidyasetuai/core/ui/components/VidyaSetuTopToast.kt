package com.vidyasetuai.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import com.vidyasetuai.core.ui.colors.AppColors
import kotlinx.coroutines.delay

enum class ToastType {
    Success,
    Error,
    Warning,
    Info
}

data class ToastMessage(
    val title: String,
    val description: String? = null,
    val type: ToastType = ToastType.Info
)

@Composable
fun VidyaSetuTopToast(
    visible: Boolean,
    message: ToastMessage?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 3500L
) {
    LaunchedEffect(visible, message) {
        if (visible && message != null) {
            delay(durationMillis)
            onDismiss()
        }
    }

    val isDark = isSystemInDarkTheme()

    AnimatedVisibility(
        visible = visible && message != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn() + scaleIn(initialScale = 0.9f),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut() + scaleOut(targetScale = 0.9f),
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 12.dp)
            .zIndex(999f)
    ) {
        if (message == null) return@AnimatedVisibility

        val icon: ImageVector = when (message.type) {
            ToastType.Success -> Lucide.Check
            ToastType.Error -> Lucide.CircleAlert
            ToastType.Warning -> Lucide.TriangleAlert
            ToastType.Info -> Lucide.Info
        }

        val iconTint: Color = when (message.type) {
            ToastType.Success -> AppColors.EmeraldGreen
            ToastType.Error -> Color(0xFFEF4444)
            ToastType.Warning -> Color(0xFFF59E0B)
            ToastType.Info -> Color(0xFF3B82F6)
        }

        val borderColor: Color = when (message.type) {
            ToastType.Success -> AppColors.EmeraldGreen.copy(alpha = 0.4f)
            ToastType.Error -> Color(0xFFEF4444).copy(alpha = 0.4f)
            ToastType.Warning -> Color(0xFFF59E0B).copy(alpha = 0.4f)
            ToastType.Info -> Color(0xFF3B82F6).copy(alpha = 0.4f)
        }

        Surface(
            color = if (isDark) Color(0xFF1E1E1E) else Color.White,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(width = 1.dp, color = borderColor),
            shadowElevation = 0.dp,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .wrapContentSize()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = message.type.name,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = message.title,
                        color = if (isDark) Color.White else Color(0xFF1A202C),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!message.description.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = message.description,
                            color = if (isDark) Color(0xFF8696A0) else Color(0xFF54656F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
