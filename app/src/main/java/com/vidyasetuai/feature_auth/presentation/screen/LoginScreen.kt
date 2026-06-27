package com.vidyasetuai.feature_auth.presentation.screen

import android.widget.Toast
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.composables.icons.lucide.Check
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.vidyasetuai.R
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.ui.components.VidyaSetuFieldState
import com.vidyasetuai.core.ui.components.VidyaSetuInputField
import com.vidyasetuai.core.ui.components.VidyaSetuPrimaryButton
import com.vidyasetuai.core.ui.components.getEmailValidationState
import com.vidyasetuai.core.ui.components.getPasswordValidationState
import com.vidyasetuai.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onNavigateToSignUp: (() -> Unit)? = null,
    onLoginSuccess: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    
    var showToast by remember { mutableStateOf(false) }
    var loggedInUsername by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var startAnimation by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { startAnimation = true }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing),
        label = "loginAlpha"
    )
    val translateY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 18f,
        animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing),
        label = "loginTranslateY"
    )

    val isEmailValid = getEmailValidationState(email) == VidyaSetuFieldState.Valid
    val isPasswordValid = getPasswordValidationState(password) == VidyaSetuFieldState.Valid

    val emailState = when {
        showErrors && !isEmailValid -> VidyaSetuFieldState.Invalid
        emailFocused && email.isEmpty() -> VidyaSetuFieldState.Focused
        email.isEmpty() -> VidyaSetuFieldState.Idle
        else -> getEmailValidationState(email)
    }
    val passwordState = when {
        showErrors && !isPasswordValid -> VidyaSetuFieldState.Invalid
        passwordFocused && password.isEmpty() -> VidyaSetuFieldState.Focused
        password.isEmpty() -> VidyaSetuFieldState.Idle
        else -> getPasswordValidationState(password)
    }

    val emailError = when {
        showErrors && email.isEmpty() -> "Email address is required"
        showErrors && !isEmailValid -> "Please enter a valid email address"
        else -> null
    }
    val passwordError = when {
        showErrors && password.isEmpty() -> "Password is required"
        showErrors && !isPasswordValid -> "Password must be at least 6 characters"
        else -> null
    }

    val isDark = isSystemInDarkTheme()

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 44.dp)
                .graphicsLayer(alpha = alpha, translationY = translateY),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(88.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_bridge_logo),
            contentDescription = "VidyaSetu Logo",
            tint = AppColors.EmeraldGreen,
            modifier = Modifier.size(width = 80.dp, height = 26.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Welcome to VidyaSetu AI",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Error message display for API failures only
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Email Field ───────────────────────────────────────────
        VidyaSetuInputField(
            value = email,
            onValueChange = { 
                email = it 
                if (showErrors) {
                    showErrors = false
                }
            },
            hint = "Email address",
            leadingIcon = Lucide.Mail,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            validationState = emailState,
            errorText = emailError
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Password Field ────────────────────────────────────────
        VidyaSetuInputField(
            value = password,
            onValueChange = { 
                password = it 
                if (showErrors) {
                    showErrors = false
                }
            },
            hint = "Password",
            leadingIcon = Lucide.Lock,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            validationState = passwordState,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible },
            errorText = passwordError
        )

        Spacer(modifier = Modifier.height(36.dp))

        // ── Login Button (Always Enabled / Green) ──────────────────
        VidyaSetuPrimaryButton(
            text = if (isLoading) "Login..." else "Login",
            enabled = !isLoading,
            onClick = {
                if (!isEmailValid || !isPasswordValid) {
                    showErrors = true
                    errorMessage = null
                } else {
                    showErrors = false
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        val result = authRepository.signIn(email, password)
                        isLoading = false
                        result.fold(
                            onSuccess = {
                                val parsedName = email.substringBefore("@")
                                    .split(".", "_", "-")
                                    .joinToString(" ") { part ->
                                        part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                    }
                                loggedInUsername = parsedName
                                showToast = true
                                scope.launch {
                                    kotlinx.coroutines.delay(2500)
                                    showToast = false
                                    onLoginSuccess?.invoke()
                                }
                            },
                            onFailure = { error ->
                                errorMessage = error.localizedMessage ?: "Invalid email or password"
                            }
                        )
                    }
                }
            },
            modifier = Modifier.width(160.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Bottom Navigation ─────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = { if (!isLoading) onNavigateToSignUp?.invoke() },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = AppColors.EmeraldGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Custom Floating Toast
    AnimatedVisibility(
        visible = showToast,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn() + scaleIn(initialScale = 0.9f),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut() + scaleOut(targetScale = 0.9f),
        modifier = Modifier
            .align(Alignment.TopCenter)
            .statusBarsPadding()
            .padding(top = 12.dp)
            .zIndex(999f)
    ) {
        Surface(
            color = if (isDark) Color(0xFF1E1E1E) else Color.White,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (isDark) Color(0xFF333333) else Color(0xFFE2E8F0)
            ),
            shadowElevation = 6.dp,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .wrapContentSize()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Lucide.Check,
                    contentDescription = "Success",
                    tint = AppColors.EmeraldGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Welcome Back, $loggedInUsername",
                        color = if (isDark) Color.White else Color(0xFF1A202C),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Successfully signed in to VidyaSetu AI",
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
