package com.vidyasetuai.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.vidyasetuai.core.ui.colors.AppColors
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.vidyasetuai.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

/**
 * Premium WhatsApp-style input field with icon, validation state and optional trailing icon.
 */
@Composable
fun VidyaSetuInputField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingContent: (@Composable () -> Unit)? = null,
    validationState: VidyaSetuFieldState = VidyaSetuFieldState.Idle,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    errorText: String? = null
) {
    val lineColor = when (validationState) {
        VidyaSetuFieldState.Invalid -> MaterialTheme.colorScheme.error
        else -> AppColors.EmeraldGreen
    }

    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else visualTransformation,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        ),
        cursorBrush = SolidColor(AppColors.EmeraldGreen),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = when (validationState) {
                            VidyaSetuFieldState.Invalid -> MaterialTheme.colorScheme.error
                            else -> AppColors.EmeraldGreen
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = hint,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF9E9E9E),
                                    fontSize = 16.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                    if (isPassword && onPasswordToggle != null) {
                        IconButton(
                            onClick = onPasswordToggle,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Lucide.Eye else Lucide.EyeOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        AnimatedVisibility(
                            visible = validationState == VidyaSetuFieldState.Valid,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Valid",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        AnimatedVisibility(
                            visible = validationState == VidyaSetuFieldState.Invalid,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Icon(
                                imageVector = Lucide.CircleAlert,
                                contentDescription = "Invalid",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp)
                        .background(lineColor)
                )
                if (validationState == VidyaSetuFieldState.Invalid && !errorText.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }
            }
        }
    )
}

/**
 * Validation state for input fields.
 */
enum class VidyaSetuFieldState { Idle, Focused, Valid, Invalid }

/**
 * Premium pill-shaped primary action button (Emerald Green).
 */
@Composable
fun VidyaSetuPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(21.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.EmeraldGreen,
            contentColor = AppColors.PureWhite,
            disabledContainerColor = AppColors.EmeraldGreen,
            disabledContentColor = AppColors.PureWhite.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 15.sp,
                letterSpacing = 0.3.sp
            )
        )
    }
}

/**
 * Field validation helper: live-validates email format.
 */
fun getEmailValidationState(email: String): VidyaSetuFieldState {
    if (email.isEmpty()) return VidyaSetuFieldState.Idle
    val pattern = android.util.Patterns.EMAIL_ADDRESS
    return if (pattern.matcher(email).matches()) VidyaSetuFieldState.Valid
    else VidyaSetuFieldState.Invalid
}

/**
 * Field validation helper: live-validates password (min 6 chars).
 */
fun getPasswordValidationState(password: String): VidyaSetuFieldState {
    if (password.isEmpty()) return VidyaSetuFieldState.Idle
    return if (password.length >= 6) VidyaSetuFieldState.Valid
    else VidyaSetuFieldState.Invalid
}

/**
 * Reusable branding component featuring the green bridge logo and stylized "VidyaSetu AI" text.
 */
@Composable
fun VidyaSetuLogoBranding(
    modifier: Modifier = Modifier,
    logoWidth: Dp = 150.dp,
    textSize: TextUnit = 24.sp,
    isDark: Boolean = false
) {
    // Aspect ratio of the custom bridge path is 100 : 32 = 3.125
    val logoHeight = logoWidth / 3.125f
    val textColor = if (isDark) Color.White else Color.Black

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_bridge_logo),
            contentDescription = "VidyaSetu Logo",
            tint = AppColors.EmeraldGreen,
            modifier = Modifier.size(width = logoWidth, height = logoHeight)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = textColor, fontWeight = FontWeight.Bold)) {
                    append("Vidya")
                }
                withStyle(style = SpanStyle(color = AppColors.EmeraldGreen, fontWeight = FontWeight.Bold)) {
                    append("Setu AI")
                }
            },
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp,
                fontSize = textSize
            )
        )
    }
}


/**
 * Premium reusable Instagram-style Author Header for Case Studies and Experiences.
 */
@Composable
fun AuthorHeader(
    authorName: String,
    authorUsername: String,
    authorProfilePicUrl: String?,
    isAuthorVerified: Boolean,
    authorType: String?,
    authorUserId: String?,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlatform = authorType == "platform" || authorUserId == null || authorUsername == "vidyasetu"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isPlatform) { 
                if (authorUserId != null) {
                    onAuthorClick(authorUserId)
                }
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPlatform) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F8F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bridge_logo),
                    contentDescription = "VidyaSetu Logo",
                    tint = AppColors.EmeraldGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            if (!authorProfilePicUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = authorProfilePicUrl,
                    contentDescription = "Author Avatar",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(0.5.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF2F2F7)),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = authorName.take(1).uppercase()
                    Text(
                        text = initial,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isPlatform) "VidyaSetu AI" else authorName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (isPlatform || isAuthorVerified) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = "Verified",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
            Text(
                text = "@${if (isPlatform) "vidyasetu" else authorUsername}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

