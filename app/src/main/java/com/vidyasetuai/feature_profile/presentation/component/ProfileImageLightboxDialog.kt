package com.vidyasetuai.feature_profile.presentation.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X

/**
 * Fullscreen Image Lightbox Dialog with dark backdrop and layered BackHandler (Pillar 5 AGENTS.md).
 */
@Composable
fun ProfileImageLightboxDialog(
    imageUrl: String?,
    onDismiss: () -> Unit
) {
    if (imageUrl.isNullOrBlank()) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        BackHandler(enabled = true) {
            onDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .statusBarsPadding()
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}
