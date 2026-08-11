package com.vidyasetuai.feature_auth.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.KeyRound
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.vidyasetuai.core.network.NetworkErrorMapper
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.ui.components.VidyaSetuFieldState
import com.vidyasetuai.core.ui.components.VidyaSetuInputField
import com.vidyasetuai.core.ui.components.VidyaSetuPrimaryButton
import com.vidyasetuai.core.ui.components.getEmailValidationState
import com.vidyasetuai.core.ui.components.getPasswordValidationState
import com.vidyasetuai.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.launch

enum class ForgotPasswordStep {
    ENTER_EMAIL,
    ENTER_OTP,
    ENTER_NEW_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordBottomSheet(
    authRepository: AuthRepository,
    initialEmail: String = "",
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf(ForgotPasswordStep.ENTER_EMAIL) }
    var email by remember { mutableStateOf(initialEmail) }
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    val isEmailValid = getEmailValidationState(email) == VidyaSetuFieldState.Valid
    val isPasswordValid = getPasswordValidationState(newPassword) == VidyaSetuFieldState.Valid
    val isConfirmValid = confirmPassword.length >= 6 && confirmPassword == newPassword

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ForgotPasswordStepTransition"
            ) { step ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (step) {
                        ForgotPasswordStep.ENTER_EMAIL -> {
                            Text(
                                text = "Reset Password",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enter your registered email address to receive a 8-digit OTP code.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            VidyaSetuInputField(
                                value = email,
                                onValueChange = { email = it },
                                hint = "Email address",
                                leadingIcon = Lucide.Mail,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                validationState = if (showErrors && !isEmailValid) VidyaSetuFieldState.Invalid else getEmailValidationState(email),
                                errorText = if (showErrors && !isEmailValid) "Please enter a valid email address" else null
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            VidyaSetuPrimaryButton(
                                text = if (isLoading) "Sending OTP..." else "Send OTP",
                                enabled = !isLoading,
                                onClick = {
                                    if (!isEmailValid) {
                                        showErrors = true
                                        onError("Please enter a valid email address.")
                                    } else {
                                        showErrors = false
                                        scope.launch {
                                            isLoading = true
                                            val res = authRepository.sendPasswordResetOtp(email)
                                            isLoading = false
                                            res.fold(
                                                onSuccess = {
                                                    currentStep = ForgotPasswordStep.ENTER_OTP
                                                },
                                                onFailure = { err ->
                                                    val cleanMsg = NetworkErrorMapper.parseErrorMessage(err)
                                                    onError(cleanMsg)
                                                }
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.width(180.dp)
                            )
                        }

                        ForgotPasswordStep.ENTER_OTP -> {
                            Text(
                                text = "Verify 8-Character OTP",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "We sent an 8-character OTP code to $email",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            VidyaSetuInputField(
                                value = otpCode,
                                onValueChange = { if (it.length <= 8) otpCode = it },
                                hint = "Enter 8-character OTP",
                                leadingIcon = Lucide.KeyRound,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Done
                                ),
                                validationState = if (showErrors && otpCode.length < 8) VidyaSetuFieldState.Invalid else VidyaSetuFieldState.Idle,
                                errorText = if (showErrors && otpCode.length < 8) "Please enter complete 8-character OTP" else null
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            VidyaSetuPrimaryButton(
                                text = if (isLoading) "Verifying OTP..." else "Verify OTP",
                                enabled = !isLoading && otpCode.trim().length == 8,
                                onClick = {
                                    if (otpCode.trim().length < 8) {
                                        showErrors = true
                                        onError("Please enter the complete 8-character OTP code.")
                                    } else {
                                        showErrors = false
                                        scope.launch {
                                            isLoading = true
                                            val res = authRepository.verifyRecoveryOtp(email, otpCode)
                                            isLoading = false
                                            res.fold(
                                                onSuccess = {
                                                    currentStep = ForgotPasswordStep.ENTER_NEW_PASSWORD
                                                },
                                                onFailure = { err ->
                                                    val cleanMsg = NetworkErrorMapper.parseErrorMessage(err)
                                                    onError(cleanMsg)
                                                }
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.width(180.dp)
                            )
                        }

                        ForgotPasswordStep.ENTER_NEW_PASSWORD -> {
                            Text(
                                text = "Set New Password",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create a strong new password for your account.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            VidyaSetuInputField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                hint = "New Password",
                                leadingIcon = Lucide.Lock,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                validationState = if (showErrors && !isPasswordValid) VidyaSetuFieldState.Invalid else getPasswordValidationState(newPassword),
                                isPassword = true,
                                passwordVisible = newPasswordVisible,
                                onPasswordToggle = { newPasswordVisible = !newPasswordVisible },
                                errorText = if (showErrors && !isPasswordValid) "Password must be at least 6 characters" else null
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            VidyaSetuInputField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                hint = "Confirm New Password",
                                leadingIcon = Lucide.Lock,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                validationState = if (showErrors && !isConfirmValid) VidyaSetuFieldState.Invalid else VidyaSetuFieldState.Idle,
                                isPassword = true,
                                passwordVisible = newPasswordVisible,
                                onPasswordToggle = { newPasswordVisible = !newPasswordVisible },
                                errorText = if (showErrors && !isConfirmValid) "Passwords do not match" else null
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            VidyaSetuPrimaryButton(
                                text = if (isLoading) "Updating..." else "Update Password",
                                enabled = !isLoading,
                                onClick = {
                                    if (!isPasswordValid || !isConfirmValid) {
                                        showErrors = true
                                        onError("Please ensure both passwords match and are at least 6 characters.")
                                    } else {
                                        showErrors = false
                                        scope.launch {
                                            isLoading = true
                                            val res = authRepository.updatePassword(newPassword)
                                            isLoading = false
                                            res.fold(
                                                onSuccess = {
                                                    onSuccess(email)
                                                    onDismiss()
                                                },
                                                onFailure = { err ->
                                                    val cleanMsg = NetworkErrorMapper.parseErrorMessage(err)
                                                    onError(cleanMsg)
                                                }
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.width(200.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
