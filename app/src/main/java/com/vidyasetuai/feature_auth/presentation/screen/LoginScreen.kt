package com.vidyasetuai.feature_auth.presentation.screen

import android.widget.Toast
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import com.composables.icons.lucide.Fingerprint
import com.composables.icons.lucide.Headphones
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.vidyasetuai.R
import com.vidyasetuai.core.auth.SessionManager
import com.vidyasetuai.core.network.NetworkErrorMapper
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.core.security.BiometricAuthManager
import com.vidyasetuai.core.security.CryptoManager
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.ui.components.AboutScreen
import com.vidyasetuai.core.ui.components.TermsScreen
import com.vidyasetuai.core.ui.components.ToastMessage
import com.vidyasetuai.core.ui.components.ToastType
import com.vidyasetuai.core.ui.components.VidyaSetuFieldState
import com.vidyasetuai.core.ui.components.VidyaSetuInputField
import com.vidyasetuai.core.ui.components.VidyaSetuPrimaryButton
import com.vidyasetuai.core.ui.components.VidyaSetuTopToast
import com.vidyasetuai.core.ui.components.getEmailValidationState
import com.vidyasetuai.core.ui.components.getPasswordValidationState
import com.vidyasetuai.feature_auth.data.repository.TrustedDeviceRepository
import com.vidyasetuai.feature_auth.domain.repository.AuthRepository
import com.vidyasetuai.feature_auth.presentation.component.ForgotPasswordBottomSheet
import com.vidyasetuai.feature_auth.presentation.component.HelpAndLegalBottomSheet
import com.vidyasetuai.feature_auth.presentation.component.LoginByOtpBottomSheet
import com.vidyasetuai.feature_auth.presentation.component.QuickLoginAccountSelectorSheet
import com.composables.icons.lucide.Trash2
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    sessionManager: SessionManager? = null,
    trustedDeviceRepository: TrustedDeviceRepository? = null,
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
    var toastMessage by remember { mutableStateOf<ToastMessage?>(null) }
    
    var isLoading by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }
    var showForgotPasswordSheet by remember { mutableStateOf(false) }
    var showLoginOtpSheet by remember { mutableStateOf(false) }
    var showHelpSheet by remember { mutableStateOf(false) }
    var showTermsScreen by remember { mutableStateOf(false) }
    var showAboutScreen by remember { mutableStateOf(false) }
    var showAccountDeletionDialog by remember { mutableStateOf(false) }

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

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 44.dp)
                .graphicsLayer(alpha = alpha, translationY = translateY),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Top Header Bar with Help & Support Icon ──────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.1f))
                        .clickable { showHelpSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Headphones,
                        contentDescription = "Help & Legal Support",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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

            // ── Email Field ───────────────────────────────────────────
            VidyaSetuInputField(
                value = email,
                onValueChange = { 
                    email = it 
                    if (showErrors) showErrors = false
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
                    if (showErrors) showErrors = false
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

            // ── Forgot Password Button (Right Aligned) ───────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { showForgotPasswordSheet = true },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = AppColors.EmeraldGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Login Button (Always Enabled / Green) ──────────────────
            VidyaSetuPrimaryButton(
                text = if (isLoading) "Login..." else "Login",
                enabled = !isLoading,
                onClick = {
                    if (!isEmailValid || !isPasswordValid) {
                        showErrors = true
                        toastMessage = ToastMessage(
                            title = "Invalid Input",
                            description = "Please check your email and password format.",
                            type = ToastType.Warning
                        )
                        showToast = true
                    } else {
                        showErrors = false
                        scope.launch {
                            isLoading = true
                            val result = authRepository.signIn(email, password)
                            isLoading = false
                            result.fold(
                                onSuccess = {
                                    val parsedName = email.substringBefore("@")
                                        .split(".", "_", "-")
                                        .joinToString(" ") { part ->
                                            part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                        }
                                    toastMessage = ToastMessage(
                                        title = "Welcome Back, $parsedName",
                                        description = "Successfully signed in to VidyaSetu AI",
                                        type = ToastType.Success
                                    )
                                    showToast = true

                                    // Auto register device key in Supabase & Local Session on successful email login
                                    sessionManager?.let { sm ->
                                        val userId = sm.getUserId() ?: ""
                                        if (userId.isNotEmpty()) {
                                            CryptoManager.generateOrGetKeyPair(userId)
                                            val currentSession = SupabaseClient.client.auth.currentSessionOrNull()
                                            val freshRefreshToken = currentSession?.refreshToken ?: sm.getRefreshToken() ?: ""
                                            android.util.Log.d("VidyaSetu_Login", "Manual login onSuccess: saving trusted account email='${email.trim()}', passLength=${password.trim().length}")
                                            sm.saveTrustedAccount(email = email.trim(), userId = userId, password = password.trim(), refreshToken = freshRefreshToken)
                                            scope.launch {
                                                trustedDeviceRepository?.registerDevicePublicKey(userId, email.trim())
                                            }
                                        }
                                    }

                                    scope.launch {
                                        delay(2200)
                                        showToast = false
                                        onLoginSuccess?.invoke()
                                    }
                                },
                                onFailure = { error ->
                                    val userFriendlyMsg = NetworkErrorMapper.parseErrorMessage(error)
                                    toastMessage = ToastMessage(
                                        title = "Login Failed",
                                        description = userFriendlyMsg,
                                        type = ToastType.Error
                                    )
                                    showToast = true
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.width(160.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── OR Divider ─────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material3.Divider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Text(
                    text = "  OR  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                androidx.compose.material3.Divider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Quick Options Row (Side-by-Side 50-50 Split) ───────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Biometric Quick Login (Left 50%)
                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        val sm = sessionManager ?: return@OutlinedButton
                        val trustedAccounts = sm.getTrustedAccounts()
                        val fragmentActivity = context as? androidx.fragment.app.FragmentActivity

                        if (trustedAccounts.isEmpty()) {
                            toastMessage = ToastMessage(
                                title = "Quick Login Unavailable",
                                description = "No trusted device registered. Please log in with Email & Password first.",
                                type = ToastType.Warning
                            )
                            showToast = true
                            return@OutlinedButton
                        }

                        if (trustedAccounts.size == 1) {
                            val acc = trustedAccounts.first()
                            if (fragmentActivity != null) {
                                scope.launch {
                                    isLoading = true
                                    val authRes = BiometricAuthManager.authenticate(
                                        activity = fragmentActivity,
                                        title = "Quick Login",
                                        subtitle = "Scan fingerprint to access ${acc.email}"
                                    )
                                    if (authRes.isSuccess) {
                                        val pass = acc.password.trim()
                                        android.util.Log.d("VidyaSetu_Login", "Quick Login single account: email='${acc.email}', passLength=${pass.length}")
                                        if (pass.isNotEmpty()) {
                                            val loginRes = authRepository.signIn(acc.email.trim(), pass)
                                            if (loginRes.isSuccess) {
                                                val currentSession = SupabaseClient.client.auth.currentSessionOrNull()
                                                val newRefreshToken = currentSession?.refreshToken ?: ""
                                                sm.saveTrustedAccount(email = acc.email, userId = acc.userId, password = pass, refreshToken = newRefreshToken)
                                                val parsedName = acc.email.substringBefore("@")
                                                toastMessage = ToastMessage(
                                                    title = "Welcome Back, $parsedName",
                                                    description = "Successfully signed in via Biometric Quick Login",
                                                    type = ToastType.Success
                                                )
                                                showToast = true
                                                delay(1500)
                                                showToast = false
                                                onLoginSuccess?.invoke()
                                            } else {
                                                val userFriendlyMsg = NetworkErrorMapper.parseErrorMessage(loginRes.exceptionOrNull())
                                                toastMessage = ToastMessage(
                                                    title = "Quick Login Failed",
                                                    description = userFriendlyMsg,
                                                    type = ToastType.Error
                                                )
                                                showToast = true
                                            }
                                        } else {
                                            // Passwordless OTP account -> Hardware RSA Signature + RPC verification
                                            var loginSuccessful = false
                                            var errorDetails: String? = null
                                            if (trustedDeviceRepository != null) {
                                                val sigRes = trustedDeviceRepository.authenticateWithHardwareSignature(acc.email, acc.userId)
                                                if (sigRes.isSuccess) {
                                                    val otpCode = sigRes.getOrNull()!!
                                                    val verifyRes = authRepository.signIn(acc.email.trim(), otpCode)
                                                    if (verifyRes.isSuccess) {
                                                        loginSuccessful = true
                                                    } else {
                                                        errorDetails = NetworkErrorMapper.parseErrorMessage(verifyRes.exceptionOrNull())
                                                    }
                                                } else {
                                                    errorDetails = NetworkErrorMapper.parseErrorMessage(sigRes.exceptionOrNull())
                                                }
                                            }

                                            if (!loginSuccessful && errorDetails == null) {
                                                val storedToken = acc.refreshToken.ifEmpty { sm.getRefreshToken() ?: "" }
                                                if (storedToken.isNotEmpty()) {
                                                    try {
                                                        val refreshed = SupabaseClient.client.auth.refreshSession(storedToken)
                                                        sm.saveSession(acc.userId, acc.email, refreshed.accessToken, refreshed.refreshToken)
                                                        loginSuccessful = true
                                                    } catch (e: Exception) {
                                                        android.util.Log.w("VidyaSetu_Login", "Refresh fallback error: ${e.message}")
                                                    }
                                                }
                                            }

                                            if (loginSuccessful) {
                                                val parsedName = acc.email.substringBefore("@")
                                                toastMessage = ToastMessage(
                                                    title = "Welcome Back, $parsedName",
                                                    description = "Successfully signed in via Biometric Security",
                                                    type = ToastType.Success
                                                )
                                                showToast = true
                                                delay(1500)
                                                showToast = false
                                                onLoginSuccess?.invoke()
                                            } else {
                                                toastMessage = ToastMessage(
                                                    title = "Quick Login Failed",
                                                    description = errorDetails ?: "Biometric verification failed. Please log in with Email OTP once.",
                                                    type = ToastType.Error
                                                )
                                                showToast = true
                                            }
                                        }
                                    } else {
                                        val biometricError = authRes.exceptionOrNull()?.message ?: "Biometric authentication cancelled"
                                        toastMessage = ToastMessage(
                                            title = "Biometric Cancelled",
                                            description = biometricError,
                                            type = ToastType.Info
                                        )
                                        showToast = true
                                    }
                                    isLoading = false
                                }
                            }
                        } else {
                            showAccountSheet = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.EmeraldGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Fingerprint,
                            contentDescription = "Quick Biometric Login",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Biometric",
                            color = AppColors.EmeraldGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Email OTP Login (Right 50%)
                androidx.compose.material3.OutlinedButton(
                    onClick = { showLoginOtpSheet = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.EmeraldGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Mail,
                            contentDescription = "Login with Email OTP",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Email OTP",
                            color = AppColors.EmeraldGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

        // Top Floating Custom Toast (Replaces inline errors completely)
        VidyaSetuTopToast(
            visible = showToast,
            message = toastMessage,
            onDismiss = { showToast = false },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    if (showForgotPasswordSheet) {
        ForgotPasswordBottomSheet(
            authRepository = authRepository,
            initialEmail = email,
            onDismiss = { showForgotPasswordSheet = false },
            onSuccess = { updatedEmail ->
                email = updatedEmail
                toastMessage = ToastMessage(
                    title = "Password Updated",
                    description = "🎉 Password reset successfully! Please log in with your new password.",
                    type = ToastType.Success
                )
                showToast = true
            },
            onError = { errorMsg ->
                toastMessage = ToastMessage(
                    title = "Password Recovery",
                    description = errorMsg,
                    type = ToastType.Error
                )
                showToast = true
            }
        )
    }

    if (showLoginOtpSheet) {
        LoginByOtpBottomSheet(
            authRepository = authRepository,
            initialEmail = email,
            onDismiss = { showLoginOtpSheet = false },
            onSuccess = { loggedInEmail ->
                email = loggedInEmail
                toastMessage = ToastMessage(
                    title = "Welcome Back",
                    description = "🎉 Logged in successfully via Email OTP!",
                    type = ToastType.Success
                )
                showToast = true
                scope.launch {
                    val activeUserId = sessionManager?.getUserId() ?: ""
                    if (activeUserId.isNotEmpty()) {
                        try {
                            trustedDeviceRepository?.registerDevicePublicKey(activeUserId, loggedInEmail.trim())
                            android.util.Log.d("VidyaSetu_Login", "Device public key auto-registered on OTP login for user: $activeUserId")
                        } catch (e: Exception) {
                            android.util.Log.w("VidyaSetu_Login", "Failed to register public key on OTP login: ${e.message}")
                        }
                    }
                    delay(1500)
                    showToast = false
                    onLoginSuccess?.invoke()
                }
            },
            onError = { errorMsg ->
                toastMessage = ToastMessage(
                    title = "Login Failed",
                    description = errorMsg,
                    type = ToastType.Error
                )
                showToast = true
            }
        )
    }

    if (showAccountSheet && sessionManager != null) {
        val fragmentActivity = context as? androidx.fragment.app.FragmentActivity
        QuickLoginAccountSelectorSheet(
            accounts = sessionManager.getTrustedAccounts(),
            onAccountSelected = { acc ->
                showAccountSheet = false
                if (fragmentActivity != null) {
                    scope.launch {
                        isLoading = true
                        val authRes = BiometricAuthManager.authenticate(
                            activity = fragmentActivity,
                            title = "Quick Login",
                            subtitle = "Scan fingerprint to access ${acc.email}"
                        )
                        if (authRes.isSuccess) {
                            val pass = acc.password.trim()
                            android.util.Log.d("VidyaSetu_Login", "Quick Login sheet account: email='${acc.email}', passLength=${pass.length}")
                            if (pass.isNotEmpty()) {
                                val loginRes = authRepository.signIn(acc.email.trim(), pass)
                                if (loginRes.isSuccess) {
                                    val currentSession = SupabaseClient.client.auth.currentSessionOrNull()
                                    val newRefreshToken = currentSession?.refreshToken ?: ""
                                    sessionManager.saveTrustedAccount(email = acc.email, userId = acc.userId, password = pass, refreshToken = newRefreshToken)
                                    val parsedName = acc.email.substringBefore("@")
                                    toastMessage = ToastMessage(
                                        title = "Welcome Back, $parsedName",
                                        description = "Successfully signed in via Biometric Quick Login",
                                        type = ToastType.Success
                                    )
                                    showToast = true
                                    delay(1500)
                                    showToast = false
                                    onLoginSuccess?.invoke()
                                } else {
                                    val userFriendlyMsg = NetworkErrorMapper.parseErrorMessage(loginRes.exceptionOrNull())
                                    toastMessage = ToastMessage(
                                        title = "Quick Login Failed",
                                        description = userFriendlyMsg,
                                        type = ToastType.Error
                                    )
                                    showToast = true
                                }
                            } else {
                                // Passwordless OTP account -> Hardware RSA Signature + RPC verification
                                var loginSuccessful = false
                                var errorDetails: String? = null
                                if (trustedDeviceRepository != null) {
                                    val sigRes = trustedDeviceRepository.authenticateWithHardwareSignature(acc.email, acc.userId)
                                    if (sigRes.isSuccess) {
                                        val otpCode = sigRes.getOrNull()!!
                                        val verifyRes = authRepository.signIn(acc.email.trim(), otpCode)
                                        if (verifyRes.isSuccess) {
                                            loginSuccessful = true
                                        } else {
                                            errorDetails = NetworkErrorMapper.parseErrorMessage(verifyRes.exceptionOrNull())
                                        }
                                    } else {
                                        errorDetails = NetworkErrorMapper.parseErrorMessage(sigRes.exceptionOrNull())
                                    }
                                }

                                if (!loginSuccessful && errorDetails == null) {
                                    val storedToken = acc.refreshToken.ifEmpty { sessionManager.getRefreshToken() ?: "" }
                                    if (storedToken.isNotEmpty()) {
                                        try {
                                            val refreshed = SupabaseClient.client.auth.refreshSession(storedToken)
                                            sessionManager.saveSession(acc.userId, acc.email, refreshed.accessToken, refreshed.refreshToken)
                                            loginSuccessful = true
                                        } catch (e: Exception) {
                                            android.util.Log.w("VidyaSetu_Login", "Refresh fallback error: ${e.message}")
                                        }
                                    }
                                }

                                if (loginSuccessful) {
                                    val parsedName = acc.email.substringBefore("@")
                                    toastMessage = ToastMessage(
                                        title = "Welcome Back, $parsedName",
                                        description = "Successfully signed in via Biometric Security",
                                        type = ToastType.Success
                                    )
                                    showToast = true
                                    delay(1500)
                                    showToast = false
                                    onLoginSuccess?.invoke()
                                } else {
                                    toastMessage = ToastMessage(
                                        title = "Quick Login Failed",
                                        description = errorDetails ?: "Biometric verification failed. Please log in with Email OTP once.",
                                        type = ToastType.Error
                                    )
                                    showToast = true
                                }
                            }
                        } else {
                            val biometricError = authRes.exceptionOrNull()?.message ?: "Biometric authentication cancelled"
                            toastMessage = ToastMessage(
                                title = "Biometric Cancelled",
                                description = biometricError,
                                type = ToastType.Info
                            )
                            showToast = true
                        }
                        isLoading = false
                    }
                }
            },
            onDismiss = { showAccountSheet = false }
        )
    }

    if (showHelpSheet) {
        HelpAndLegalBottomSheet(
            onDismiss = { showHelpSheet = false },
            onOpenTerms = { showTermsScreen = true },
            onOpenAbout = { showAboutScreen = true },
            onRequestAccountDeletion = { showAccountDeletionDialog = true }
        )
    }

    if (showTermsScreen) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showTermsScreen = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            TermsScreen(onBack = { showTermsScreen = false })
        }
    }

    if (showAboutScreen) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showAboutScreen = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AboutScreen(onBack = { showAboutScreen = false })
        }
    }

    if (showAccountDeletionDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAccountDeletionDialog = false },
            icon = {
                Icon(
                    imageVector = Lucide.Trash2,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color(0xFFEF4444)
                )
            },
            title = {
                Text(
                    text = "Request Account Deletion",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Google Play Policy requires an unauthenticated option to request deletion of your account and associated data.\n\nSubmitting this request will launch your email client with a pre-filled deletion request to support@vidyasetu.ai.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showAccountDeletionDialog = false
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@vidyasetu.ai")
                                putExtra(Intent.EXTRA_SUBJECT, "Account Deletion Request - VidyaSetu AI")
                                putExtra(Intent.EXTRA_TEXT, "Hello Support Team,\n\nI would like to request the permanent deletion of my account and associated data.\n\nRegistered Email / Phone: \nReason (Optional): ")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open email client", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFFEF4444))
                ) {
                    Text("Proceed via Email", color = androidx.compose.ui.graphics.Color.White)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showAccountDeletionDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
