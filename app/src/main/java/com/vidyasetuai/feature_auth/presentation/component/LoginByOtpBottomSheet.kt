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
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.vidyasetuai.core.network.NetworkErrorMapper
import com.vidyasetuai.core.ui.components.VidyaSetuFieldState
import com.vidyasetuai.core.ui.components.VidyaSetuInputField
import com.vidyasetuai.core.ui.components.VidyaSetuPrimaryButton
import com.vidyasetuai.core.ui.components.getEmailValidationState
import com.vidyasetuai.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.launch

enum class LoginOtpStep {
    ENTER_EMAIL,
    ENTER_OTP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginByOtpBottomSheet(
    authRepository: AuthRepository,
    initialEmail: String = "",
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf(LoginOtpStep.ENTER_EMAIL) }
    var email by remember { mutableStateOf(initialEmail) }
    var otpCode by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    val isEmailValid = getEmailValidationState(email) == VidyaSetuFieldState.Valid

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
                label = "LoginOtpStepTransition"
            ) { step ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (step) {
                        LoginOtpStep.ENTER_EMAIL -> {
                            Text(
                                text = "Login with Email OTP",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enter your registered email address to receive a login OTP.",
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
                                text = if (isLoading) "Sending OTP..." else "Send Login OTP",
                                enabled = !isLoading,
                                onClick = {
                                    if (!isEmailValid) {
                                        showErrors = true
                                        onError("Please enter a valid email address.")
                                    } else {
                                        showErrors = false
                                        scope.launch {
                                            isLoading = true
                                            val res = authRepository.sendLoginOtp(email)
                                            isLoading = false
                                            res.fold(
                                                onSuccess = {
                                                    currentStep = LoginOtpStep.ENTER_OTP
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

                        LoginOtpStep.ENTER_OTP -> {
                            Text(
                                text = "Verify Login OTP",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "We sent an OTP code to $email",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            VidyaSetuInputField(
                                value = otpCode,
                                onValueChange = { if (it.length <= 8) otpCode = it },
                                hint = "Enter OTP code",
                                leadingIcon = Lucide.KeyRound,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Done
                                ),
                                validationState = if (showErrors && otpCode.length < 6) VidyaSetuFieldState.Invalid else VidyaSetuFieldState.Idle,
                                errorText = if (showErrors && otpCode.length < 6) "Please enter complete OTP code" else null
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            VidyaSetuPrimaryButton(
                                text = if (isLoading) "Logging in..." else "Verify & Login",
                                enabled = !isLoading && otpCode.trim().length >= 6,
                                onClick = {
                                    if (otpCode.trim().length < 6) {
                                        showErrors = true
                                        onError("Please enter the complete OTP code.")
                                    } else {
                                        showErrors = false
                                        scope.launch {
                                            isLoading = true
                                            val res = authRepository.verifyLoginOtp(email, otpCode)
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
